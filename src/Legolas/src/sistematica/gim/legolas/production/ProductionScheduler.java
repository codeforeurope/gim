package sistematica.gim.legolas.production;

import java.sql.SQLException;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobQueue;
import sistematica.gim.legolas.scheduler.Scheduler;
import sistematica.pbutils.FormatLogger;

/**
 * Scheduler di produzione. Gestisce la coda dei job real-time (schedulati) e
 * quella dei job storici.
 */
public class ProductionScheduler extends Scheduler {

    private static final FormatLogger logger = FormatLogger.getLogger(ProductionScheduler.class);
    private JobQueue realTimeQueue;
    private JobQueue historicQueue;

    /**
     * Crea una nuova istanza della classe.
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param metrics le metriche del programma
     */
    public ProductionScheduler(Configuration configuration, RoadGraph roadGraph, RuntimeMetrics metrics) throws SQLException {
        super(configuration, roadGraph, metrics);
        realTimeQueue = new JobQueue("real-time", configuration);
        historicQueue = new JobQueue("historic", configuration);
    }

    @Override
    public void schedule(JobDescriptor descriptor) throws InterruptedException {
        switch (descriptor.getJobType()) {
            case REAL_TIME:
                metrics.addRealTimeJob();
                realTimeQueue.scheduleJob(descriptor);
                logger.info("Real time job with timestamp %s scheduled", descriptor.getTimestamp());
                break;
            case HISTORIC:
                metrics.addHistoricJob();
                historicQueue.scheduleJob(descriptor);
                logger.info("Historic job with timestamp %s scheduled", descriptor.getTimestamp());
                break;
            default:
                throw new UnsupportedOperationException("Bad job type: " + descriptor);
        }
    }

    @Override
    public void start() {
        new Thread(realTimeQueue).start();
        new Thread(historicQueue).start();
        logger.info("Scheduler started (production mode)");
    }

    @Override
    public void stop() {
        super.stop();
        realTimeQueue.stop();
        historicQueue.stop();
        logger.info("Scheduler stopped (production mode)");
    }

    /**
     * @return la rappresentazione JSON dello stato delle varie code dello scheduler
     */
    @Override
    public String toJSON() {
        return String.format("{%s, %s,}", realTimeQueue.toJSON(), historicQueue.toJSON());
    }
}
