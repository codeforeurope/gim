package sistematica.gim.legolas.scheduler;

import java.sql.SQLException;
import java.util.Date;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.algorithm.DensityModel;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.entity.Streets;
import sistematica.gim.legolas.metrics.RuntimeMetrics;

/**
 * Bean coi parametri associati ad un {@link Job}. Evita signature lunghissime.
 */
public class JobDescriptor {

    private Configuration configuration;
    private RoadGraph roadGraph;
    private RuntimeMetrics metrics;
    private JobType jobType;
    private Date timestamp;
    private String sources; // "S1, S2, S3", null o "" per tutte le sorgenti
    private DensityModel densityModel;
    private Class<? extends OutputTask> outputTaskClass;
    private Streets streets;

    /**
     * Crea una nuova istanza del descrittore.
     *
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param streets le strade censite
     * @param metrics le metriche del programma
     * @param jobType il tipo di job (vedi {@link JobType})
     * @param timestamp il timestamp che verrà associato alle statistiche che
     * verranno scritte su DB
     * @param sources le sorgenti dati FCD (ID separati da virgola oppure
     * null/"" per tutte le sorgenti)
     * @param densityModel il modello per il calcolo della densità veicolare
     * @param outputTaskClass sottoclasse di {@link OutputTask} che verrà usata
     * dal job
     * @throws SQLException se c'è un errore nelle query o nella connessione al
     * DB
     */
    public JobDescriptor(
            Configuration configuration,
            RoadGraph roadGraph,
            Streets streets,
            RuntimeMetrics metrics,
            JobType jobType,
            Date timestamp,
            String sources,
            DensityModel densityModel,
            Class<? extends OutputTask> outputTaskClass) throws SQLException {
        this.configuration = configuration;
        this.roadGraph = roadGraph;
        this.streets = streets;
        this.metrics = metrics;
        this.jobType = jobType;
        this.timestamp = timestamp;
        this.sources = sources;
        this.densityModel = densityModel;
        this.outputTaskClass = outputTaskClass;
    }

    /**
     * @return la configurazione del programma
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return il grafo stradale di riferimento
     */
    public RoadGraph getRoadGraph() {
        return roadGraph;
    }

    /**
     * @return le strade censite
     */
    public Streets getStreets() {
        return streets;
    }

    /**
     * @return le metriche del programma
     */
    public RuntimeMetrics getMetrics() {
        return metrics;
    }

    /**
     * @return il modello di calcolo della densità veicolare
     */
    public DensityModel getDensityModel() {
        return densityModel;
    }

    /**
     * @return il tipo di job
     */
    public JobType getJobType() {
        return jobType;
    }

    /**
     * @return la data e l'ora per cui verrà eseguito il job
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @return le sorgenti dati FCD (ID separati da virgola o null/"" per tutte
     * le sorgenti)
     */
    public String getSources() {
        return sources;
    }

    /**
     * @return la sottoclasse di {@link OutputTask} che verrà usata dal job
     */
    public Class<? extends OutputTask> getOutputTaskClass() {
        return outputTaskClass;
    }

    /**
     * @return la rappresentazione JSON di questo descrittore
     */
    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(String.format("\"jobType\": %s,", jobType.toJSON()));
        builder.append(String.format("\"timestamp\": \"%s\",", timestamp));
        builder.append("}");
        return builder.toString();
    }
}
