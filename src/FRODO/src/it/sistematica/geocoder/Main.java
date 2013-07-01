package it.sistematica.geocoder;

import it.sistematica.geocoder.data.RawData;
import it.sistematica.geocoder.util.DbManager;
import it.sistematica.geocoder.util.RawDataManager;
import it.sistematica.geocoder.util.Util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main
{
	private static Logger log = Logger.getLogger(Main.class);
	
	public static Properties custom_properties = null;
	
	public static int NUM_OF_ELABORATION = 10000;
	
	public static void main(String[] args)
	{
		Connection spatialConn = null;
		DbManager dbSpatialManager = null;

		Connection systemConn = null;
		DbManager dbSystemManager = null;
		
		RawDataManager rdMng = null;
		Geocoder geo = null;
				
		if(args != null && args.length > 0)
		{
			custom_properties = new Properties();
			try
			{
				custom_properties.load(new FileInputStream(args[0]));
				PropertyConfigurator.configure(custom_properties);
			}
			catch(Exception e)
			{
				custom_properties = null;
			}
			
			log.info("Caricate properties : " + custom_properties);
		}
		else
			log.info("Caricate properties di default");
		
		//-------------------------------
		// FILE DI PROPERTIES
		//-------------------------------
		log.info("#####################################");
		log.info("###        FILE PROPERTIES        ###");
		log.info("#####################################");
		log.info("# geocoder.db.spatial.driver." + Util.getProperty(Main.custom_properties, "geocoder.db.system.driver"));
		log.info("# geocoder.db.system.url....." + Util.getProperty(Main.custom_properties, "geocoder.db.system.url"));
		log.info("# geocoder.db.system.user...." + Util.getProperty(Main.custom_properties, "geocoder.db.system.user"));
		log.info("# geocoder.db.system.pass...." + Util.getProperty(Main.custom_properties, "geocoder.db.system.pass"));
		log.info("#####################################");
		log.info("# geocoder.db.spatial.driver." + Util.getProperty(Main.custom_properties, "geocoder.db.spatial.driver"));
		log.info("# geocoder.db.spatial.url...." + Util.getProperty(Main.custom_properties, "geocoder.db.spatial.url"));
		log.info("# geocoder.db.spatial.user..." + Util.getProperty(Main.custom_properties, "geocoder.db.spatial.user"));
		log.info("# geocoder.db.spatial.pass..." + Util.getProperty(Main.custom_properties, "geocoder.db.spatial.pass"));
		log.info("#####################################");
		log.info("# geocoder.sleep.seconds....." + Util.getProperty(Main.custom_properties, "geocoder.sleep.seconds"));
		log.info("#####################################");
		log.info("# geocoder.mail.smtp.host...." + Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.host"));
		log.info("# geocoder.mail.smtp.port...." + Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.port"));
		log.info("# geocoder.mail.smtp.user...." + Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.user"));
		log.info("# geocoder.mail.smtp.from...." + Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.from"));
		log.info("# geocoder.mail.smtp.to......" + Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.to"));
		log.info("#####################################");
		
		try
		{
			// VISUAL TRACK LOG
			Util.initSismon();
			log.info("#####################################");
			log.info("# Sismon log enable");
			log.info("#####################################");
			
//			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
			
//			Util.sendMail("Frodo ERROR", "questa è una mail di prova");
			
			dbSystemManager = DbManager.getInstance(DbManager.SYSTEM);
			systemConn = dbSystemManager.getConnessione();
			
			dbSpatialManager = DbManager.getInstance(DbManager.SPATIAL);
			spatialConn = dbSpatialManager.getConnessione();
		    		    
		    ((org.postgresql.PGConnection)spatialConn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
		    ((org.postgresql.PGConnection)spatialConn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));
		    
		    log.info("Postgres Connected.");
		    
		    long last_id = -1;
		    
		    while(true)
		    {		    
		    	last_id = Util.getLastIdProcessed(spatialConn);
		    			    	
		    	rdMng = new RawDataManager(systemConn);
			    List<RawData> raw_data_list = rdMng.readRawData(last_id);
			    log.info("Estratti " + raw_data_list.size() + " FCD");
			    
			    long start = System.currentTimeMillis();
			    geo = new Geocoder(spatialConn, 0.01, 0.005);
			    List<RawData> output = geo.geocode(raw_data_list);
			    
			    log.info("Georeferenziati " + output.size() + " di FCD in " + (System.currentTimeMillis() - start) + " [ms] ");
			    
//			    Util.sismonSendLog(Util.getProperty(Main.custom_properties, "visual.track.log.tag"), "Georeferenziati " + output.size() + " di FCD in " + (System.currentTimeMillis() - start) + " [ms] ");
			    
			    log.debug("Inserisco nella tabella di output.");
			    
			    rdMng.writeRawData(output);
			    
			    try
			    {
			    	if(output != null)
			    	{
			    		try
			    		{
			    			last_id = output.get(output.size() - 1).getId_pos();
				    		log.debug("LAST ID IS " + last_id);
			    		}
			    		catch(Exception e)
			    		{
			    			if(raw_data_list != null && raw_data_list.size() > 0)
			    			{
			    				log.debug(" >> DOVUTO A last_id -->" + last_id + " E (raw_data_list.size()-1) --> " + (raw_data_list.size()-1));
			    				last_id = last_id + (raw_data_list.size()-1);
//			    				last_id = raw_data_list.get(raw_data_list.size()).getId_pos();

					    		log.debug("		>> LAST ID IS " + last_id);
			    			}
			    		}
			    		Util.setLastIdProcessed(spatialConn, last_id);
			    	}
			    }
			    catch(Exception e)
			    {
			    	log.error("Can not get last timestamp: " + e.getMessage(), e);
			    }
			    		    	
		    	try
		    	{
		    		Thread.sleep(Integer.parseInt(Util.getProperty(custom_properties, "geocoder.sleep.seconds"))*1000);
		    	}
		    	catch(InterruptedException e)
		    	{}

//		    	Util.sismonSendMetric(Util.getProperty(Main.custom_properties, "visual.track.metric.status"), "1");
//		    	Util.sismonSendMetric(Util.getProperty(Main.custom_properties, "visual.track.metric.pos.ok"), String.valueOf(output.size()));
//		    	Util.sismonSendMetric(Util.getProperty(Main.custom_properties, "visual.track.metric.pos.err"), String.valueOf(raw_data_list.size() - output.size()));
		    }
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
//			Util.sendMail("FRODO ERROR", e);
		}
	}
	
	private static class ShutdownHook extends Thread
	{
		public void run()
		{
	    	Util.sismonSendMetric(Util.getProperty(Main.custom_properties, "visual.track.metric.status"), "1");
	    	Util.closeSismon();
	    	
			Util.sendMail("FRODO ERROR", "Applicazione arrestata in modo anomalo!!!!");
		}
	}
}
