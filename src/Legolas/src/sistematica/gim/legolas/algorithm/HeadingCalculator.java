package sistematica.gim.legolas.algorithm;

import java.util.HashMap;
import java.util.Map;
import sistematica.geolib.GeoUtils;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.Sample;
import sistematica.gim.legolas.scheduler.InputTask;

/**
 * Classe usata da {@link InputTask} per calcolare l'heading dei veicoli che
 * giacciono su archi percorribili in entrambi i sensi e che non hanno un
 * heading affidabile.
 */
public class HeadingCalculator {

    private class Heading {

        public double latitude;
        public double longitude;
        public double speed;
        public double heading = Double.NaN;
        public long time;

        public Heading(Sample sample) {
            latitude = sample.getLatitude();
            longitude = sample.getLongitude();
            speed = sample.getSpeed();
            time = sample.getTimestamp().getTime();
        }

        public Heading(Sample sample, double heading) {
            this(sample);
            this.heading = heading;
        }
    }
    private Configuration configuration;
    private Map<String, Heading> vehicleHeadings = new HashMap<String, Heading>();

    /**
     * Crea una nuova istanza di {@link HeadingCalculator}.
     * 
     * @param configuration la configurazione del programma
     */
    public HeadingCalculator(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Aggiunge un campionamento alla lista di quelli necessari per calcolare 
     * l'heading dei veicoli.
     * 
     * È necessario aggiungere tutti i campionamenti di tutti i veicoli, in 
     * ordine cronologico crescente, se si vuole che il calcolo dell'heading
     * sia affidabile.
     * 
     * @param sample un campionamento FCD
     */
    public void addSample(Sample sample) {
        String idVehicle = sample.getIdVehicle();
        if (!vehicleHeadings.containsKey(idVehicle)) {
            vehicleHeadings.put(idVehicle, new Heading(sample));
        } else {
            Heading lastHeading = vehicleHeadings.get(idVehicle);

            // Scarto posizioni troppo distanti temporalmente
            if (Math.abs(sample.getTimestamp().getTime() - lastHeading.time) > configuration.MAX_CONSECUTIVE_FCD_SECONDS_FOR_HEADING * 1000) {
                return;
            }

            // Due posizioni consecutive a velocità nulla non forniscono un heading affidabile
            if (sample.getSpeed() < configuration.MIN_FCD_SPEED_BEFORE_STOP && lastHeading.speed < configuration.MIN_FCD_SPEED_BEFORE_STOP) {
                return;
            }

            int angleInDegrees = (int) Math.round(GeoUtils.getDirection(lastHeading.latitude, lastHeading.longitude, sample.getLatitude(), sample.getLongitude()));

            vehicleHeadings.put(idVehicle, new Heading(sample, angleInDegrees));
        }
    }

    /**
     * Restituisce l'heading, in gradi, di un veicolo calcolato sulla base delle 
     * sue posizioni successive.
     * 
     * @param sample il campionamento FCD di cui si vuole calcolare l'heading
     * @return l'heading, in gradi, del veicolo a cui si riferisce il 
     *         campionamento o Double.NaN se non è stato possibile calcolarlo
     */
    public double getHeading(Sample sample) {
        String idVehicle = sample.getIdVehicle();
        if (!vehicleHeadings.containsKey(idVehicle)) {
            return Double.NaN;
        } else {
            return vehicleHeadings.get(idVehicle).heading;
        }
    }
}