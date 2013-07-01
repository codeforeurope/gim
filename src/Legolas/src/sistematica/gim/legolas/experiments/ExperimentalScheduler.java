package sistematica.gim.legolas.experiments;

import java.sql.SQLException;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobQueue;
import sistematica.gim.legolas.scheduler.Scheduler;
import sistematica.pbutils.FormatLogger;

/**
 * Scheduler di produzione. Gestisce la coda dei job sperimentali.
 */
public class ExperimentalScheduler extends Scheduler {

    private static final FormatLogger logger = FormatLogger.getLogger(ExperimentalScheduler.class);
    private JobQueue experimentalQueue;

    /**
     * Crea una nuova istanza della classe.
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param metrics le metriche del programma
     */
    public ExperimentalScheduler(Configuration configuration, RoadGraph roadGraph, RuntimeMetrics metrics) throws SQLException {
        super(configuration, roadGraph, metrics);
        experimentalQueue = new JobQueue("experimental", configuration);
    }

    @Override
    public void schedule(JobDescriptor descriptor) throws InterruptedException {
        switch (descriptor.getJobType()) {
            case EXPERIMENTAL:
                metrics.addExperimentalJob();
                experimentalQueue.scheduleJob(descriptor);
                logger.info("Experimental job with timestamp %s scheduled", descriptor.getTimestamp());
                break;
            default:
                throw new UnsupportedOperationException("Bad job type: " + descriptor);
        }
    }

    @Override
    public void start() {
        new Thread(experimentalQueue).start();
        logger.info("Scheduler started (experimental mode)");
    }

    @Override
    public void stop() {
        super.stop();
        experimentalQueue.stop();
        logger.info("Scheduler stopped (experimental mode)");
    }

    /**
     * @return la rappresentazione JSON dello stato delle varie code dello scheduler
     */
    @Override
    public String toJSON() {
        return String.format("{%s,}", experimentalQueue.toJSON());
    }
}
