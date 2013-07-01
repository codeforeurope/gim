package sistematica.gim.legolas.entity;

/**
 * Rapresenta la <b>direzione</b> dell'arco (lo chiamo direction per coerenza
 * con le colonne del DB che usano il termine "dir".
 *
 * Può assumere tre valori:
 *
 * <ul>
 *
 * <li>0 (REFERENCE): una direzione di riferimento, scelta arbitrariamente; di
 * solito il criterio è Nord-Sud, Est-Ovest oppure in senso orario.</li>
 *
 * <li>1 (NON_REFERENCE): la direzione opposta a quella di riferimento</li>
 *
 * </ul>
 */
public enum StreetDirection {

    /**
     * Direzione di riferimento (arbitraria).
     */
    REFERENCE(0),
    /**
     * Direzione opposta a quella di riferimento.
     */
    NON_REFERENCE(1);
    private final int value;

    /**
     * Crea un nuovo elemento dell'enumerazione.
     *
     * @param value il valore intero associato all'elemento
     */
    StreetDirection(int value) {
        this.value = value;
    }

    /**
     * @return il valore intero associato all'elemento dell'enumerazione
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value un valore intero (dovrebbe essere 1 o 0)
     * @return l'istanza dell'enumerazione associata al valore specificato
     */
    public static StreetDirection fromInt(int value) {
        switch (value) {
            case 0:
                return REFERENCE;
            default:
                return NON_REFERENCE;
        }
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
