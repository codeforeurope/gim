package sistematica.gim.legolas.entity;

/**
 * Rapresenta l'<b>orientamento</b> dell'arco (lo chiamo direction per coerenza
 * con le colonne del DB che usano il termine "dirx", "dir" o "sdir" a seconda
 * dei casi).
 *
 * Può assumere tre valori:
 *
 * <ul>
 *
 * <li>1 (TAIL_TO_HEAD): l'arco è percorribile soltanto dall'<b>ultimo</b> al
 * <b>primo</b> segmento dello shape</li>
 *
 * <li>0 (BOTH): l'arco è percorribile in entrambi i sensi</li>
 *
 * <li>-1 (HEAD_TO_TAIL): l'arco è percorribile soltanto dal <b>primo</b>
 * all'<b>ultimo</b> segmento dello shape</li>
 *
 * </ul>
 */
public enum RoadGraphDirection {

    /**
     * L'arco è percorribile soltanto dall'ultimo al primo segmento dello shape.
     */
    TAIL_TO_HEAD(1),
    /**
     * L'arco è percorribile in entrambi i sensi.
     */
    BOTH(0),
    /**
     * L'arco è percorribile soltanto dal primo all'ultimo segmento dello shape.
     */
    HEAD_TO_TAIL(-1);
    private final int value;

    /**
     * Crea un nuovo elemento dell'enumerazione.
     *
     * @param value il valore intero associato all'elemento
     */
    RoadGraphDirection(int value) {
        this.value = value;
    }

    /**
     * @return il valore intero associato all'elemento dell'enumerazione
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value un valore intero (dovrebbe essere 1, 0 o -1)
     * @return l'istanza dell'enumerazione associata al valore specificato
     */
    public static RoadGraphDirection fromInt(int value) {
        switch (value) {
            case 1:
                return TAIL_TO_HEAD;
            case -1:
                return HEAD_TO_TAIL;
            default:
                return BOTH;
        }
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}