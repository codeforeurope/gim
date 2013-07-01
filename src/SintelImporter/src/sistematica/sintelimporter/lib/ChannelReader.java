/**
 * 
 */
package sistematica.sintelimporter.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import sistematica.sintelimporter.data.BodyFlow;
import sistematica.sintelimporter.data.BodyIntegrated;
import sistematica.sintelimporter.data.BodyRoot;
import sistematica.sintelimporter.data.ClassType;
import sistematica.sintelimporter.data.DbMilanoObject;
import sistematica.sintelimporter.data.Message;
import sistematica.sintelimporter.data.MessageType;
import sistematica.sintelimporter.data.XmlDataMsg;
import sistematica.sintelimporter.data.ZoneFlow;
import sistematica.sintelimporter.data.ZoneIntegrated;
import sistematica.sintelimporter.settings.Keys;
import sistematica.sintelimporter.utils.BinaryDecoder;
import sistematica.sintelimporter.utils.DbConnection;

/**
 * @author gsilvestri
 *
 */
public class ChannelReader extends Thread
{
	private Logger log4j = Logger.getLogger(ChannelReader.class);
	
	InputStream inputStream = null;
	String moviTraffTag = null;
	int msgType = -1;
	
	public ChannelReader(InputStream inputStream, String moviTraffTag, int msgType)
	{
		this.inputStream = inputStream;
		this.moviTraffTag = moviTraffTag;
		this.msgType = msgType;
	}
	
	public void run()
	{
		String res = "";
		boolean stop = false;
		
		try
		{
			if(this.inputStream != null)
			{	
				//InpuStream
				log4j.info("Reading inputStream...");
				int count = 0;
				byte[] len = new byte[4];
				int lenXml = -1;
				while (!stop)
				{
					int b = inputStream.read();
					count++;
					if(count == 0 || count == 1)
					{
						//2 byte di inizio
						log4j.trace(b);
					}
					else if(count == 2 || count == 3 || count == 4 || count == 5)
					{
						//4 byte di lunghezza
						len[count-2] = (byte) b;
						
						if(count == 5)
						{
							lenXml = BinaryDecoder.decode4(len, 0);
							log4j.debug("lenXml = " + BinaryDecoder.decode4(len, 0));
						}
					}
					else
					{
						res += (char)b;
						if(res.length() == lenXml)
						{
							stop = true;
						}
					}
					
				}
				
				log4j.info(res);
				log4j.debug("tot len = " + res.length());
				if(msgType == Message.TYPE_GET_DATA)
				{
					XmlDataMsg xmlDataMsg = parseXml(res);
					if(xmlDataMsg != null)
					{
						xmlDataMsg.stamp();
						ArrayList<DbMilanoObject> list = getDbMilanoObjList(xmlDataMsg);
						
						if(list != null)
						{
							int insertNum = insertDB(list);
							if(insertNum > 0)
							{
								log4j.info("Ok, " + insertNum + " data correctly inserted into the DB...");
							}
							else
							{
								log4j.warn("Error inserting data into the DB...");
							}
						}
						else
						{
							log4j.warn("No data to insert into the DB.. nothing to do.");
						}
						
					}
				}
				else
				{
					if(Keys.ARCHIVE_XML_ENABLED)
					{
						writeXml(res, msgType);
					}
				}
				
				log4j.info("ChannelReader stop..");
			}
		}
		catch (Exception e)
		{
			log4j.error(e,e);
		}
	}

	private void writeXml(String res, int msgType)
	{		
		SAXBuilder builder = null;
		Document xml_doc = null;
		File file = null;
		FileOutputStream fos = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try
		{
			builder = new SAXBuilder();
			xml_doc = builder.build(new StringReader(res));
			
			//Creazione dell'oggetto XMLOutputter e quindi del file xml
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			file = new File(Keys.ARCHIVE_XML_DIR + File.separatorChar + "SINTEL_" + Message.getTagMessage(msgType) + "_" + sdf.format(new Date(System.currentTimeMillis())) + ".xml");
			fos = new FileOutputStream(file);
			outputter.output(xml_doc, fos);
			
			log4j.info(file.getPath() + " created.");
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		finally
		{
			try
			{
				if(fos != null)
					fos.close();
			}
			catch (IOException e)
			{
				log4j.error(e,e);
			}
		}	
	}

	private int insertDB(ArrayList<DbMilanoObject> list)
	{
		DbConnection dbConn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		
		int res = 0;
		
		String sql = "insert into DatiAggregati (IdAggregato, IdSezione, DataOra, Periodicita, IdCorsia, NumeroVeicoli, VelocitaMedia, LunghezzaMedia, HeadwayMedio, GapMedio, Occupazione, Diagnostica) values ( ?, (select IdSezione from Sezioni where TagSezione = ?), STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'), ?, ?, ?, ?, ?, ?, ?, ?, ? );";
		
		try
		{			
			dbConn = new DbConnection(Keys.DB_DRIVER, Keys.DB_URL, Keys.DB_USER, Keys.DB_PWD);
			conn = dbConn.getConnection();
			
			ps = conn.prepareStatement(sql);
			int perdiodicita = -1;
			double lunghezza = -1;
			
			for(DbMilanoObject data:list)
			{
				
				perdiodicita = Integer.parseInt(data.getPeriodicita())/60;
				lunghezza = Double.parseDouble(data.getLunghezzaMedia())/10d;
				
				setVarchar(ps, 1, data.getIdAggregato());
				setVarchar(ps, 2, data.getTagMoviTraff());
				setVarchar(ps, 3, data.getDataOra());
				setVarchar(ps, 4, String.valueOf(perdiodicita));
				setVarchar(ps, 5, data.getIdCorsia());
				setVarchar(ps, 6, data.getNumeroVeicoli());
				setVarchar(ps, 7, data.getVelocitaMedia());
				setVarchar(ps, 8, String.valueOf(lunghezza));
				setVarchar(ps, 9, data.getHeadWayMedio());
				setVarchar(ps, 10, data.getGapMedio());
				setVarchar(ps, 11, data.getOccupazione());
				setVarchar(ps, 12, data.getDiagnostica());
			    
				log4j.trace(ps.toString().substring(ps.toString().indexOf(":") + 1 ));
				
			    ps.addBatch();
			}

			int resArr[] = ps.executeBatch();
			
			for(int i=0; i<resArr.length; i++)
			{
				if(resArr[i] == Statement.SUCCESS_NO_INFO)
				{
					res++;
				}
				else if(resArr[i] >= 0)
				{
					res++;
				}
				else if(resArr[i] == Statement.EXECUTE_FAILED)
				{}
			}
			
			log4j.info("Ok, writed on DB " + res + " aggregated data.");
			
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		finally
		{
			
			try
			{	if(ps != null)
					ps.close();
			}
			catch (SQLException e)
			{
				log4j.error(e,e);
			}
			
			if(dbConn != null)
			{
				dbConn.returnConnection(conn);
				dbConn.closeConnection();
			}
		}
		
		return res;
	}

	private ArrayList<DbMilanoObject> getDbMilanoObjList(XmlDataMsg xmlDataMsg) throws Exception
	{
		ArrayList<DbMilanoObject> list = null;
		
		DbMilanoObject temp = null;
		
		if(xmlDataMsg.bodyRoot.listMessageType != null)
		{
			for(MessageType type: xmlDataMsg.bodyRoot.listMessageType)
			{				
				if(type.bodyIntegrated != null)
				{					
					if(type.bodyIntegrated.listZoneIntegrated != null)
					{
						for(ZoneIntegrated zone: type.bodyIntegrated.listZoneIntegrated)
						{						
							int sommaVeicoli = 0;
							double velocitaMediaClassi = 0d;
							double gapMedioClassi = 0d;
							int divideFor = 0; 
							
							if(zone.listClassType != null)
							{
								for(ClassType _class: zone.listClassType)
								{					
									sommaVeicoli += Integer.parseInt(_class.numVeh);
									velocitaMediaClassi += Double.parseDouble(_class.speed);
									gapMedioClassi += Double.parseDouble(_class.gapTime);
									
									if(Double.parseDouble(_class.speed) != 0d)
									{
										divideFor++;
									}
								}
								
								if(divideFor != 0)
								{
									velocitaMediaClassi = velocitaMediaClassi/divideFor;
									gapMedioClassi = gapMedioClassi/divideFor;
								}
								else
								{
									velocitaMediaClassi = 0;
									gapMedioClassi = 0;
								}
							}
							
							temp = new DbMilanoObject(this.moviTraffTag, type.bodyIntegrated.dataNumber, convertTimestamp(type.bodyIntegrated.utc), type.bodyIntegrated.intervalTime, zone.zoneId, String.valueOf(sommaVeicoli), String.valueOf(velocitaMediaClassi), zone.length, zone.headWay, String.valueOf(gapMedioClassi), zone.occupancy, zone.confidence);
							
							if(list == null)
								list = new ArrayList<DbMilanoObject>();
							
							list.add(temp);
						}
					}
				}
				
			}
		}
		
		return list;
	}

	private String convertTimestamp(String utc) throws Exception
	{
		String utcStr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		long seconds = Long.parseLong(utc);
		long milli = seconds * 1000;
		
		utcStr = sdf.format(new Date(milli));
		
		return utcStr;
	}

	private XmlDataMsg parseXml(String res)
	{
		//JDOM object
		SAXBuilder builder = null;
		Document xml_doc = null;
		Element rootElement = null;
		Element bodyRoot = null;
		Element bodyMessageTypeElement = null;
		List<Element> listMessageTypeElement = null;
		List<Element> listZoneFlowElement = null;
		List<Element> listZoneIntegratedElement = null;
		List<Element> listClassType = null;
		
		//ELEMENT BODY ATTRIBUTES
		String typeBodyMessageTypeElement = "";
		String intervalTimeBodyMessageTypeElement = "";
		String dataNumberBodyMessageTypeElement = "";
		String utcBodyMessageTypeElement = "";
		String millisecondsBodyMessageTypeElement = "";
		
		//ELEMENT ZONE (FlowSpeedData) ATTRIBUTES
		String idZoneFlowElement = "";
		String flowSpeedZoneFlowElement = "";
		String occupancyZoneFlowElement = "";
		
		//ELEMENT ZONE (IntegratedData) ATTRIBUTES
		String idZoneIntegratedElement = "";
		String occupancyZoneIntegratedElement = "";
		String confidenceZoneIntegratedElement = "";
		String lengthZoneIntegratedElement = "";
		String headwayZoneIntegratedElement = "";
		String densityZoneIntegratedElement = "";
		String headwaySqZoneIntegratedElement = "";
		
		//ELEMENT CLASS ATTRIBUTES
		String classNumberClassType = "";
		String numVehClassType = "";
		String speedClassType = "";
		String gapTimeClassType = "";
		String speedSqClassType = "";
		String gapSqClassType = "";

		
		//sistematica.sintelimporter.data object
		XmlDataMsg _xmlDataMsg = null;
		BodyRoot _bodyRoot = null;
		ArrayList<MessageType> _listMessageType = null;
		MessageType _messageType = null;
		BodyFlow _bodyFlow = null;
		BodyIntegrated _bodyIntegrated = null;
		ArrayList<ZoneFlow> _listZoneFlow = null;
		ZoneFlow _zoneFlow = null;
		ArrayList<ZoneIntegrated> _listZoneIntegrated = null;
		ZoneIntegrated _zoneIntegrated = null;
		ArrayList<ClassType> _listClassType = null;
		ClassType _classType = null;
		
		int countIntegratedData = 0;
		int countFlowSpeedData = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		try
		{
			builder = new SAXBuilder();
			xml_doc = builder.build(new StringReader(res));
			
			//Elemento root <Message Type="Data">
			rootElement = xml_doc.getRootElement();
			_xmlDataMsg = new XmlDataMsg();
			
			if (rootElement != null)
			{
				//Elemento body "Root"
				bodyRoot = rootElement.getChild("Body");
				_bodyRoot = new BodyRoot();
				
				if (bodyRoot != null)
				{
					listMessageTypeElement = bodyRoot.getChildren("Message");
					
					if (listMessageTypeElement != null)
					{
						_listMessageType = new ArrayList<MessageType>();
						
						
						log4j.debug("Find " + listMessageTypeElement.size() + " MessageType element on Body");
						for (Element messageTypeElement : listMessageTypeElement)
						{	
							_messageType = new MessageType();
							
							
							bodyMessageTypeElement = messageTypeElement.getChild("Body");

							typeBodyMessageTypeElement = bodyMessageTypeElement.getAttributeValue("Type");
							intervalTimeBodyMessageTypeElement = bodyMessageTypeElement.getAttributeValue("IntervalTime");
							dataNumberBodyMessageTypeElement = bodyMessageTypeElement.getAttributeValue("DataNumber");
							utcBodyMessageTypeElement = bodyMessageTypeElement.getAttributeValue("Utc");
							millisecondsBodyMessageTypeElement = bodyMessageTypeElement.getAttributeValue("MilliSeconds");
							
							
							if (typeBodyMessageTypeElement.equals("FlowSpeedData"))
							{
								countFlowSpeedData++;
								
								_bodyFlow = new BodyFlow();
								_bodyFlow.intervalTime = intervalTimeBodyMessageTypeElement;
								_bodyFlow.dataNumber = dataNumberBodyMessageTypeElement;
								_bodyFlow.utc = utcBodyMessageTypeElement;
								_bodyFlow.milliseconds = millisecondsBodyMessageTypeElement;
								
//								<Zone ZoneId="1" FlowSpeed="90" ZoneOccupancy="0"/>
//								<Zone ZoneId="2" FlowSpeed="89" ZoneOccupancy="0"/>
								
								listZoneFlowElement = bodyMessageTypeElement.getChildren("Zone");
								
								if (listZoneFlowElement != null)
								{
									_listZoneFlow = new ArrayList<ZoneFlow>();
									
									log4j.debug("Find " + listZoneFlowElement.size() + " ZoneFlowElement element on bodyMessageTypeElement");
									for (Element zoneFlowElement : listZoneFlowElement)
									{
										idZoneFlowElement = zoneFlowElement.getAttributeValue("ZoneId");
										flowSpeedZoneFlowElement = zoneFlowElement.getAttributeValue("FlowSpeed");
										occupancyZoneFlowElement = zoneFlowElement.getAttributeValue("ZoneOccupancy");
										
										_zoneFlow = new ZoneFlow();
										_zoneFlow.zoneId = idZoneFlowElement;
										_zoneFlow.flowSpeed = flowSpeedZoneFlowElement;
										_zoneFlow.zoneOccupancy = occupancyZoneFlowElement;
										
										_listZoneFlow.add(_zoneFlow);
									}
									
									_bodyFlow.listZoneFlow = _listZoneFlow;
								}
								else
								{
									log4j.warn("listZoneFlowElement is null!");
								}
								
								_messageType.bodyIntegrated = null;
								_messageType.bodyFlow = _bodyFlow;
								
							}
							else if (typeBodyMessageTypeElement.equals("IntegratedData"))
							{
								countIntegratedData++;
								
								_bodyIntegrated = new BodyIntegrated();
								_bodyIntegrated.intervalTime = intervalTimeBodyMessageTypeElement;
								_bodyIntegrated.dataNumber = dataNumberBodyMessageTypeElement;
								_bodyIntegrated.utc = utcBodyMessageTypeElement;
								_bodyIntegrated.milliseconds = millisecondsBodyMessageTypeElement;
								
								listZoneIntegratedElement = bodyMessageTypeElement.getChildren("Zone");
								
								if (listZoneIntegratedElement != null)
								{
									_listZoneIntegrated = new ArrayList<ZoneIntegrated>();
									
									log4j.debug("Find " + listZoneIntegratedElement.size() + " ZoneIntegratedElement element on bodyMessageTypeElement");
									for (Element zoneIntegratedElement : listZoneIntegratedElement)
									{
//										<Zone ZoneId="1" Occupancy="0" Confidence="0" Length="0" HeadWay="0" Density="0" HeadWaySq="0">
										
										idZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("ZoneId");
										occupancyZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("Occupancy");
										confidenceZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("Confidence");
										lengthZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("Length");
										headwayZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("HeadWay");
										densityZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("Density");
										headwaySqZoneIntegratedElement = zoneIntegratedElement.getAttributeValue("HeadWaySq");
										
										_zoneIntegrated = new ZoneIntegrated();
										_zoneIntegrated.zoneId = idZoneIntegratedElement;
										_zoneIntegrated.occupancy = occupancyZoneIntegratedElement;
										_zoneIntegrated.confidence = confidenceZoneIntegratedElement;
										_zoneIntegrated.length = lengthZoneIntegratedElement;
										_zoneIntegrated.headWay = headwayZoneIntegratedElement;
										_zoneIntegrated.density = densityZoneIntegratedElement;
										_zoneIntegrated.headWaySq = headwaySqZoneIntegratedElement;
										
										listClassType = zoneIntegratedElement.getChildren("Class");
										
										if(listClassType != null)
										{
											_listClassType = new ArrayList<ClassType>();
											
											log4j.debug("Find " + listClassType.size() + " ClassType element on Zone(IntegratedData)");
											for (Element classType : listClassType)
											{
//												<Class ClassNr="1" NumVeh="0" Speed="0" GapTime="0" SpeedSq="0" GapTimeSq="0"/>
												
												classNumberClassType = classType.getAttributeValue("ClassNr");
												numVehClassType = classType.getAttributeValue("NumVeh");
												speedClassType = classType.getAttributeValue("Speed");
												gapTimeClassType = classType.getAttributeValue("GapTime");
												speedSqClassType = classType.getAttributeValue("SpeedSq");
												gapSqClassType = classType.getAttributeValue("GapTimeSq");
												
												_classType = new ClassType();
												_classType.classNr = classNumberClassType;
												_classType.numVeh = numVehClassType;
												_classType.speed = speedClassType;
												_classType.gapTime = gapTimeClassType;
												_classType.speedSq = speedSqClassType;
												_classType.gapTimeSq = gapSqClassType;
												
												_listClassType.add(_classType);
											}
											
											_zoneIntegrated.listClassType = _listClassType;
										}
										else
										{
											log4j.warn("listClassType is null!");
										}
										
										_listZoneIntegrated.add(_zoneIntegrated);
										
									}
									_bodyIntegrated.listZoneIntegrated = _listZoneIntegrated;
								}
								else
								{
									log4j.warn("listZoneIntegratedElement is null!");
								}
								
								_messageType.bodyIntegrated = _bodyIntegrated;
								_messageType.bodyFlow = null;
							}
							else
							{
								log4j.error("Unknown BodyType = " + typeBodyMessageTypeElement);
							}
							
//							_messageType.bodyIntegrated = _bodyIntegrated;
//							_messageType.bodyFlow = _bodyFlow;
							
							_listMessageType.add(_messageType);
							
						}
						
						_bodyRoot.listMessageType = _listMessageType;
						_xmlDataMsg.bodyRoot = _bodyRoot;
						
						log4j.debug("countFlowSpeedData = " + countFlowSpeedData);
						log4j.debug("countIntegratedData = " + countIntegratedData);
					}
					else
					{
						log4j.warn("listMessageTypeElement is null!");
					}
				}
				else
				{
					log4j.warn("bodyRoot is null!");
				}
				
			}
			else
			{
				log4j.warn("rootElement is null!");
			}
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		
		if(Keys.ARCHIVE_XML_ENABLED)
		{
			File file = null;
			FileOutputStream fos = null;
			try
			{
				//Creazione dell'oggetto XMLOutputter e quindi del file xml
				XMLOutputter outputter = new XMLOutputter();
				outputter.setFormat(Format.getPrettyFormat());
				file = new File(Keys.ARCHIVE_XML_DIR + File.separatorChar + "SINTEL_" + Message.getTagMessage(msgType) + "_" + sdf.format(new Date(System.currentTimeMillis())) + ".xml");
				fos = new FileOutputStream(file);
				outputter.output(xml_doc, fos);
				
				log4j.info(file.getPath() + " created.");
			}
			catch(Exception e)
			{
				log4j.error(e,e);
			}
			finally
			{
				try
				{
					if(fos != null)
						fos.close();
				}
				catch (IOException e)
				{
					log4j.error(e,e);
				}
			}
		}
		return _xmlDataMsg;
		
	}
	
	
	private static void setVarchar(PreparedStatement ps, int index, String data) throws SQLException
	{
		if (data != null)
			ps.setString(index, data);
		else
			ps.setNull(index, java.sql.Types.VARCHAR);
	}
}
