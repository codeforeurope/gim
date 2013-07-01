package sistematica.gim.legolas.entity;

/**
 * Identificatore univoco di una strada censita.
 */
public class StreetKey {

    private long idStreet;
    private StreetDirection direction;

    /**
     * Crea una nuova istanza di StreetKey.
     *
     * @param idStreet l'ID della strada
     * @param direction la direzione della strada (0 o 1)
     */
    public StreetKey(long idStreet, StreetDirection direction) {
        this.idStreet = idStreet;
        this.direction = direction;
    }

    /**
     * @return l'ID della strada
     */
    public long getIdStreet() {
        return idStreet;
    }

    /**
     * @return la direzione della strada (0 o 1)
     */
    public StreetDirection getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("(idStreet: %d, direction: %s)", idStreet, direction);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StreetKey other = (StreetKey) obj;
        if (this.idStreet != other.idStreet) {
            return false;
        }
        if (this.direction != other.direction) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
