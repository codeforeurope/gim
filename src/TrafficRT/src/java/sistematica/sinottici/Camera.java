/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sistematica.sinottici;

/**
 *
 * @author Manuel
 */
public class Camera {

    //Identificativo della telecamera
    private int id;
    //Link
    private String link;
    //Nome della telecamera
    private String name;
    //Descrizione della telecamera
    private String description;
    //Latitudine
    private double latitude;
    //Longitudine
    private double longitude;

    public Camera (int id,String link,String name,String description,double latitude,double longitude) {
        this.id = id;
        this.link = link;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
