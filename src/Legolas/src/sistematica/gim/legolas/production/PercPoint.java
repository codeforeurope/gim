package sistematica.gim.legolas.production;

import sistematica.gim.legolas.entity.StreetStat;

public class PercPoint extends PercShape {

    private static final String TEMPLATE = "<circle style=\"stroke: $COLOR$; fill: $COLOR$\" cx=\"%s\" cy=\"%s\" r=\"1\"/>";
    private StreetStat stat;

    public PercPoint(double kx, double ky, double marginX, double marginY, long minId, StreetStat stat) {
        super(kx, ky, marginX, marginY, minId);
        this.stat = stat;
    }

    @Override
    public String toSVG() {
        long id = stat.getStreetDetail().getId();
        double perc = stat.getPercSpeed();

        return String.format(TEMPLATE, idToX(id), percToY(perc));
    }
}
