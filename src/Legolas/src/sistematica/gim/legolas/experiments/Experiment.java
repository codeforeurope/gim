package sistematica.gim.legolas.experiments;

import java.util.Date;
import sistematica.gim.legolas.algorithm.DensityModel;

/**
 * Rappresenta un esperimento eseguibile da Legolas.
 */
public class Experiment {

    private long id;
    private String description;
    private String sources;
    private DensityModel densityModel;
    private Date timestamp;

    /**
     * Crea una nuova istanza di Experiment.
     * 
     * @param id l'ID dell'esperimento sul DB
     * @param description la descrizione
     * @param sources le sorgenti (sigle separate da virgola, se null significa "tutte le sorgenti")
     * @param densityModel modello di calcolo della densità a partire dalla velocità media e dalle caratteristiche della strada
     * @param timestamp timestamp di riferimento per l'esperimento
     */
    public Experiment(long id, String description, String sources, DensityModel densityModel, Date timestamp) {
        this.id = id;
        this.description = description;
        this.sources = sources;
        this.densityModel = densityModel;
        this.timestamp = timestamp;
    }

    /**
     * @return l'ID dell'esperimento sul DB
     */
    public long getId() {
        return id;
    }

    /**
     * @return la descrizione
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return le sorgenti (sigle separate da virgola, se null significa "tutte le sorgenti")
     */
    public String getSources() {
        return sources;
    }

    /**
     * @return modello di calcolo della densità a partire dalla velocità media e dalle caratteristiche della strada
     */
    public DensityModel getDensityModel() {
        return densityModel;
    }

    /**
     * @return timestamp di riferimento per l'esperimento
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @return la rappresentazione JSON dell'esperimento
     */
    public String toJSON() {
        return String.format("{\"id\": %d, \"description\": \"%s\", \"sources\": \"%s\", \"model\": \"%s\", \"timestamp\": \"%s\"}",
                id, description, sources, densityModel, timestamp);
    }
}
