package sistematica.webcontext;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.*;
import javax.sql.DataSource;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sistematica.apptemplate.cfg.Configuration;
import sistematica.rdbms.Rdbms;
import sistematica.rdbms.RdbmsFactory;

////////////// counts the number of sessions that are bound to this object
public class WebAppListener implements HttpSessionAttributeListener, ServletContextListener {

    private static final Logger log = Logger.getLogger(WebAppListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicConfigurator.configure();

        ServletContext ctx = sce.getServletContext();

        InputStream is = null;
        try {
            String ctxName = ctx.getContextPath();
            if (ctxName != null && ctxName.length() != 0) {
                ctxName = ctxName.substring(1);
            }
            String path = "/WEB-INF/" + getPropFileName(ctxName);
            is = ctx.getResourceAsStream(path);

            if (is == null) {
                throw new NullPointerException("Couldn't find the configuration file (" + path + ")");
            }

            Properties cfg = new Properties();
            cfg.load(is);

            Configuration.init(WebSettings.class, cfg);

            PropertyConfigurator.configure(cfg);
            log.info("Log Configured");

            WebSettings.print();

            initDB();
            
            initDBSistema();

            StartUpAppl.init(cfg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static String getPropFileName(String baseName) throws Exception {
        String nomeAmbiente = System.getProperty(Settings.ENV_NAME_PROPERTY, null);
        if (nomeAmbiente == null) {
            throw new Exception("VARIABILE DI AMBIENTE '" + Settings.ENV_NAME_PROPERTY + "' NON DEFINITA.");
        }

        String fileName = baseName + "_" + nomeAmbiente.trim() + ".properties";

        log.info("Initialization file: " + fileName);

        return fileName;
    }

    private static void initDB() throws Exception {
        if (Settings.DATA_SOURCE_NAME == null) {
            log.warn("#####################################################################################");
            log.warn("##  NO DATASOURCE");
            log.warn("#####################################################################################");
            return;
        }

        DatabaseMetaData dbmd = null;
        Hashtable<String, Rdbms> rdbmsHash = new Hashtable<String, Rdbms>();

        Context initContext = new InitialContext();

        DataSource ds = (DataSource) initContext.lookup(Settings.DATA_SOURCE_NAME);
        
        log.info("ds is " + ds);

        DataSources.addDataSources(ds, Settings.DATA_SOURCE_NAME);

        Connection c = ds.getConnection();
        dbmd = (DatabaseMetaData) c.getMetaData();

        String DatabaseProductName = dbmd.getDatabaseProductName();
        c.close();

        // creo Rdbms per la connessione di sistema
        RdbmsFactory rdbmsFact = RdbmsFactory.getInstance();


        Rdbms rdbms = rdbmsFact.getRdbmsForDs(Settings.RDBMS_TYPE);
        rdbmsHash.put(DatabaseProductName, rdbms);

        log.info("#####################################################################################");
        log.info("DataSources Loaded .......... " + Settings.DATA_SOURCE_NAME);
        log.info("DatabaseProductName ......... " + DatabaseProductName);
        log.info("DriverName .................. " + dbmd.getDriverName() + " v. " + dbmd.getDriverVersion());
        log.info("JDBC URL .................... " + dbmd.getURL());
        log.info("#####################################################################################");

        int counter = 1;
        String key_ds_aux = Settings.DATA_SOURCE_NAME + "." + counter;

        String ds_aux_name = null;
        ;
        try {
            ds_aux_name = StartUpAppl.getProperty(key_ds_aux);
        } catch (Exception e) {
            ds_aux_name = null;
        }

        while (ds_aux_name != null) {
            if (ds_aux_name.length() > 0) {
                DataSource ds_aux = (DataSource) initContext.lookup(ds_aux_name);
                // if (SistDataSources.containsDs(ds_aux_name))
                if (DataSources.containsDs(ds_aux_name)) {
                    throw new Exception("Datasources '" + ds_aux_name + "' already defined. Please choose a unique name!");
                } else {
                    // SistDataSources.addDataSources(ds_aux,ds_aux_name);
                    DataSources.addDataSources(ds_aux, ds_aux_name);

                    // definisco la classe RDBMS
                    dbmd = (DatabaseMetaData) ds_aux.getConnection().getMetaData();
                    String aux_DatabaseProductName = dbmd.getDatabaseProductName();
                    if (!(rdbmsHash.containsKey(aux_DatabaseProductName))) {
                        String class_name = StartUpAppl.getProperty(aux_DatabaseProductName);
                        rdbms = rdbmsFact.getRdbmsForDs(class_name);
                        rdbmsHash.put(aux_DatabaseProductName, rdbms);
                    }
                    log.info("#####################################################################################");
                    log.info("DataSources Loaded .......... " + ds_aux_name);
                    log.info("DatabaseProductName ......... " + dbmd.getDatabaseProductName());
                    log.info("DriverName .................. " + dbmd.getDriverName() + " v. " + dbmd.getDriverVersion());
                    log.info("rdbms ....................... " + rdbmsHash.get(aux_DatabaseProductName).getClass());
                    log.info("#####################################################################################");

                }
            }

            counter++;
            try {
                key_ds_aux = Settings.DATA_SOURCE_NAME + "." + counter;
                System.out.println("\n\n\n\n" + "key_ds_aux =" + key_ds_aux + "\n\n\n\n");
                ds_aux_name = StartUpAppl.getProperty(key_ds_aux);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                ds_aux_name = null;
            }
        }

        // SistDataSources.setRdbmshash(rdbmsHash);
        DataSources.setRdbmshash(rdbmsHash);
    }
    
   private static void initDBSistema() throws Exception {
        if (Settings.SISTEMA_DATA_SOURCE_NAME == null) {
            log.warn("#####################################################################################");
            log.warn("##  NO DATASOURCE SISTEMA");
            log.warn("#####################################################################################");
            return;
        }
        System.out.println("Settings.SISTEMA_DATA_SOURCE_NAME "+Settings.SISTEMA_DATA_SOURCE_NAME);

        DatabaseMetaData dbmd = null;
        Hashtable<String, Rdbms> rdbmsHash = new Hashtable<String, Rdbms>();

        Context initContext = new InitialContext();

        DataSource ds = (DataSource) initContext.lookup(Settings.SISTEMA_DATA_SOURCE_NAME);
        log.info("ds sistema is " + ds);

        DataSources.addDataSources(ds, Settings.SISTEMA_DATA_SOURCE_NAME);

        Connection c = ds.getConnection();
        dbmd = (DatabaseMetaData) c.getMetaData();

        String DatabaseProductName = dbmd.getDatabaseProductName();
        c.close();

        // creo Rdbms per la connessione di sistema
        RdbmsFactory rdbmsFact = RdbmsFactory.getInstance();


        Rdbms rdbms = rdbmsFact.getRdbmsForDs(Settings.RDBMS_TYPE);
        rdbmsHash.put(DatabaseProductName, rdbms);

        log.info("#####################################################################################");
        log.info("DataSources Sistema Loaded .......... " + Settings.SISTEMA_DATA_SOURCE_NAME);
        log.info("DatabaseProductName ......... " + DatabaseProductName);
        log.info("DriverName .................. " + dbmd.getDriverName() + " v. " + dbmd.getDriverVersion());
        log.info("JDBC URL .................... " + dbmd.getURL());
        log.info("#####################################################################################");

        int counter = 1;
        String key_ds_aux = Settings.SISTEMA_DATA_SOURCE_NAME + "." + counter;

        String ds_aux_name = null;
        ;
        try {
            ds_aux_name = StartUpAppl.getProperty(key_ds_aux);
        } catch (Exception e) {
            ds_aux_name = null;
        }

        while (ds_aux_name != null) {
            if (ds_aux_name.length() > 0) {
                DataSource ds_aux = (DataSource) initContext.lookup(ds_aux_name);
                // if (SistDataSources.containsDs(ds_aux_name))
                if (DataSources.containsDs(ds_aux_name)) {
                    throw new Exception("Datasources Sistema '" + ds_aux_name + "' already defined. Please choose a unique name!");
                } else {
                    // SistDataSources.addDataSources(ds_aux,ds_aux_name);
                    DataSources.addDataSources(ds_aux, ds_aux_name);

                    // definisco la classe RDBMS
                    dbmd = (DatabaseMetaData) ds_aux.getConnection().getMetaData();
                    String aux_DatabaseProductName = dbmd.getDatabaseProductName();
                    if (!(rdbmsHash.containsKey(aux_DatabaseProductName))) {
                        String class_name = StartUpAppl.getProperty(aux_DatabaseProductName);
                        rdbms = rdbmsFact.getRdbmsForDs(class_name);
                        rdbmsHash.put(aux_DatabaseProductName, rdbms);
                    }
                    log.info("#####################################################################################");
                    log.info("DataSources Sistema Loaded .......... " + ds_aux_name);
                    log.info("DatabaseProductName ......... " + dbmd.getDatabaseProductName());
                    log.info("DriverName .................. " + dbmd.getDriverName() + " v. " + dbmd.getDriverVersion());
                    log.info("rdbms ....................... " + rdbmsHash.get(aux_DatabaseProductName).getClass());
                    log.info("#####################################################################################");

                }
            }

            counter++;
            try {
                key_ds_aux = Settings.SISTEMA_DATA_SOURCE_NAME + "." + counter;
                System.out.println("\n\n\n\n" + "key_ds_aux =" + key_ds_aux + "\n\n\n\n");
                ds_aux_name = StartUpAppl.getProperty(key_ds_aux);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                ds_aux_name = null;
            }
        }

        // SistDataSources.setRdbmshash(rdbmsHash);
        DataSources.setRdbmshash(rdbmsHash);
    }
}