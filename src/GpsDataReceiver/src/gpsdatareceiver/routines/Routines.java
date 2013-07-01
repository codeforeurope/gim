/**
 * 
 */
package gpsdatareceiver.routines;

import gpsdatareceiver.webcontext.DataSources;
import gpsdatareceiver.webcontext.StartApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sistematica.mobiworkdb.dao.DAOFactory;
import sistematica.mobiworkdb.dao.MessageDAO;
import sistematica.mobiworkdb.dao.NodeDAO;
import sistematica.mobiworkdb.dao.PositionDAO;
import sistematica.mobiworkdb.dao.VehicleDAO;
import sistematica.mobiworkdb.entity.ObuDef;

/**
 * COmmon routines.
 * @author strozzino
 *
 */
public class Routines
{
	/**
	 * Directory where check for XML input
	 */
	private File fileInputXMLDir=null;
	
	/**
	 * Directory where move the wrong XML input
	 */
	private File fileOutputBadXMLDir=null;
	
	/**
	 * Directory where move the good XML input
	 */
	private File fileOutputGoodXMLDir=null;
	
	/**
	 * XML input filter
	 */
	private FilenameFilter inputFilter=null;
	
	/**
	 * Logger
	 */
	private static Logger log4j=Logger.getLogger(Routines.class);
	
	/**
	 * 
	 */
	final private String INPUT_END_PATTERN=".xml";

	/**
	 * DB connection
	 */
	private Connection connection=null;
	
	/**
	 * Max retry for db connection
	 */
	private int dbMaxRetry=0;
	
	/**
	 * Delay after each retry
	 */
	private int dbRetryDelay=0;
	
	/**
	 * Timeout
	 */
	private int dbTimeout = 5000;
	
	/**
	 * For batch insertion
	 */
	@SuppressWarnings("unused")
	private int dbCommitSize=0;
	
	/**
	 * XML parser
	 */
	private DOMParser parser=null;
	
	/**
	 * Application constants
	 */
    int idApplication=0;
    int idMessageType=0;
    int idMessageCategory=0;
    
    private final String CONF_KEY_DB_MAXRETRY = "gps.data.receiver.db.maxRetry";
	private final String CONF_KEY_DB_RETRY_DELAY = "gps.data.receiver.db.retry.delay";
	private final String CONF_KEY_DB_TIMEOUT = "gps.data.receiver.db.timeout";
	private final String CONF_KEY_DB_COMMIT_SIZE = "gps.data.receiver.db.commit.size";
	
	private final String CONF_KEY_MAPPING_ID_APPLICATION = "gps.data.receiver.mapping.id_application";
	private final String CONF_KEY_MAPPING_ID_MESSAGE_TYPE = "gps.data.receiver.mapping.id_message_type";
	private final String CONF_KEY_MAPPING_ID_MESSAGE_CATEGORY = "gps.data.receiver.mapping.id_message_category";
	
	private final String IO_ENCODING = "UTF-8";
	
	private enum XMLFields
	{
		stuMessage,
		ID,
		nome,
		data_posizione,
		lat,
		lon,
		alt,
		vel,
		heading,
		num_sat_used,
		num_sat_viewed;
		
		@Override
		public String toString() {
			return super.toString().replace("_", "-");
		}
	}
	
	/**
	 * Constructor
	 * @param in
	 * @param outb
	 * @param outg
	 */
	public Routines(String in, String outb, String outg)
	{
		fileInputXMLDir=new File(in);
		fileOutputBadXMLDir=new File(outb);
		fileOutputGoodXMLDir=new File(outg);
	}
	
	/**
	 * Checks and returns for input files.
	 * @return The array of input xml files.
	 */
	public File[] checkInputXmlFiles()
	{
		return fileInputXMLDir.listFiles(inputFilter);
	}
	
	/**
	 * Analyze this file
	 * @param file
	 */
	public void analyze(File file)
	{
		boolean test = true;
		int currentFileSize = 0;
		int count = 0;
		Node node = null;
        Element el = null;
        Element ell = null;
        NodeList nodeList = null;
        //List<ObuDef> defs=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        Long insertedPosId = null;
        Long idMsg = null;
        //questi due valori devono essere scalari per via delle conversioni che si devono fare dopo ...
        long tmpNodeId = 0;
        long tmpVehicleId = 0;
        boolean isFirst = true;
        
        //XML fields
        String id = null;
        String nome = null;
        String dataPosizione = null;
		String lat = null;
		String lon = null;
		String alt = null;
		String vel = null;
		String heading = null;
		String numSatUsed = null;
		String numSatViewed = null;
		
		PreparedStatement pstmt = null;
        		
		try
		{
			connection.setAutoCommit(false);
			
			parser.parse(new InputStreamReader(new FileInputStream(file), IO_ENCODING));
			XMLDocument doc = parser.getDocument();
            NodeList nodes = doc.getElementsByTagName(XMLFields.stuMessage.toString());
            currentFileSize = nodes.getLength();
            //log
            log4j.info("Found " + currentFileSize + " positions ... ");
            
            double last_lat = -999;
            double last_lon = -999;
            
            ObuDef last_obu = null;
            
            pstmt = connection.prepareStatement(insert_fcd);
            
            //for each positions in the file ...
			for(int i=0;i<currentFileSize;i++)
			{
				count++;
				node = nodes.item(i);
				el = (Element) node;
				//ID
				nodeList = el.getElementsByTagName(XMLFields.ID.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				id = ((Node) nodeList.item(0)).getNodeValue();
				//nome
				nodeList = el.getElementsByTagName(XMLFields.nome.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				nome = ((Node) nodeList.item(0)).getNodeValue();
				//data_posizione
				nodeList = el.getElementsByTagName(XMLFields.data_posizione.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				dataPosizione = ((Node) nodeList.item(0)).getNodeValue();
				//lat
				nodeList = el.getElementsByTagName(XMLFields.lat.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				lat = ((Node) nodeList.item(0)).getNodeValue();
				//lon
				nodeList = el.getElementsByTagName(XMLFields.lon.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				lon = ((Node) nodeList.item(0)).getNodeValue();
				//alt
				nodeList = el.getElementsByTagName(XMLFields.alt.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				alt = ((Node) nodeList.item(0)).getNodeValue();
				//vel
				nodeList = el.getElementsByTagName(XMLFields.vel.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				vel = ((Node) nodeList.item(0)).getNodeValue();
				//heading
				nodeList = el.getElementsByTagName(XMLFields.heading.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				heading = ((Node) nodeList.item(0)).getNodeValue();
				//num_sat_used
				nodeList = el.getElementsByTagName(XMLFields.num_sat_used.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				numSatUsed = ((Node) nodeList.item(0)).getNodeValue();
				//num_sat_viewed
				nodeList = el.getElementsByTagName(XMLFields.num_sat_viewed.toString());
				ell = (Element)nodeList.item(0);
				nodeList = ell.getChildNodes();
				numSatViewed = ((Node) nodeList.item(0)).getNodeValue();
				
				//log
				log4j.debug(id+" -- "+nome+" -- "+dataPosizione+" -- "+lat+" -- "+lon+" -- "+vel+" -- "+heading+" -- "+numSatUsed+" -- "+numSatViewed);
				
				//db
				if(!isValidConnection())
					openConnection();

				//INSERISCO IN FCD
				//id_vehicle, latitude, longitude, speed, timestamp, dir, satellites, source
				int index = 1;
				
				pstmt.setString(index++, id);
				pstmt.setInt(index++, (int) (Double.parseDouble(lat) * 1000000));
				pstmt.setInt(index++, (int) (Double.parseDouble(lon) * 1000000));
				pstmt.setInt(index++, (int) (Double.parseDouble(vel)));
				pstmt.setTimestamp(index++, new Timestamp(sdf.parse(dataPosizione).getTime()));
				pstmt.setInt(index++, Integer.parseInt(heading));
				pstmt.setInt(index++, Integer.parseInt(numSatUsed));
				pstmt.setString(index++, "P");
				
				pstmt.executeUpdate();				
			}
			
			connection.commit();
		}
		catch(Exception e) {
			log4j.error("Unable to parse file: "+file.getAbsolutePath(), e);
			
			try
			{
				if(connection != null)
					connection.rollback();
				
			}
			catch (SQLException e1)
			{}
			
			test=false;
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.setAutoCommit(true);
				if(pstmt != null)
					pstmt.close();
			}
			catch (SQLException e)
			{}
		}
		
		//move the file ..
		move(file, test);
	}
	
	/**
	 * Move the xml file in the corresponding directory.
	 * @param file
	 * @param b
	 */
	private void move(File file, boolean b)
	{ 
		// Destination directory 
		File dir = null;
		if(b)
			dir=fileOutputGoodXMLDir;
		else
			dir=fileOutputBadXMLDir;
		// Move file to new directory 
		boolean success = file.renameTo(new File(dir, file.getName())); 
		if(!success)
			log4j.error("Unable to move file "+file.getAbsolutePath()+" to the directory "+dir.getAbsolutePath());
	}
	
	/**
	 * Try to open a db connection and prepare the statements ...
	 * @throws Exception
	 */
	public void prepare() throws Exception
	{
		inputFilter = new InputFilter(INPUT_END_PATTERN);
		parser = new DOMParser();
		//some params
		dbMaxRetry=Integer.parseInt(StartApp.getProperty(CONF_KEY_DB_MAXRETRY));
		dbRetryDelay=Integer.parseInt(StartApp.getProperty(CONF_KEY_DB_RETRY_DELAY));
		dbTimeout=Integer.parseInt(StartApp.getProperty(CONF_KEY_DB_TIMEOUT));
		dbCommitSize=Integer.parseInt(StartApp.getProperty(CONF_KEY_DB_COMMIT_SIZE));
		
		//constants
        idApplication=Integer.parseInt(StartApp.getProperty(CONF_KEY_MAPPING_ID_APPLICATION));
        idMessageType=Integer.parseInt(StartApp.getProperty(CONF_KEY_MAPPING_ID_MESSAGE_TYPE));
        idMessageCategory=Integer.parseInt(StartApp.getProperty(CONF_KEY_MAPPING_ID_MESSAGE_CATEGORY));
		
		//try to open the connection
		openConnection();
	}
	
	/**
	 * Shutdown routines
	 */
	public void shutdown()
	{
		try
		{
			closeConnection();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Try to open a db connection with retry.
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	private void openConnection() throws SQLException, ClassNotFoundException, InterruptedException
	{
		connection = DataSources.getConnection();
	}
	
	/**
	 * Checks if the db connection is alive.
	 * @return true if is alive
	 * @throws SQLException 
	 */
	private boolean isValidConnection() throws SQLException
	{
		return true; //connection.isValid(dbTimeout);
	}

	/**
	 * Closes the connection
	 * @throws SQLException
	 */
	private void closeConnection() throws SQLException
	{
		if(connection!=null)
			connection.close();
	}
	
	private String insert_fcd = "insert into position_fcd (id_vehicle, latitude, longitude, speed, timestamp, dir, satellites, source) values(?, ?, ?, ?, ?, ?, ?, ?)";
}
