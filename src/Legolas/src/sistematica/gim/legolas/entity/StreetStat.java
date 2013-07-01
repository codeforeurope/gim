package sistematica.gim.legolas.entity;

/**
 * Rappresenta una statistica su un arco di una strada censita.
 */
public class StreetStat implements Comparable<StreetStat> {

    private StreetDetail streetDetail;
    private double percSpeed;

    public StreetStat(StreetDetail streetDetail, double percSpeed) {
        this.streetDetail = streetDetail;
        this.percSpeed = percSpeed;
    }

    /**
     * @return l'arco a cui si riferisce la statistica
     */
    public StreetDetail getStreetDetail() {
        return streetDetail;
    }

    /**
     * @return la percentuale della velocità media rispetto alla velocità limite
     * della strada (da 0 a 100)
     */
    public double getPercSpeed() {
        return percSpeed;
    }

    @Override
    public int compareTo(StreetStat that) {
        return this.streetDetail.compareTo(that.streetDetail);
    }
    
    @Override
    public String toString() {
        return String.format("%s, percSpeed: %f", streetDetail, percSpeed);
    }
}
