package sistematica.gim.legolas.entity;

/**
 * Rappresenta un dettaglio delle strade censite.
 */
public class StreetDetail implements Comparable<StreetDetail> {

    private StreetKey key;
    private long id;
    private double freeFlowSpeed;

    /**
     * Crea una nuova istanza di StreetDetail.
     *
     * @param key identificativo univoco della strada censita (idStreet e direzione)
     * @param direction la direzione dell'arco (0 o 1)
     * @param freeFlowSpeed il limite di velocità dell'arco
     */
    public StreetDetail(StreetKey key, long id, double freeFlowSpeed) {
        this.key = key;
        this.id = id;
        this.freeFlowSpeed = freeFlowSpeed;
    }

    /**
     * @return l'identificativo univoco della strada censita (idStreet e direzione)
     */
    public StreetKey getKey() {
        return key;
    }

    /**
     * @return l'ID dell'arco
     */
    public long getId() {
        return id;
    }
    
    /**
     * @return il limite di velocità dell'arco
     */
    public double getFreeFlowSpeed() {
        return freeFlowSpeed;
    }
    
    @Override
    public String toString() {
        return String.format("%s, ID: %s, freeFlowSpeed: %f", key, id, freeFlowSpeed);
    }

    @Override
    public int compareTo(StreetDetail that) {
        return that == null ? 1 : Long.compare(this.id, that.id);
    }
}
