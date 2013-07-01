package sistematica.gim.legolas.algorithm;

/**
 * Modelli di calcolo per la densità veicolare a partire da velocità media e
 * caratteristiche della strada supportati da Legolas.
 */
public enum DensityModel {

    /**
     * Modello lineare di Greenshield.
     */
    GREENSHIELD,
    /**
     * Generatore casuale.
     */
    RANDOM,
    /**
     * Non calcola la densità (è sempre NaN).
     */
    NONE;

    @Override
    public String toString() {
        switch (this) {
            case GREENSHIELD:
                return "greenshield";
            case RANDOM:
                return "random";
            case NONE:
                return "none";
            default:
                return "unknown";
        }

    }

    /**
     * @return la rappresentazione JSON del modello per il calcolo della densità
     */
    public String toJSON() {
        return "\"" + toString() + "\"";
    }

    /**
     * Factory per l'enumerazione a partire dal nome sotto forma di stringa
     * @param name il nome del valore dell'enumerazione desiderato
     * @return il valore dell'enumerazione desiderato o NONE se non è stato trovato
     */
    public static DensityModel fromString(String name) {
        if ("greenshield".equalsIgnoreCase(name)) {
            return GREENSHIELD;
        } else if ("RANDOM".equalsIgnoreCase(name)) {
            return RANDOM;
        } else {
            return NONE;
        }
    }
}
