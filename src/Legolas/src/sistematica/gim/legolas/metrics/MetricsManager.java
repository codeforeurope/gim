package sistematica.gim.legolas.metrics;

import java.net.MalformedURLException;
import java.util.Date;
import sistematica.gim.legolas.Configuration;
import sistematica.pbutils.FormatLogger;
import visualtrack.logger.client.VTLoggerMetric;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

/**
 * Intesa per essere un thread a parte, questa classe gestisce l'invio periodico
 * delle metriche di Legolas al manager tramite l'accrocco prodigioso di Bani.
 */
public class MetricsManager extends Thread {

    private static final FormatLogger logger = FormatLogger.getLogger(MetricsManager.class);
    private Configuration configuration;
    private RuntimeMetrics metrics;
    private boolean mustRun = true;

    /**
     * Crea una nuova istanza di MetricsManager.
     * 
     * @param configuration la configurazione del programma
     * @param metrics contiene le metriche che verranno inviate al manager.
     */
    public MetricsManager(Configuration configuration, RuntimeMetrics metrics) {
        this.configuration = configuration;
        this.metrics = metrics;
    }

    @Override
    public void run() {
        try {
            VTLoggerThreadPool.init(configuration.VT_LOGGER_THREADS, configuration.VT_LOGGER_TIMEOUT_SECS * 1000);
            VTLoggerMetric vtmlog = new VTLoggerMetric(configuration.VT_LOGGER_URL, true);

            logger.info("MetricsManager started");

            while (mustRun) {
                try {
                    long now = System.currentTimeMillis();
                    vtmlog.sendValue("LEGOLAS_TEST", "1", now);
                    vtmlog.sendValue("LEGOLAS_STATUS", "1", now);
                    vtmlog.sendValue("LEGOLAS_DONE", "" + metrics.getEdgesWithStats(), now);
                    vtmlog.sendValue("LEGOLAS_BIDIREZ", "" + metrics.getBidirectionalEdges(), now);
                    vtmlog.sendValue("LEGOLAS_NODATA", "" + metrics.getJobErrors(), now);
                    vtmlog.sendValue("LEGOLAS_DONE", "" + metrics.getSamples(), now);

                    long target = new Date().getTime() + configuration.METRICS_PERIOD_SECS * 1000;
                    while (mustRun && new Date().getTime() < target) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    logger.warn("MetricsManager sleep interrupted", ex);
                }
            }
            vtmlog.sendValue("LEGOLAS_STATUS", "0", System.currentTimeMillis());
            VTLoggerThreadPool.terminateExecutorService(false);
            logger.info("MetricsManager stopped");
        } catch (MalformedURLException ex) {
            logger.warn("Error while starting MetricsManager", ex);
        }
    }

    /**
     * Ferma il thread.
     */
    public void halt() {
        logger.info("Stopping MetricsManager...");
        mustRun = false;
    }
}
