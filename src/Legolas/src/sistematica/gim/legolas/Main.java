package sistematica.gim.legolas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.entity.Streets;
import sistematica.gim.legolas.experiments.ExperimentalJobDescriptorFactory;
import sistematica.gim.legolas.experiments.ExperimentalScheduler;
import sistematica.gim.legolas.metrics.MetricsManager;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.production.ProductionJobDescriptorFactory;
import sistematica.gim.legolas.production.ProductionScheduler;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobDescriptorFactory;
import sistematica.gim.legolas.scheduler.JobType;
import sistematica.gim.legolas.scheduler.Scheduler;
import sistematica.gim.legolas.web.WebServer;
import sistematica.pbutils.*;

/**
 * Classe principale del programma, con la main.
 */
public class Main {

    private static final FormatLogger logger = FormatLogger.getLogger(Main.class);
    private static final File stopFile = new File("ctl/stop");
    private Configuration configuration;
    private RoadGraph roadGraph;
    private Streets streets;
    private Scheduler scheduler;
    private WebServer webServer;
    private RuntimeMetrics metrics = new RuntimeMetrics();
    private MetricsManager metricsManager;
    private JobDescriptorFactory jobDescriptorFactory;

    /**
     * Inizializza il programma.
     *
     * @throws ClassNotFoundException se non è stata trovata la classe del
     * driver JDBC
     * @throws SQLException se c'è stato un errore nella connessione al DB
     * @throws IOException se c'è stato un errore nella lettura del file delle
     * properties
     * @throws FileNotFoundException se il file delle properties non esiste o
     * non e' accessibile
     */
    private void init() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {

        stopFile.delete();

        DateTime.setUTCTimeZone(); // Qualunque sia l'orario dell'host Legolas lavora in UTC!
        DAO.dbms = DBMS.MYSQL; // In questo modo DAO.formatQuery scrive i timestamp nel formato corretto

        // Caricamento del file di configurazione
        File propertiesFile = new File("cfg/legolas.properties");
        Properties properties = new Properties();
        properties.load(new FileReader(propertiesFile));

        org.apache.log4j.PropertyConfigurator.configure(properties);

        configuration = new Configuration();
        configuration.load(properties);

        Class.forName(configuration.DB_DRIVER);

        // Cerca di connettersi al DB (bloccante!)
        Connection connection = waitForDB();

        // Ora che c'è la connessione carica il grafo stradale
        roadGraph = new RoadGraph(configuration, connection);

        // Una volta caricato il grafo stradale carico anche i dettagli delle strade
        streets = new Streets(configuration, connection, roadGraph);

        // Istanzia la factory dei descriptor e lo scheduler adatti alla
        // modalità con cui Legolas è in esecuzione (sperimentale o produzione)
        if (configuration.MODE_EXPERIMENTAL) {
            jobDescriptorFactory = new ExperimentalJobDescriptorFactory(configuration, roadGraph, metrics);
            scheduler = new ExperimentalScheduler(configuration, roadGraph, metrics);
        } else {
            jobDescriptorFactory = new ProductionJobDescriptorFactory(configuration, roadGraph, streets, metrics);
            scheduler = new ProductionScheduler(configuration, roadGraph, metrics);
        }

        // Si fa partire il server web embedded
        webServer = new WebServer(configuration, scheduler, jobDescriptorFactory);

        // Infine parte il thread per l'invio delle metriche
        if (configuration.VT_LOGGER_ENABLED) {
            metricsManager = new MetricsManager(configuration, metrics);
        }
    }

    /**
     * Aspetta che il database sia disponibile poi restituisce una connessione
     * allo stesso.
     *
     * @return la connessione al DB
     */
    private Connection waitForDB() {
        Connection connection = null;
        do {
            try {
                connection = DriverManager.getConnection(configuration.DB_URL, configuration.DB_USER, configuration.DB_PASSWORD);
            } catch (SQLException ex) {
                logger.error("DB connection error, trying again in 5 seconds...", ex);
                try {
                    Thread.sleep(5000);
                    if (stopFile.exists()) { // Deve essere possibile fermare Legolas anche quando cerca di connettersi al DB
                        System.exit(0);
                    }
                } catch (InterruptedException ex1) {
                    // Nothing to do...
                }
            }
        } while (connection == null);
        return connection;
    }

    /**
     * Rimane in attesa fino al superamento del timestamp specificato. Se nel
     * frattempo lo scheduler viene terminato, interrompe l'attesa.
     *
     * @param deadline il timestamp superato il quale finisce l'attesa
     */
    private void waitUntil(Date deadline) {
        long now = new Date().getTime();
        long target = deadline.getTime();

        logger.debug("Sleeping until %s", deadline);
        while (now < target && !stopFile.exists()) {
            try {
                Thread.sleep(1000);
                now = new Date().getTime();
            } catch (InterruptedException ex) {
                logger.warn("Legolas interrupted", ex);
            }
        }
    }

    /**
     * Calcola le statistiche a partire dai dati che arrivano man mano, partendo
     * dalla data e dall'ora corrente. Rimane anche in attesa per i job storici.
     *
     * @throws InterruptedException se viene interrota la sleep
     * @throws SQLException se c'è un errore nella creazione dei job descriptor
     */
    private void runProduction() throws InterruptedException, SQLException {
        Date deadline = DateTime.nextDateWithMinutesDivisibleBy(new Date(), configuration.PERIOD_MINUTES);
        Date previousDeadline = null;
        
        while (!stopFile.exists()) {
            while (previousDeadline != null && previousDeadline.getTime() == deadline.getTime() && !stopFile.exists()) {
                deadline = DateTime.nextDateWithMinutesDivisibleBy(new Date(), configuration.PERIOD_MINUTES);
                Thread.sleep(1000);
            }

            waitUntil(deadline);

            if (!stopFile.exists()) { // Il programma potrebbe essere stato terminato durante la waitUntil
                JobDescriptor descriptor = jobDescriptorFactory.newJob(JobType.REAL_TIME, deadline);
                scheduler.schedule(descriptor);
                previousDeadline = deadline;
            }
        }
    }

    /**
     * Rimane in attesa di job sperimentali.
     *
     * @throws InterruptedException se viene interrota la sleep
     */
    private void runExperimental() throws InterruptedException {
        while (!stopFile.exists()) {
            Thread.sleep(1000);
        }
    }

    /**
     * Avvia Legolas in modalità "produzione" o "sperimentale", a seconda della
     * configurazione.
     */
    public void run() {
        try {
            init();

            if (NetUtils.isPortAvailable(configuration.WEB_PORT)) {

                scheduler.start();
                webServer.start();

                if (configuration.VT_LOGGER_ENABLED) {
                    metricsManager.start();
                }

                if (configuration.MODE_EXPERIMENTAL) {
                    runExperimental();
                } else {
                    runProduction();
                }

                scheduler.stop();
                webServer.stop();

                if (configuration.VT_LOGGER_ENABLED) {
                    metricsManager.halt();
                }
            } else {
                logger.error("Port %d is already in use. Maybe another instance of Legolas is running?", configuration.WEB_PORT);
            }
        } catch (Exception ex) {
            logger.error("Legolas error", ex);
        }

        stopFile.delete();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
