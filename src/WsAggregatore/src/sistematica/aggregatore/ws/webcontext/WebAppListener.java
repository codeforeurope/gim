package sistematica.aggregatore.ws.webcontext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sistematica.aggregatore.ws.settings.Configuration;
import sistematica.aggregatore.ws.settings.WebSettings;
import sistematica.mobiworkdb.ds.DataSources;



/**
 * @author Alessio Rossini
 * @dateTime 11/mar/2011 16.12.36
 */
public class WebAppListener implements ServletContextListener
{

    private static final Logger log4j = Logger.getLogger(WebAppListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
     * ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sctxe)
    {
        log4j.info("Context " + sctxe.getServletContext().getContextPath() + " DESTROYED");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
     * .ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sctxe)
    {
        ServletContext ctx = sctxe.getServletContext();
        _init(ctx);
    }

    private boolean _init(ServletContext ctx)
    {
        boolean ret = false;
//        BasicConfigurator.configure();
//        Logger.getRootLogger().setLevel(Level.DEBUG);
        System.out.println("Initializing....");

        InputStream is = null;
        try
        {
            String ctxName = ctx.getContextPath();
            if (ctxName != null && ctxName.length() != 0)
                ctxName = ctxName.substring(1);
            String path = "/WEB-INF/wsaggregatore.properties";

            is = ctx.getResourceAsStream(path);

            Configuration.init(WebSettings.class, is);

            System.out.println("Properties loaded.");
            WebSettings.print();
            PropertyConfigurator.configure(WebSettings.getProperties());
            log4j.info("Log Configured");

            initDB(WebSettings.DATASOURCE_NAME);
            ret = true;
        }
        catch (Throwable ex)
        {
            log4j.fatal(ex, ex);
        }
        finally
        {
            if (is != null)
                try
                {
                    is.close();
                }
                catch (Exception e)
                {
                }
        }
        return ret;

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

    private String _getPropFileName(String baseName) throws Exception
    {
//        String hostName = System.getProperty(WebSettings.KEY_NOME_AMBIENTE, "");
    	
    	String hostName = getHostName();
        
        if (baseName == null || baseName.length() == 0)
            baseName = "WsAggregatore";
        else
            baseName = baseName.toLowerCase();

        String fileName = (baseName + (hostName.length() != 0 ? "_" + hostName.trim() : "") + ".properties").toLowerCase();

        System.out.println("Initialization file: " + fileName);

        return fileName;
    }
    
    private String getHostName() throws Exception
    {
    	String hostName = "";
    	String exec = "";
    	
    	String OS = System.getProperty("os.name");
    	System.out.println("Operative System: " + OS);
		
		BufferedReader stdInput = null;
		Process process = null;
		
		if(OS.equalsIgnoreCase("LINUX"))
		{
			exec = "hostname";
			
			process = Runtime.getRuntime().exec(exec);
			process.waitFor();
			
			stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			if(stdInput.equals(null))
				throw new Exception("Exception: An error occurr excuting " + exec);
			
			if ((hostName = stdInput.readLine()) != null) 
			{ 
				System.out.println(exec + " executed  = " + hostName);
			}			
		}
		else if(OS.toUpperCase().contains("WINDOWS"))
		{
			exec = "HOSTNAME";
			
			process = Runtime.getRuntime().exec(exec);
			process.waitFor();
			
			stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			if(stdInput.equals(null))
				throw new Exception("Exception: An error occurr excuting " + exec);
			
			if ((hostName = stdInput.readLine()) != null) 
			{ 
				System.out.println(exec + " executed  = " + hostName);
			}
		}
    	
    	return hostName;
    }
}
