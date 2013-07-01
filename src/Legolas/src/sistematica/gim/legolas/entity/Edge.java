package sistematica.gim.legolas.entity;

import java.util.List;
import sistematica.geolib.GeoUtils;
import sistematica.pbutils.SimplePoint;

/**
 * Rappresenta un arco orientato del grafo stradale. Questa classe è
 * utilizzabile come chiave per le {@link java.util.Map}.
 */
public class Edge {

    private long id;
    private List<SimplePoint> geometry;
    private RoadGraphDirection direction;
    private String name;
    private double freeFlowSpeed;
    private double lanes;

    /**
     * Crea una nuova istanza di Edge.
     *
     * @param id l'ID dell'arco sul DB
     * @param direction il senso di percorrenza dell'arco
     * @param freeFlowSpeed la velocità a flusso libero sull'arco (km/h)
     * @param lanes il numero di corsie
     */
    public Edge(long id, List<SimplePoint> geometry, RoadGraphDirection direction, String name, double freeFlowSpeed, double lanes) {
        this.id = id;
        this.geometry = geometry;
        this.direction = direction;
        this.name = name;
        this.freeFlowSpeed = freeFlowSpeed;
        this.lanes = lanes;
    }

    /**
     * Crea una nuova istanza di Edge a partire da un altro Edge.
     *
     * @param that l'altro Edge
     */
    public Edge(Edge that) {
        this.id = that.id;
        this.geometry = that.geometry;
        this.direction = that.direction;
        this.name = that.name;
        this.freeFlowSpeed = that.freeFlowSpeed;
        this.lanes = that.lanes;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) (this.getId() ^ (this.getId() >>> 32));
        hash = 17 * hash + this.getDirection().getValue();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return o != null
                && o instanceof Edge
                && ((Edge) o).getId() == getId()
                && ((Edge) o).getDirection() == getDirection();
    }

    /**
     * @return l'identificativo numerico dell'arco sul grafo stradale
     * considerato
     */
    public long getId() {
        return id;
    }

    /**
     * @param idEdge l'identificativo numerico dell'arco sul grafo stradale
     * considerato
     */
    public void setId(long idEdge) {
        this.id = idEdge;
    }

    /**
     * @return la geometria della strada sotto forma di lista di punti (x, y),
     * ovvero (lon, lat)
     */
    public List<SimplePoint> getGeometry() {
        return geometry;
    }

    /**
     * @param geometry la geometria della strada sotto forma di lista di punti
     * (x, y), ovvero (lon, lat)
     */
    public void setGeometry(List<SimplePoint> geometry) {
        this.geometry = geometry;
    }

    /**
     * @return la direzione di percorrenza dell'arco
     */
    public RoadGraphDirection getDirection() {
        return direction;
    }

    /**
     * @param direction la direzione di percorrenza dell'arco
     */
    public void setDirection(RoadGraphDirection direction) {
        this.direction = direction;
    }

    /**
     * @return la velocità a flusso libero sull'arco (km/h)
     */
    public double getFreeFlowSpeed() {
        return freeFlowSpeed;
    }

    /**
     * @param freeFlowSpeed la velocità a flusso libero sull'arco (km/h)
     */
    public void setFreeFlowSpeed(double freeFlowSpeed) {
        this.freeFlowSpeed = freeFlowSpeed;
    }

    /**
     * @return il numero di corsie
     */
    public double getLanes() {
        return lanes;
    }

    /**
     * @param lanes il numero di corsie
     */
    public void setLanes(double lanes) {
        this.lanes = lanes;
    }

    /**
     * @return il nome dell'arco
     */
    public String getName() {
        return name;
    }

    /**
     * @param name il nome dell'arco
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce il segmento dell'arco più vicino al punto specificato.
     *
     * @param p il punto di riferimento
     * @return il segmento dell'arco più vicino al punto specificato
     */
    public SimplePoint[] getClosestSegment(SimplePoint p) {
        double minDistance = Double.POSITIVE_INFINITY;
        SimplePoint minP0 = null, minP1 = null;
        for (int i = 1, size = geometry.size(); i < size; i++) { // Direction.HEAD_TO_TAIL
            SimplePoint p0 = geometry.get(i - 1);
            SimplePoint p1 = geometry.get(i);
            double currDistance = GeoUtils.getDistance(p0.getY(), p0.getX(), p1.getY(), p1.getX()); // Y = Lat, X = Lon
            if (currDistance < minDistance) {
                minP0 = p0;
                minP1 = p1;
                minDistance = currDistance;
            }
        }

        return new SimplePoint[]{minP0, minP1};
    }

    @Override
    public String toString() {
        return String.format("%d %s", id, direction);
    }
}