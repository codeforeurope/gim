package gpsdatareceiver.webcontext;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import visualtrack.logger.client.VTLoggerMetric;
import visualtrack.logger.client.VTLoggerText;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

public class StartApp implements ServletContextListener
{
	public static ServletContext application;
	private static Logger log = Logger.getLogger(StartApp.class);
	public static Properties m_cfg;
	
	private int VISUAL_TRACK_LOG_INTERVAL = 300000;
	public static VTLoggerMetric vtmlog;
	public static VTLoggerText vttlog;
	
	public void contextDestroyed(ServletContextEvent ev)
	{

	}

	public void contextInitialized(ServletContextEvent ev)
	{
		try
		{
			application = ev.getServletContext();
			
			BasicConfigurator.configure();
			
			String propsFile = "/WEB-INF/gpsdatareceiver.properties";
			
			init(propsFile);
			
			initSismon();
			
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						while(true)
						{
							sismonSendMetric(getProperty("visual.track.metric.status"), "1");
							
							try
							{
								Thread.sleep(VISUAL_TRACK_LOG_INTERVAL);
							}
							catch(InterruptedException e)
							{}
						}
					}
				}
			).start();
			
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		}
		catch(Exception e)
		{
			log.error(" WEBAPP INIT FAIL :");
			log.error(e.getMessage(), e);
		}
	}
	
	public Properties getProperties() 
    {
        return m_cfg;
    }
	
	public static String getProperty(String key)
    {
        return m_cfg.getProperty(key);
    }
	
	public static String getProperty(String name, String defaultValue)
    {
        return m_cfg.getProperty(name, defaultValue);
    }
	
	//------------------------------
	// SISMON - VT
	//------------------------------
	public static void initSismon() throws MalformedURLException
	{
		VTLoggerThreadPool.init(Integer.parseInt(getProperty("visual.track.thread.number")), 
								Integer.parseInt(getProperty("visual.track.thread.post.timeout")));
		
		vtmlog = new VTLoggerMetric(getProperty("visual.track.metric.url"), true);
		vttlog = new VTLoggerText(getProperty("visual.track.log.url"), true);
	}
	
	public static void closeSismon()
	{
		VTLoggerThreadPool.terminateExecutorService(false); 
	}
	
	public static void sismonSendMetric(String metrica, String value)
	{
		vtmlog.sendValue(metrica, value, System.currentTimeMillis()); 
	}
	
	public static void sismonSendLog(String tag, String log)
	{
		vttlog.info(tag, log, System.currentTimeMillis()); 
	}
	
	public static void init(String propsFile) throws Exception
    {
        InputStream is = null;
        try
        {
        	is = application.getResourceAsStream(propsFile); //new FileInputStream(propsFile);
            _init(is);
        }
        catch(Exception e)
        {
        	log.error(e);
        }
        finally
        {
            if (is!=null)
            {
                try
                {
                    is.close();
                }
                catch(Exception e){}
            }
            
        }
    }
	
	private static void _init(InputStream is) throws Exception
    {
        m_cfg = new Properties();
        m_cfg.load(is);
        
        PropertyConfigurator.configure(m_cfg);

    	log.info("");
    	log.info("##########################################");
        log.info("File Properties...................CARICATO");
    	log.info("##########################################");
    	log.info("");
        
        initDb();
    }

	public static void initDb() throws Exception
	{
		try
		{		
			Context ctx = new InitialContext();
//			String dsName = "java:comp/env/" + m_cfg.getProperty(Constants.PARAM_DATASOURCE_NAME);
			String dsName = m_cfg.getProperty("datasource.name"); //"java:comp/env/" + m_cfg.getProperty("datasource.name");
			
			DataSource ds = (DataSource)ctx.lookup(dsName);
						
			DataSources.addDataSources(ds, dsName);
			
			log.info("##########################################");
			log.info(" connection pool name............." + m_cfg.getProperty("datasource.name"));
			log.info(" .................................avviato");
			log.info("##########################################");
			log.info("");
			
			int counter = 1;
			
			while(true)
			{
				String key_aux_name = "datasource.name" + "." + counter;
				
				String ds_aux_name = null;
				
				try
				{
					ds_aux_name = m_cfg.getProperty(key_aux_name);
				}
				catch(Exception e)
				{
					ds_aux_name = null;
					break;
				}
				
				if(ds_aux_name != null)
				{
					DataSource ds_aux = (DataSource)ctx.lookup(ds_aux_name);
										
					DataSources.addDataSources(ds_aux, ds_aux_name);
					
					log.info("##########################################");
					log.info(" connection pool name............." + m_cfg.getProperty(key_aux_name));
					log.info(" .................................avviato");
					log.info("##########################################");
					log.info("");
				}
				else
					break;
				
				counter++;
			}
			
		}
		catch(Exception e)
		{
			log.error(" Error during initDb().");
			log.error(e.getMessage(), e);
			throw new RuntimeException();
		}
	}
	
	private static class ShutdownHook extends Thread
	{
		public void run()
		{
	    	sismonSendMetric(getProperty("visual.track.metric.status"), "0");
	    	closeSismon();
		}
	}
}
