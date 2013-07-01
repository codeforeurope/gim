/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sistematica.sinottici;

/**
 *
 * @author Manuel
 */
public class Street {

    //Identificativo Strada
    private int id;
    //Descrizone Strada
    private String name;
    //Tipologia Strada
    //Indica il tipo di strada
    // C => consolare   A => autostrade
    private String typeStreet;
    private double latUp;
    private double lonUp;
    private double latDown;
    private double lonDown;

    public Street(int id, String name, String typeStreet, double latUp, double lonUp, double latDown, double lonDown) {
        this.id = id;
        this.name = name;
        this.typeStreet = typeStreet;
        this.latUp = latUp;
        this.lonUp = lonUp;
        this.latDown = latDown;
        this.lonDown = lonDown;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTypeStreet() {
        return typeStreet;
    }

    public double getLatUp() {
        return latUp;
    }

    public double getLonUp() {
        return lonUp;
    }

    public double getLatDown() {
        return latDown;
    }

    public double getLonDown() {
        return lonDown;
    }
}
