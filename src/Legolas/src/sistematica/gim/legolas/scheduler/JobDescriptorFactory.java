package sistematica.gim.legolas.scheduler;

import java.sql.SQLException;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.entity.RoadGraph;

/**
 * Facilita la creazione dei descrittori dei job.
 */
public abstract class JobDescriptorFactory {

    protected Configuration configuration;
    protected RoadGraph roadGraph;
    protected RuntimeMetrics metrics;

    /**
     * Factory per la creazione semplificata dei vari job descriptor.
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param metrics le metriche del programma
     */
    public JobDescriptorFactory(Configuration configuration, RoadGraph roadGraph, RuntimeMetrics metrics) {
        this.configuration = configuration;
        this.roadGraph = roadGraph;
        this.metrics = metrics;
    }

    /**
     * Crea un nuovo job descriptor del tipo specificato.
     * 
     * @param type il tipo di job
     * @param args gli argomenti richiesti per creare il job
     * @return il job descriptor
     * @throws SQLException se c'è stato un errore nell'interazione col DB
     */
    public abstract JobDescriptor newJob(JobType type, Object... args) throws SQLException;
}
