package sistematica.gim.legolas.scheduler;

/**
 * Tipi di job supportati da Legolas.
 */
public enum JobType {

    /**
     * Aggiorna le informazioni in tempo reale e lo storico.
     */
    REAL_TIME,
    /**
     * Aggiorna soltanto lo storico.
     */
    HISTORIC,
    /**
     * Serve per effettuare test, anche comparativi.
     */
    EXPERIMENTAL;

    @Override
    public String toString() {
        switch (this) {
            case REAL_TIME:
                return "real-time";
            case HISTORIC:
                return "historic";
            case EXPERIMENTAL:
                return "experimental";
            default:
                return "unknown";
        }
    }

    /**
     * @return la rappresentazione JSON del tipo di job
     */
    public String toJSON() {
        return "\"" + toString() + "\"";
    }
}
