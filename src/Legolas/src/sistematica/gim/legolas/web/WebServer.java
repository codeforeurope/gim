package sistematica.gim.legolas.web;

import sistematica.gim.legolas.scheduler.Scheduler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.JobDescriptorFactory;
import sistematica.pbutils.FormatLogger;

public class WebServer {

    public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final FormatLogger logger = FormatLogger.getLogger(WebServer.class);
    private Configuration configuration;
    private Server jetty;
    private VelocityEngine velocity;
    private Connection connection;

    /**
     * Inizializza il server web integrato.
     * 
     * @param configuration la configurazione del programma
     * @param scheduler lo scheduler
     * @throws SQLException se c'è un errore nella connessione al DB
     */
    public WebServer(Configuration configuration, Scheduler scheduler, JobDescriptorFactory jobDescriptorFactory) throws SQLException {
        this.configuration = configuration;

        // Connessione al DB
        connection = DriverManager.getConnection(configuration.DB_URL, configuration.DB_USER, configuration.DB_PASSWORD);
        connection.setAutoCommit(true);

        // Server web embedded
        jetty = new Server(configuration.WEB_PORT);
        jetty.setAttribute("legolas", scheduler);

        // Templating engine
        velocity = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", "www/tpl");
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        velocity.init(p);

        // Resource handler per i file statici
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase("www/res");

        // Resource handler per le servlet
        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");
        servletHandler.addServlet(new ServletHolder(new LoginServlet(configuration, velocity, scheduler)), "/login");
        servletHandler.addServlet(new ServletHolder(new LogoutServlet(configuration, velocity, scheduler)), "/logout");
        servletHandler.addServlet(new ServletHolder(new MonitorServlet(configuration, velocity, scheduler)), "/monitor");
        servletHandler.addServlet(new ServletHolder(new AjaxMetricsServlet(configuration, velocity, scheduler)), "/ajax/metrics");
        servletHandler.addServlet(new ServletHolder(new VelocityServlet(configuration, velocity, scheduler, "log.html")), "/log");
        servletHandler.addServlet(new ServletHolder(new AjaxLogServlet(configuration, velocity, scheduler)), "/ajax/log");
        servletHandler.addServlet(new ServletHolder(new JobsServlet(configuration, velocity, scheduler)), "/jobs");
        servletHandler.addServlet(new ServletHolder(new AjaxJobsServlet(configuration, velocity, scheduler, jobDescriptorFactory)), "/ajax/jobs");
        servletHandler.addServlet(new ServletHolder(new ExperimentsServlet(configuration, velocity, scheduler)), "/experiments");
        servletHandler.addServlet(new ServletHolder(new AjaxExperimentsServlet(configuration, velocity, scheduler, connection, jobDescriptorFactory)), "/ajax/experiments");
        servletHandler.addServlet(new ServletHolder(new VelocityServlet(configuration, velocity, scheduler, "comparison.html")), "/comparison");
        servletHandler.addServlet(new ServletHolder(new AjaxComparisonServlet(configuration, velocity, scheduler, connection)), "/ajax/comparison");

        // Combinazione dei due resource handler (file statici e servlet)
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, servletHandler});
        jetty.setHandler(handlers);
    }

    /**
     * Avvia il server web.
     */
    public void start() {
        try {
            jetty.start();
            logger.info("WebServer started on port %d", configuration.WEB_PORT);
        } catch (Exception ex) {
            logger.error("Error starting Legolas WebServer", ex);
        }
    }

    /**
     * Ferma il server web.
     */
    public void stop() {
        try {
            logger.info("Stopping WebServer...");
            jetty.stop();
            connection.close();
            logger.info("WebServer stopped");
        } catch (Exception ex) {
            logger.error("Error stopping Legolas WebServer", ex);
        }
    }
}
