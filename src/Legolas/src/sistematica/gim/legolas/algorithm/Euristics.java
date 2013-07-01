package sistematica.gim.legolas.algorithm;

import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.geolib.GeoUtils;
import sistematica.gim.legolas.entity.RoadGraphDirection;
import sistematica.gim.legolas.entity.Edge;
import sistematica.gim.legolas.entity.Sample;
import sistematica.pbutils.FormatLogger;
import sistematica.pbutils.SimplePoint;

/**
 * Contiene tutte le funzioni che calcolano valori basandosi su "rilevazioni
 * euristiche".
 */
public class Euristics {

    private static final FormatLogger logger = FormatLogger.getLogger(Euristics.class);

    private Euristics() {
        // Questa "classe" non s'ha da istanziare...
    }

    /**
     * Calcola la densità veicolare di un arco stradale a partire dalla quale si
     * può parlare di ingorgo.
     *
     * @param freeFlowSpeed la velocità a flusso libero (km/h)
     * @param lanes il numero di corsie
     * @return la jam density, ovvero la densità in una situazione di traffico
     * estremo (veicoli/km)
     */
    public static double getJamDensity(double freeFlowSpeed, double lanes) {
        /*
         * Il numero magico, 42, è stato scelto dopo aver fatto delle prove con
         * i dati provenienti dalle spire della Provincia. Sebbene piazzate su
         * strade di poco conto, hanno quantomeno dato un'idea dell'ordine di
         * grandezza dei valori di flusso e densità.
         *
         * Con una jam density di 42 veicoli/km/corsia abbiamo i seguenti picchi
         * di flusso, a seconda del limite di velocità della strada:
         *
         * 130 km/h -> 1357 veicoli/h/corsia (3 corsie -> 4071 veicoli/h)
         *
         * 110 km/h -> 1145 veicoli/h/corsia (2 corsie -> 2290 veicoli/h)
         *
         * 90 km/h -> 933 veicoli/h/corsia (2 corsie -> 1866 veicoli/h)
         *
         * 50 km/h -> 504 veicoli/h/corsia (2 corsie -> 1000 veicoli/h)
         *
         * 42 veicoli/km/corsia equivalgono ad uno spazio per veicolo pari a
         * 24m. Sicuramente è troppo per le macchine (~4m) ma è realistico per
         * autobus e similari (~15m).
         */

        return 42 * lanes;
    }

    /**
     * Cerca di dedurre il numero di corsie di una strada dalla sua velocità di
     * percorrenza.
     *
     * @param freeFlowSpeed la velocità a flusso libero (km/h)
     * @return il numero di corsie
     */
    public static double getLanes(double freeFlowSpeed) {
        if (freeFlowSpeed <= 30) {
            return 1;
        } else if (freeFlowSpeed < 130) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Calcola la direzione di percorrenza (vedi {@link Direction}) del veicolo
     * sull'arco dove è stato georeferenziato; se l'arco ha un solo senso di
     * percorrenza usa quello (ignoriamo chi va contromano), altrimenti la
     * calcola confrontando l'heading del veicolo e quello del segmento
     * dell'arco dove il veicolo si trova.
     *
     * @param graph il grafo stradale di riferimento
     * @param sample il campionamento FCD
     * @param edge l'arco del grafo stradale su cui si trova il veicolo
     * @return la direzione del veicolo sull'arco del grafo stradale dove si
     * trova
     */
    public static RoadGraphDirection calculateSampleDirection(RoadGraph graph, Sample sample, Edge edge) {
        if (edge.getDirection() == RoadGraphDirection.BOTH) {
            SimplePoint[] closestSegment = edge.getClosestSegment(new SimplePoint(sample.getLongitude(), sample.getLatitude()));

            if (closestSegment[0] == null || closestSegment[1] == null) {
                logger.error("Error while finding the closest segment (vehicleHeading = %f, idEdge = %d)", sample.getHeading(), edge.getId());
                return RoadGraphDirection.BOTH;
            } else {
                double edgeHeading = GeoUtils.getDirection(closestSegment[0].getY(), closestSegment[0].getX(), closestSegment[1].getY(), closestSegment[1].getX());
                if (Math.abs(sample.getHeading() - edgeHeading) < 180) {
                    return RoadGraphDirection.HEAD_TO_TAIL;
                } else {
                    return RoadGraphDirection.TAIL_TO_HEAD;
                }
            }
        } else {
            return edge.getDirection();
        }
    }

    /**
     * Calcola la densità veicolare di un arco stradale in modo assolutamente
     * casuale. La densità calcolata è comunque compresa tra 0 e la jam density.
     *
     * @param edge l'arco del grafo stradale
     * @return la densità veicolare (veicoli/km) sull'arco. Qualora non sia
     * disponibile la velocità a flusso libero dell'arco (ovvero
     * edge.getFreeFlowSpeed() è NaN) la densità verrà impostata a NaN.
     */
    public static double calculateDensityRandom(Edge edge) {
        double freeFlowSpeed = edge.getFreeFlowSpeed();

        if (!Double.isNaN(freeFlowSpeed)) {
            double jamDensity = Euristics.getJamDensity(edge.getFreeFlowSpeed(), edge.getLanes());
            return Math.random() * jamDensity;
        } else {
            return Double.NaN;
        }
    }

    /**
     * Calcola la densità veicolare dell'arco stradale a partire dalla velocità
     * media usando il modello lineare di Greenshield.
     *
     * @param edge l'arco del grafo stradale
     * @param avgSpeed la velocità media sull'arco (km/h)
     * @return la densità veicolare (veicoli/km) sull'arco. Qualora non sia
     * disponibile la velocità a flusso libero dell'arco (ovvero
     * edge.getFreeFlowSpeed() è NaN) la densità verrà impostata a NaN.
     */
    public static double calculateDensityGreenshield(Edge edge, double avgSpeed) {
        double freeFlowSpeed = edge.getFreeFlowSpeed();
        double density = Double.NaN;

        if (!Double.isNaN(freeFlowSpeed)) {
            double jamDensity = Euristics.getJamDensity(edge.getFreeFlowSpeed(), edge.getLanes());

            avgSpeed = Math.min(avgSpeed, freeFlowSpeed - 1); // La velocità media deve essere minore della velocità a flusso libero
            avgSpeed = Math.max(avgSpeed, 0); // La velocità media deve essere maggiore o uguale a zero

            density = jamDensity * (freeFlowSpeed - avgSpeed) / freeFlowSpeed;
        }

        return density;
    }
}
