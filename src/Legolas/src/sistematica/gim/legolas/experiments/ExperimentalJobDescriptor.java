package sistematica.gim.legolas.experiments;

import java.sql.SQLException;
import java.util.Date;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.algorithm.DensityModel;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobType;
import sistematica.gim.legolas.scheduler.OutputTask;

public class ExperimentalJobDescriptor extends JobDescriptor {

    private Experiment experiment;

    /**
     * Crea una nuova istanza del descrittore.
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param metrics le metriche del programma
     * @param jobType il tipo di job (vedi {@link JobType})
     * @param timestamp il timestamp che verrà associato alle statistiche che verranno scritte su DB
     * @param sources le sorgenti dati FCD (ID separati da virgola oppure null/"" per tutte le sorgenti)
     * @param densityModel il modello per il calcolo della densità veicolare
     * @param outputTaskClass sottoclasse di {@link OutputTask} che verrà usata dal job
     * @param experiment l'esperimento a cui si riferisce il job
     * @throws SQLException se c'è un errore nelle query o nella connessione al DB
     */
    public ExperimentalJobDescriptor(
            Configuration configuration,
            RoadGraph roadGraph,
            RuntimeMetrics metrics,
            JobType jobType,
            Date timestamp,
            String sources,
            DensityModel densityModel,
            Class<? extends OutputTask> outputTaskClass,
            Experiment experiment) throws SQLException {
        // Ai job sperimentali non servono (per ora?) le strade censite
        super(configuration, roadGraph, null, metrics, jobType, timestamp, sources, densityModel, outputTaskClass);
        this.experiment = experiment;
    }

    /**
     * @return l'esperimento a cui si riferisce il job
     */
    public Experiment getExperiment() {
        return experiment;
    }
}
