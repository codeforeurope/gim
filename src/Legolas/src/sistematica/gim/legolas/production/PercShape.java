package sistematica.gim.legolas.production;

public abstract class PercShape {
    private double kx;
    private double ky;
    private double marginX;
    private double marginY;
    private long minId;
    
    public PercShape(double kx, double ky, double marginX, double marginY, long minId) {
        this.kx = kx;
        this.ky = ky;
        this.marginX = marginX;
        this.marginY = marginY;
        this.minId = minId;
    }
    
    protected double idToX(long id) {
        return (id - minId) * kx + marginX;
    }
    
    protected double percToY(double perc) {
        return (100 - perc) * ky + marginY;
    }
    
    public abstract String toSVG();
}
