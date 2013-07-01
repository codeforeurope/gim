package it.sistematica.geocoder.test;

import it.sistematica.geocoder.Geocoder;
import it.sistematica.geocoder.data.RawData;
import it.sistematica.geocoder.util.DbManager;
import it.sistematica.geocoder.util.RawDataManager;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

public class Test
{
	private static Logger log = Logger.getLogger(Test.class);
	
	public static void main(String[] args)
	{
		//------------------------------
		// POSTGRES CONNECTION
		//------------------------------

		Connection conn = null;
		DbManager dbManager = null;
		
		RawDataManager rdMng = null;
		Geocoder geo = null;
		
		try
		{
//			Class.forName("org.postgresql.Driver"); 
//		    String url = "jdbc:postgresql://localhost:5432/gim"; 
//		    conn = DriverManager.getConnection(url, "postgres", "postgres");
		    
		    dbManager = DbManager.getInstance(1);
		    conn = dbManager.getConnessione();
		    		    
		    ((org.postgresql.PGConnection)conn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
		    ((org.postgresql.PGConnection)conn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));
		    
		    log.info("Postgres Connected.");
		    
		    rdMng = new RawDataManager(conn);
		    List<RawData> raw_data_list = rdMng.readRawData(-1);
		    log.info("Estratti " + raw_data_list.size() + " FCD");
		    
		    long start = System.currentTimeMillis();
		    geo = new Geocoder(conn, 0.001, 0.005);
		    List<RawData> output = geo.geocode(raw_data_list);
		    
		    log.info("Georeferenziati " + output.size() + " di FCD in " + (System.currentTimeMillis() - start) + " [ms] ");
		    
		    for(int i=0;i<output.size();i++)
		    {
		    	log.info("" + output.get(i).getEdgeId());
		    }
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

}
