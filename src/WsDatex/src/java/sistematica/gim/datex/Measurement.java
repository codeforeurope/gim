package sistematica.gim.datex;

public class Measurement {
    private long link;
    private String instant;
    private double speed;
    private double flow;

    public long getLink() {
        return link;
    }

    public void setLink(long link) {
        this.link = link;
    }

    public String getInstant() {
        return instant;
    }

    public void setInstant(String instant) {
        this.instant = instant;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }
}
