package sistematica.gim.legolas.entity;

/**
 * Rappresenta una strada censita
 */
public class Street {

    private StreetKey key;
    private long minId;
    private long maxId;
    private String name;
    private String dirName;

    /**
     * Crea una nuova istanza di Street.
     *
     * @param key la chiave che identifica univocamente la strada
     * @param minId l'ID minimo tra quelli degli archi della strada
     * @param maxId l'ID massimo tra quelli degli archi della strada
     * TODO: aggiornami
     */
    public Street(StreetKey key, long minId, long maxId, String name, String dirName) {
        this.key = key;
        this.minId = minId;
        this.maxId = maxId;
        this.name = name;
        this.dirName = dirName;
    }

    /**
     * @return la chiave che identifica univocamente la strada
     */
    public StreetKey getkey() {
        return key;
    }

    /**
     * @return l'ID minimo tra quelli degli archi della strada
     */
    public long getMinId() {
        return minId;
    }

    /**
     * @return l'ID massimo tra quelli degli archi della strada
     */
    public long getMaxId() {
        return maxId;
    }

    @Override
    public String toString() {
        return String.format("%s, minId: %d, maxId: %d", key, minId, maxId);
    }
}
