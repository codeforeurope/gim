package sistematica.gim.infomobility.utils;

import sistematica.geolib.GeoUtils;
import sistematica.gim.infomobility.servlet.InfoMobRawData;

/**
 * Classe usata per calcolare l'heading di un veicolo.
 */
public class HeadingCalculator {

    private class Heading {

        public double latitude;
        public double longitude;
        public double heading = -1;

        public Heading(InfoMobRawData sample) {
            latitude = sample.getDoubleLatitude();
            longitude = sample.getDoubleLongitude();
        }

        public Heading(InfoMobRawData sample, double heading) {
            this(sample);
            this.heading = heading;
        }
    }
    // Velocita' sotto la quale il calcolo dell'heading non e' significativo
    private static final double MIN_SPEED = 10;
    private Heading heading = null;

    /**
     * Aggiunge un campionamento alla lista di quelli necessari per calcolare
     * l'heading di un veicolo.
     * 
     * E' necessario aggiungere tutti i campionamenti di UN SINGOLO VEICOLO, in
     * ordine cronologico crescente, se si vuole che il calcolo dell'heading sia
     * affidabile.
     * 
     * @param sample un campionamento FCD
     */
    public void addSample(InfoMobRawData sample) {
        if (heading == null) {
            heading = new Heading(sample);
        } else if (sample.getDoubleSpeed() > MIN_SPEED) {
            int angleInDegrees = (int) Math.round(GeoUtils.getDirection(heading.latitude, heading.longitude, sample.getDoubleLatitude(), sample.getDoubleLongitude()));
            heading = new Heading(sample, angleInDegrees);
        }
    }

    /**
     * Restituisce l'heading, in gradi, di un veicolo calcolato sulla base delle
     * sue posizioni successive.
     * 
     * @return l'heading, in gradi, del veicolo corrente o Double.NaN se non e'
     *         stato possibile calcolarlo
     */
    public double getHeading() {
        if (heading == null) {
            return -1;
        } else {
            return heading.heading;
        }
    }
}