package sistematica.pbutils;

/**
 * Un semplice punto (x, y).
 */
public class SimplePoint {
    private double x;
    private double y;
    
    /**
     * Crea una nuova istanza di SimplePoint.
     * 
     * @param x le ascisse
     * @param y le ordinate
     */
    public SimplePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return le ascisse
     */
    public double getX() {
        return x;
    }

    /**
     * @param x le ascisse
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return le ordinate
     */
    public double getY() {
        return y;
    }

    /**
     * @param y le ordinate
     */
    public void setY(double y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
