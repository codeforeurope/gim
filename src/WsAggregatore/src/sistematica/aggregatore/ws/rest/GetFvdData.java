package sistematica.aggregatore.ws.rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import sistematica.aggregatore.ws.rest.jaxb.LocationReferenceType;
import sistematica.aggregatore.ws.rest.jaxb.LocationReferenceType.DetailedGraphInfo;
import sistematica.aggregatore.ws.rest.jaxb.TTType;
import sistematica.aggregatore.ws.rest.jaxb.TrafficData;
import sistematica.aggregatore.ws.rest.jaxb.TrafficDataType;
import sistematica.aggregatore.ws.settings.WebSettings;
import sistematica.mobiworkdb.ds.DataSources;


@Path("/")
public class GetFvdData
{
    private static Logger log4j = Logger.getLogger(GetFvdData.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@GET
	@Path("getDataFvd/")
	@Produces({ MediaType.TEXT_XML })
	public String getHTML(@QueryParam("instant") String instant) throws Exception
	{
		int i = instant.indexOf("*");
		log4j.info("--------");
		log4j.info(instant.substring(0, i));
		log4j.info(instant.substring(i + 1));
//		InputStream in = new FileInputStream("/opt/trafficDataSimone.xml");
		InputStream in = new FileInputStream(WebSettings.TEST_FILE_NAME);
		String str = "";
		int letti;
		byte[] b = new byte[4096];
		while ((letti = in.read(b)) != -1)
		{
			str += new String(b, 0, letti);
		}
		return str;
	}
	
	@GET
	@Path("getDataFvdXml/")
	@Produces("application/xml")
	public TrafficData getXML(@QueryParam("instant") String instant) throws Exception
	{
		String source = WebSettings.FCD_SOURCE;
		
		boolean instantOk = true;
		
		Date startTime = null;
		Date endTime = null;
		
		// Parsing della stringa contente l'istante iniziale e finale per cui
		// ottenere i dati
		int index = instant.indexOf("*");

		String instant1 = instant.substring(0, index);
		String instant2 = instant.substring(index + 1);

		Date date1 = sdf.parse(instant1);
		Date date2 = sdf.parse(instant2);

		log4j.info("-------- NEW CALL --------");
		log4j.debug("instant1 = " + instant1);
		log4j.debug("instant2 = " + instant2);

		// Crea l'oggetto TrafficData.
		TrafficData trafficData = new TrafficData();

		// 1. Setta l'attributo Datatype
		trafficData.setDatatype(TrafficDataType.MISURA);

		// 2. Setta l'attributo GenerationTime
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(System.currentTimeMillis());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		trafficData.setGenerationTime(date);

		// 3. Setta l'attributo StartTime e EndTime in base all'istante iniziale
		// e finale
		log4j.info("Checking Start time and end time... ");
		if (date1.before(date2))
		{
			startTime = date1;
			endTime = date2;  
		}
		else if (date2.before(date1))
		{
			startTime = date2;
			endTime = date1; 
		}
		else
		{
			log4j.warn("Start time and end time overlap!");
			instantOk = false;
			
			startTime = date1;
			endTime = date2;  
		}
		
		log4j.info("Start time = " + sdf.format(startTime));
		log4j.info("  End time = " + sdf.format(endTime));
		
		
		// Setta l'attributo StartTime
		gc.setTimeInMillis(startTime.getTime());
		date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		trafficData.setStartTime(date);

		// Setta l'attributo EndTime
		gc.setTimeInMillis(endTime.getTime());
		date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		trafficData.setEndTime(date);

		
		// 3. Setta l'attributo LocationReference
		DetailedGraphInfo grafo = new DetailedGraphInfo();
		grafo.setName("Navteq");
		grafo.setVersion("2011");

		LocationReferenceType locRefType = new LocationReferenceType();
		locRefType.setDetailedGraphInfo(grafo);
		trafficData.setLocationReference(locRefType);

		// 4. Setta l'attributo Source
		trafficData.setSource("WsAggregatore");

		// 5. Aggiunge dei valori di test TTType		
		DbManager mgr = null;
		ArrayList<TTType> list = null;
		TTType ttType = null;
		
		if (instantOk)
		{
			mgr = new DbManager(DataSources.getPools().getDefaultPool());
			
			log4j.info("Getting the elements from db to add into the xml...");
			list = mgr.getTTTypeData(startTime, endTime, source);
			
			if(list != null)
			{
				log4j.info("Ok, adding the elements to the xml...");
				
				for (int i = 0; i < list.size(); i++)
				{
					ttType = list.get(i);
					trafficData.getTTData().add(ttType);
				}
				
				log4j.info("Ok, added " + list.size() + " element to the xml.");
			}
			else
			{
				log4j.warn("No element to add to the xml.");
			}
				
		}
		else
		{
			log4j.warn("No element to add to the xml.");
		}

		return trafficData;
	}

//	@GET
//	@Path("getDataFvdXml/")
//	@Produces("application/xml")
//	public TrafficData getXML(@QueryParam("instant") String instant) throws Exception
//	{
//		boolean instantOk = true;
//		
//		Date StartTime = null;
//		Date EndTime = null;
//		
//		// Parsing della stringa contente l'istante iniziale e finale per cui
//		// ottenere i dati
//		int index = instant.indexOf("*");
//
//		String instant1 = instant.substring(0, index);
//		String instant2 = instant.substring(index + 1);
//
//		Date date1 = sdf.parse(instant1);
//		Date date2 = sdf.parse(instant2);
//
//		System.out.println("--------");
//		System.out.println(instant1);
//		System.out.println(instant2);
//
//		// Crea l'oggetto TrafficData.
//		TrafficData trafficData = new TrafficData();
//
//		// 1. Setta l'attributo Datatype
//		trafficData.setDatatype(TrafficDataType.MISURA);
//
//		// 2. Setta l'attributo GenerationTime
//		GregorianCalendar gc = new GregorianCalendar();
//		gc.setTimeInMillis(System.currentTimeMillis());
//		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
//		trafficData.setGenerationTime(date);
//
//		// 3. Setta l'attributo StartTime e EndTime in base all'istante iniziale
//		// e finale
//		if (date1.before(date2))
//		{
//			StartTime = date1;
//			EndTime = date2;  
//		}
//		else if (date2.before(date1))
//		{
//			StartTime = date2;
//			EndTime = date1; 
//		}
//		else
//		{
//			System.out.println("Istante iniziale e finale coincidenti!");
//			instantOk = false;
//			
//			StartTime = date1;
//			EndTime = date2;  
//		}
//		
//		// Setta l'attributo StartTime
//		gc.setTimeInMillis(StartTime.getTime());
//		date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
//		trafficData.setStartTime(date);
//
//		// Setta l'attributo EndTime
//		gc.setTimeInMillis(EndTime.getTime());
//		date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
//		trafficData.setEndTime(date);
//
//		
//		// 3. Setta l'attributo LocationReference
//		DetailedGraphInfo grafo = new DetailedGraphInfo();
//		grafo.setName("Navteq");
//		grafo.setVersion("2011");
//
//		LocationReferenceType locRefType = new LocationReferenceType();
//		locRefType.setDetailedGraphInfo(grafo);
//		trafficData.setLocationReference(locRefType);
//
//		// 4. Setta l'attributo Source
//		trafficData.setSource("WsAggregatore");
//
//		// 5. Aggiunge dei valori di test TTType
//
//		// <td:TT_data lcd1="1310" lcd2="1312" len="1234" time="PT48M"
//		// speed="53" n_vehicles="15" std_dev="3" accuracy="90"
//		// vehicle_type="M1-AU"/>
//		// <td:TT_data lcd1="1312" lcd2="1310" len="1234" time="PT28M"
//		// speed="53" n_vehicles="15" std_dev="3" accuracy="90"
//		// vehicle_type="M1-AU"/>
//		// <td:TT_data lcd1="1314" lcd2="1312" len="1234" time="PT44M"
//		// speed="53" n_vehicles="15" std_dev="3" accuracy="90"
//		// vehicle_type="M1-AU"/>
//		// <td:TT_data lcd1="1312" lcd2="1314" len="1234" time="PT23M"
//		// speed="53" n_vehicles="15" std_dev="3" accuracy="90"
//		// vehicle_type="M1-AU"/>
//		// <td:TT_data lcd1="1310" lcd2="1315" len="1234" time="PT12M"
//		// speed="53" n_vehicles="15" std_dev="3" accuracy="90"
//		// vehicle_type="M1-AU"/>
//		// <td:TT_data lcd1="1315" lcd2="1310" len="1234" time="PT32M"
//		// speed="53" n_vehicles="15" std_dev="3" accuracy="90"
//		// vehicle_type="M1-AU"/>
//
//		if (instantOk)
//		{
//			TTType ttType = new TTType();
//			ttType.setAccuracy(90);
//			ttType.setEstimatedSpeed(new BigDecimal("50"));
//			ttType.setLcd1(new BigInteger("1310"));
//			ttType.setLcd2(new BigInteger("1312"));
//			ttType.setLen(new BigDecimal("1234"));
//			ttType.setNVehicles(15);
//			ttType.setQIdx(3);
//			ttType.setSpeed(new BigDecimal("53"));
//			ttType.setStdDev(new BigDecimal("3"));
//			ttType.setTime(new BigDecimal("48"));
//			ttType.setVehicleType("3");
//			trafficData.getTTData().add(ttType);
//			
//			ttType = new TTType();
//			ttType.setAccuracy(90);
//			ttType.setEstimatedSpeed(new BigDecimal("50"));
//			ttType.setLcd1(new BigInteger("1310"));
//			ttType.setLcd2(new BigInteger("1312"));
//			ttType.setLen(new BigDecimal("1234"));
//			ttType.setNVehicles(15);
//			ttType.setQIdx(3);
//			ttType.setSpeed(new BigDecimal("53"));
//			ttType.setStdDev(new BigDecimal("3"));
//			ttType.setTime(new BigDecimal("48"));
//			ttType.setVehicleType("3");
//			trafficData.getTTData().add(ttType);
//			
//			ttType = new TTType();
//			ttType.setAccuracy(90);
//			ttType.setEstimatedSpeed(new BigDecimal("50"));
//			ttType.setLcd1(new BigInteger("1310"));
//			ttType.setLcd2(new BigInteger("1312"));
//			ttType.setLen(new BigDecimal("1234"));
//			ttType.setNVehicles(15);
//			ttType.setQIdx(3);
//			ttType.setSpeed(new BigDecimal("53"));
//			ttType.setStdDev(new BigDecimal("3"));
//			ttType.setTime(new BigDecimal("48"));
//			ttType.setVehicleType("3");
//			trafficData.getTTData().add(ttType);
//			
//			ttType = new TTType();
//			ttType.setAccuracy(90);
//			ttType.setEstimatedSpeed(new BigDecimal("50"));
//			ttType.setLcd1(new BigInteger("1310"));
//			ttType.setLcd2(new BigInteger("1312"));
//			ttType.setLen(new BigDecimal("1234"));
//			ttType.setNVehicles(15);
//			ttType.setQIdx(3);
//			ttType.setSpeed(new BigDecimal("93"));
//			ttType.setStdDev(new BigDecimal("3"));
//			ttType.setTime(new BigDecimal("48"));
//			ttType.setVehicleType("3");
//			trafficData.getTTData().add(ttType);
//			
//			ttType = new TTType();
//			ttType.setAccuracy(90);
//			ttType.setEstimatedSpeed(new BigDecimal("50"));
//			ttType.setLcd1(new BigInteger("1310"));
//			ttType.setLcd2(new BigInteger("1312"));
//			ttType.setLen(new BigDecimal("1234"));
//			ttType.setNVehicles(15);
//			ttType.setQIdx(3);
//			ttType.setSpeed(new BigDecimal("93"));
//			ttType.setStdDev(new BigDecimal("3"));
//			ttType.setTime(new BigDecimal("48"));
//			ttType.setVehicleType("3");
//			trafficData.getTTData().add(ttType);
//			
//			ttType = new TTType();
//			ttType.setAccuracy(90);
//			ttType.setEstimatedSpeed(new BigDecimal("50"));
//			ttType.setLcd1(new BigInteger("1310"));
//			ttType.setLcd2(new BigInteger("1312"));
//			ttType.setLen(new BigDecimal("1234"));
//			ttType.setNVehicles(15);
//			ttType.setQIdx(3);
//			ttType.setSpeed(new BigDecimal("93"));
//			ttType.setStdDev(new BigDecimal("3"));
//			ttType.setTime(new BigDecimal("48"));
//			ttType.setVehicleType("3");
//			trafficData.getTTData().add(ttType);
//		}
//
//		return trafficData;
//	}

}
