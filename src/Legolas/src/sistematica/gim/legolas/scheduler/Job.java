package sistematica.gim.legolas.scheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.Sample;
import sistematica.gim.legolas.entity.TrafficStat;
import sistematica.pbutils.DateTime;
import sistematica.pbutils.FormatLogger;

/**
 * Classe che esegue il compito principale del programma, calcolare e salvare
 * su DB le statistiche sul traffico a partire dalle rilevazioni FCD. Può fare
 * questo lavoro in loop oppure on-demand.
 */
public class Job {

    private static final FormatLogger logger = FormatLogger.getLogger(Job.class);
    private JobDescriptor descriptor;

    /**
     * Crea una nuova istanza di Job.
     * 
     * @param descriptor il descrittore del job
     */
    public Job(JobDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Esegue il job.
     * 
     * @throws Exception se qualcosa va storto durante l'esecuzione del job...
     */
    public void run() throws Exception {
        logger.debug("It's running! (on %s)", System.getProperty("os.name"));

        // 0) Mi connetto al DB
        logger.debug("Opening DB connection...");
        Configuration configuration = descriptor.getConfiguration();
        Connection connection = DriverManager.getConnection(configuration.DB_URL, configuration.DB_USER, configuration.DB_PASSWORD);
        connection.setAutoCommit(false);

        // 1) Leggo i dati dal DB e li do in pasto al processore
        long start = System.currentTimeMillis();
        InputTask input = new InputTask(descriptor, connection);

        ProcessingTask processor = new ProcessingTask(descriptor, connection);
        Sample sample;
        while ((sample = input.getNextSample()) != null) {
            processor.addSample(sample);
        }
        long elapsed = System.currentTimeMillis() - start;

        logger.info("----------------------------------------------------");
        logger.info("INPUT (%s)", descriptor.getTimestamp());
        logger.info("    Samples: %d", input.getSamples());
        logger.info("    Samples maximum age: %d mins", descriptor.getConfiguration().MAX_INPUT_AGE_MINUTES);
        logger.info("    Samples on bidirectional edges: %d", input.getSamplesOnBidirectionalEdges());
        logger.info("    Time elapsed: %s", DateTime.msecsToStr(elapsed));
        logger.info("----------------------------------------------------");

        // 2) Leggo i risultati del processore e li scrivo sul DB
        start = System.currentTimeMillis();
        OutputTask output = descriptor.getOutputTaskClass().getDeclaredConstructor(JobDescriptor.class, Connection.class).newInstance(descriptor, connection);
        TrafficStat stat;
        while ((stat = processor.getNextStat()) != null) {
            output.addStat(stat);
        }
        output.flush();
        elapsed = System.currentTimeMillis() - start;

        logger.info("----------------------------------------------------");
        logger.info("OUTPUT (%s)", descriptor.getTimestamp());
        logger.info("    Edges with statistics: %d", output.getEdgesWithStats());
        logger.info("    Time elapsed: %s", DateTime.msecsToStr(elapsed));
        logger.info("----------------------------------------------------");

        // 3) Chiusura della connessione al DB
        logger.debug("Closing DB connection...");
        connection.close();
    }
}
