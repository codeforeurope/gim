package sistematica.gim.sisasdiss.conf;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;
import java.util.Timer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sistematica.gim.sisasdiss.utils.FileUtils;
import sistematica.gim.sisasdiss.utils.MetricTask;
import sistematica.mobiworkdb.ds.DataSources;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

public class Listener implements ServletContextListener {

	public static Logger log4j = Logger.getLogger(Listener.class);
	
    @Override
    public void contextInitialized(ServletContextEvent event) 
    {
        ServletContext context = event.getServletContext();
        InputStream input = null;
        Properties properties = new Properties();

        try 
        {
            String path = "/WEB-INF/sisasdiss.properties";
            input = context.getResourceAsStream(path);
            properties.load(input);
            PropertyConfigurator.configure(properties);

            input = context.getResourceAsStream(path);
            Configuration.init(WebSettings.class, input);
            WebSettings.print();
            
            Timer timer = null;
			MetricTask metricTask = null;
			if (WebSettings.SENDMGR_ENABLE)
			{
				timer = new Timer();
	        	metricTask = new MetricTask();
	        	timer.schedule(metricTask, 60000 , WebSettings.SENDMGR_TIME_MIN*60000);
	        	log4j.info("Start timer MetricTask ...");
			}
			
			if (WebSettings.FCD_ARCHIVE_FILE_ENABLE)
			{
				log4j.info("Checking paths...");
				FileUtils.makeDir(WebSettings.FCD_ARCHIVE_FILE_DIR);
			}
			
            initDB(WebSettings.DATASOURCE_NAME);

        } 
        catch (Exception e) 
        {
        	log4j.error(e,e);
        } 
        finally 
        {
            if (input != null) 
            {
                try 
                {
                    input.close();
                } 
                catch (IOException e) 
                {
                	log4j.error(e,e);
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) 
    {
    	if (WebSettings.SENDMGR_ENABLE)
		{
			VTLoggerThreadPool.terminateExecutorService(false);
		}
    }

    private void initDB(String dataSourceName) throws Exception
    {
        Context initContext = new InitialContext();

        DataSource ds = (DataSource) initContext.lookup(dataSourceName);

        DataSources.addDataSources(ds, dataSourceName);

        Connection c = ds.getConnection();
        DatabaseMetaData dbmd = (DatabaseMetaData) c.getMetaData();

        String databaseProductName = dbmd.getDatabaseProductName();
        c.close();

        log4j.info("#####################################################################################");
        log4j.info("DataSources Loaded .......... " + dataSourceName);
        log4j.info("DatabaseProductName ......... " + databaseProductName);
        log4j.info("DriverName .................. " + dbmd.getDriverName() + " v. " + dbmd.getDriverVersion());
        log4j.info("JDBC URL .................... " + dbmd.getURL());
        log4j.info("#####################################################################################");

    }    
}
