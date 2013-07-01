package sistematica.gim.legolas.scheduler;

import java.sql.SQLException;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.pbutils.FormatLogger;

/**
 * Riceve le richieste di esecuzione dei vari job e le esegue.
 */
public abstract class Scheduler {

    private static final FormatLogger logger = FormatLogger.getLogger(Scheduler.class);
    protected RuntimeMetrics metrics;
    protected Configuration configuration;
    protected RoadGraph roadGraph;
    private boolean running = true;

    /**
     * Crea una nuova istanza della classe.
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     */
    public Scheduler(Configuration configuration, RoadGraph roadGraph, RuntimeMetrics metrics) throws SQLException {
        this.configuration = configuration;
        this.roadGraph = roadGraph;
        this.metrics = metrics;
    }

    /**
     * Metodo non bloccante che avvia lo scheduler.
     */
    public abstract void start();

    /**
     * Ferma le {@link JobQueue} che fanno parte dello scheduler.
     */
    public void stop() {
        logger.info("Stopping Scheduler...");
        running = false;
    }

    /**
     * @return true se lo scheduler è attivo, false altrimenti
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Aggiunge un job alla lista di quelli da eseguire.
     * 
     * @param descriptor il descrittore del job da eseguire
     * @throws InterruptedException se lo scheduler è stato interrotto prima di poter schedulare il job
     */
    public abstract void schedule(JobDescriptor descriptor) throws InterruptedException;

    /**
     * @return le metriche del programma
     */
    public RuntimeMetrics getMetrics() {
        return metrics;
    }

    /**
     * @return la rappresentazione JSON dello stato delle varie code dello scheduler
     */
    public abstract String toJSON();
}
