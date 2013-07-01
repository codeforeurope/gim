package sistematica.gim.legolas.entity;

/**
 * Rappresenta un provider di dati raw FCD.
 */
public class Source {

    private String id;
    private String description;

    /**
     * Crea una nuova istanza di Source.
     * 
     * @param id l'ID della sorgente sul DB
     * @param description la descrizione della sorgente
     */
    public Source(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * @return l'ID della sorgente sul DB
     */
    public String getId() {
        return id;
    }

    /**
     * @return la descrizione della sorgente
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return restituisce la rappresentazione JSON della sorgente
     */
    public String toJSON() {
        return String.format("{\"id\": \"%s\", \"description\": \"%s\",}", id, description);
    }
}
