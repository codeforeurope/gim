package sistematica.gim.legolas.scheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import sistematica.gim.legolas.Configuration;
import sistematica.pbutils.FormatLogger;

/**
 * Una coda di job, concepita per essere un thread a parte, che esegue i job
 * sequenzialmente secondo lo schema FIFO.
 */
public class JobQueue implements Runnable {

    private final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final FormatLogger logger = FormatLogger.getLogger(JobQueue.class);
    private String name;
    private BlockingQueue<JobDescriptor> jobs = new LinkedBlockingQueue<JobDescriptor>();
    private JobDescriptor currentJob;
    private int jobsDone = 0;
    private boolean mustStop = false;

    /**
     * Inizializza lo scheduler.
     * 
     * @param name il nome dello scheduler
     */
    public JobQueue(String name, Configuration configuration) throws SQLException {
        this.name = name;
    }

    /**
     * Aggiunge un job a quelli da eseguire.
     * @param descriptor il descrittore del job
     */
    public void scheduleJob(JobDescriptor descriptor) throws InterruptedException {
        jobs.put(descriptor);
    }

    @Override
    public void run() {
        while (!mustStop) {
            try {
                nextJobDescriptor();
                if (currentJob != null) { // Eseguo il job solo se nextJobDescriptor() ne ha trovato uno in coda
                    new Job(currentJob).run();
                    jobsDone++;
                    resetCurrentJobDescriptor();
                }
            } catch (InterruptedException ex) {
                logger.warn("SequentialScheduler %s interrupted while waiting for the next job", ex, name);
            } catch (Exception ex) {
                logger.error("Scheduler %s error", ex, name);
                if (currentJob != null) {
                    currentJob.getMetrics().addJobError();
                }
            }
        }

        logger.info("JobQueue %s stopped", name);
    }

    /**
     * Ferma la coda.
     */
    public void stop() {
        logger.info("Stopping JobQueue %s...", name);
        mustStop = true;
    }

    /**
     * Imposta currentJob al prossimo job descriptor presente nella coda. Se non
     * ve ne sono, aspetta per un secondo poi eventualmente imposta currentJob
     * a null.
     */
    private void nextJobDescriptor() throws InterruptedException {
        JobDescriptor descriptor = jobs.poll(1, TimeUnit.SECONDS);
        currentJob = descriptor;
    }

    /**
     * Imposta currentJob a null.
     */
    private void resetCurrentJobDescriptor() {
        currentJob = null;
    }

    /**
     * @return il job in corso.
     */
    public JobDescriptor getCurrentJob() {
        return currentJob;
    }

    /**
     * @return il numero di job in coda
     */
    public int size() {
        return jobs.size();
    }

    /**
     * @return la rappresentazione JSON dello stato della coda
     */
    public String toJSON() {
        int waiting = size();
        String running = getCurrentJob() != null ? DATE_TIME_FORMAT.format(getCurrentJob().getTimestamp()) : "none";
        return String.format("\"%s\": {\"waiting\": \"%d\", \"running\": \"%s\", \"done\": \"%d\",}", name, waiting, running, jobsDone);
    }
}
