package sistematica.gim.legolas.entity;

/**
 * Contiene le statistiche di un arco stradale prefissato.
 */
public class TrafficStat implements Comparable<TrafficStat> {

    private long idEdge;
    private RoadGraphDirection edgeDirection;
    private long samplesCount;
    private long vehiclesCount;
    private double avgSpeed;
    private double stdDevSpeed;
    private double density;
    private double flow;

    /**
     * Crea una nuova istanza di TrafficStat.
     *
     * @param idEdge l'arco del grafo stradale
     * @param edgeDirection l'orientamento dell'arco
     * @param samplesCount il numero di campionamenti FCD su cui è stata
     * calcolata la velocità media
     * @param vehiclesCount il numero di veicoli su cui è stata calcolata la
     * velocità media
     * @param avgSpeed la velocità media (km/h)
     * @param stdDevSpeed la deviazione standard delal velocità media
     * @param density la densità dei veicoli (veicoli/km)
     * @param flow il flusso veicolare (veicoli/h)
     */
    public TrafficStat(long idEdge, RoadGraphDirection edgeDirection, long samplesCount, long vehiclesCount, double avgSpeed, double stdDevSpeed, double density, double flow) {
        this.idEdge = idEdge;
        this.edgeDirection = edgeDirection;
        this.samplesCount = samplesCount;
        this.vehiclesCount = vehiclesCount;
        this.avgSpeed = avgSpeed;
        this.stdDevSpeed = stdDevSpeed;
        this.density = density;
        this.flow = flow;
    }

    /**
     * @return l'arco del grafo stradale
     */
    public long getIdEdge() {
        return idEdge;
    }

    /**
     * @param idEdge l'arco del grafo stradale
     */
    public void setIdEdge(long idEdge) {
        this.idEdge = idEdge;
    }

    /**
     * @return l'orientamento dell'arco (1, 0 o -1)
     */
    public RoadGraphDirection getEdgeDirection() {
        return edgeDirection;
    }

    /**
     * @param direction l'orientamento dell'arco (1, 0 o -1)
     */
    public void setEdgeDirection(RoadGraphDirection direction) {
        this.edgeDirection = direction;
    }

    /**
     * @return il numero di campionamenti FCD su cui sono state calcolate le
     * statistiche
     */
    public long getSamplesCount() {
        return samplesCount;
    }

    /**
     * @param samplesCount il numero di campionamenti FCD su cui sono state
     * calcolate le statistiche
     */
    public void setSamplesCount(long samplesCount) {
        this.samplesCount = samplesCount;
    }

    /**
     * @return il numero di veicoli su cui sono state calcolate le statistiche
     */
    public long getVehiclesCount() {
        return vehiclesCount;
    }

    /**
     * @param vehiclesCount il numero di veicoli su cui sono state calcolate le
     * statistiche
     */
    public void setVehiclesCount(long vehiclesCount) {
        this.vehiclesCount = vehiclesCount;
    }

    /**
     * @return la velocità media (km/h)
     */
    public double getAvgSpeed() {
        return avgSpeed;
    }

    /**
     * @param avgSpeed la velocità media (km/h)
     */
    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    /**
     * @return la deviazione standard della velocità media
     */
    public double getStdDevSpeed() {
        return stdDevSpeed;
    }

    /**
     * @param stdDevSpeed la deviazione standard della velocità media
     */
    public void setStdDevSpeed(double stdDevSpeed) {
        this.stdDevSpeed = stdDevSpeed;
    }

    /**
     * @return la densità (veicoli/km)
     */
    public double getDensity() {
        return density;
    }

    /**
     * @param density la densità (veicoli/km)
     */
    public void setDensity(double density) {
        this.density = density;
    }

    /**
     * @return il flusso veicolare (veicoli/h)
     */
    public double getFlow() {
        return flow;
    }

    /**
     * @param flow il flusso veicolare (veicoli/h)
     */
    public void setFlow(double flow) {
        this.flow = flow;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.idEdge ^ (this.idEdge >>> 32));
        hash = 71 * hash + (this.edgeDirection != null ? this.edgeDirection.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TrafficStat) {
            TrafficStat stat = (TrafficStat) o;
            return stat.idEdge == idEdge && stat.edgeDirection == edgeDirection;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(TrafficStat stat) {
        if (idEdge == stat.idEdge) {
            return edgeDirection.compareTo(stat.edgeDirection);
        } else {
            return ((Long) idEdge).compareTo(stat.idEdge);
        }
    }
}
