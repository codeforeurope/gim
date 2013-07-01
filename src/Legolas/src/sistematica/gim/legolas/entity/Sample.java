package sistematica.gim.legolas.entity;

import java.sql.Timestamp;

/**
 * Rappresenta la velocità istantanea di un dato veicolo in un dato momento ed
 * in un preciso arco del grafo stradale.
 */
public class Sample {

    private static final int MILLISECONDS_IN_A_MINUTE = 60000;
    private Edge edge;
    private String idVehicle;
    private double speed;
    private double latitude;
    private double longitude;
    private double heading;
    private Timestamp timestamp;

    /**
     * @return l'arco del grafo stradale su cui è stata fatta la rilevazione
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * @param edge l'arco del grafo stradale su cui è stata fatta la rilevazione
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    /**
     * @return l'identificativo del veicolo su DB
     */
    public String getIdVehicle() {
        return idVehicle;
    }

    /**
     * @param idVehicle l'identificativo del veicolo su DB
     */
    public void setIdVehicle(String idVehicle) {
        this.idVehicle = idVehicle;
    }

    /**
     * @return la velocità del veicolo
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param speed la velocità del veicolo
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * @return l'istante temporale della rilevazione
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp l'istante temporale della rilevazione
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return la latitudine
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude la latitudine
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return la longitudine
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude la longitudine
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return la direzione (in gradi)
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @param heading la direzione (in gradi)
     */
    public void setHeading(double heading) {
        this.heading = heading;
    }
    
    public long getMinuteTime() {
        return timestamp.getTime() / MILLISECONDS_IN_A_MINUTE;
    }
}
