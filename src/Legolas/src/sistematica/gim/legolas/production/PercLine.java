package sistematica.gim.legolas.production;

import sistematica.gim.legolas.entity.StreetStat;

public class PercLine extends PercShape {

    private static final String TEMPLATE = "<line style=\"stroke: $COLOR$; stroke-width:2\" x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\"/>";
    private StreetStat stat0;
    private StreetStat stat1;
    
    public PercLine(double kx, double ky, double marginX, double marginY, long minId, StreetStat stat0, StreetStat stat1) {
        super(kx, ky, marginX, marginY, minId);
        this.stat0 = stat0;
        this.stat1 = stat1;
    }

    @Override
    public String toSVG() {
        long id0 = stat0.getStreetDetail().getId();
        double perc0 = stat0.getPercSpeed();
        long id1 = stat1.getStreetDetail().getId();
        double perc1 = stat1.getPercSpeed();
        return String.format(TEMPLATE, idToX(id0), percToY(perc0), idToX(id1), percToY(perc1));
    }
}
