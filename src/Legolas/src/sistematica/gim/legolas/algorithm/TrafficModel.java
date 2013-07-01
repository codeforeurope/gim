package sistematica.gim.legolas.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.Edge;
import sistematica.gim.legolas.entity.Sample;

/**
 * Implementa il calcolo dei parametri per la descrizione a livello macroscopico
 * del traffico veicolare, ovvero velocità media (km/h), densità veicolare
 * (veicoli/km) e flusso (veicoli/h).
 * 
 * <p>Poiché la velocità media e calcolata a partire dalle velocità dei singoli
 * veicoli, oltre alla media sono disponibili anche il numero di campioni e la
 * deviazione standard.
 * 
 * <p>La densità veicolare viene calcolata in relazione alla velocità media.
 * Attualmente sono supportati i seguenti modelli, a seconda del valore di
 * {@link Configuration#TRAFFIC_MODEL} della configurazione passatagli per
 * argomento:
 * 
 * <ul>
 * <li><b>Greenshield:</b> il modello lineare di Greenshield con le nostre euristiche sulla jam density.</li>
 * </ul>
 * 
 * <p>Qualora la configurazione non specifichi il modello da usare verrò utilizzato
 * <b>Greenshield</b>.
 */
public class TrafficModel {

    private DensityModel densityModel;
    private Edge edge;
    private boolean calculationDone;
    private List<Sample> samples = new LinkedList<Sample>();
    private Set<String> idVehicles = new HashSet<String>();
    private double weightedAvgSpeed;
    private double stdDevSpeed;
    private double density;
    private double flow;

    /**
     * Crea una nuova istanza di TrafficModel.
     * 
     * @param configuration la configurazione del programma
     * @param edge l'arco del grafo stradale su cui verranno effettuati i calcoli
     */
    public TrafficModel(DensityModel densityModel, Edge edge) {
        this.densityModel = densityModel;
        this.edge = edge;

        calculationDone = false;

        weightedAvgSpeed = Double.NaN;
        stdDevSpeed = Double.NaN;
        density = Double.NaN;
        flow = Double.NaN;
    }

    /**
     * Aggiunge una rilevazione di velocità istantanea a quella da utilizzare
     * per il calcolo dei parametri macroscopici del traffico.
     * 
     * @param sample velocità istantanea da aggiungere a quelle usate per
     * calcolare i parametri macroscopici del traffico.
     */
    public void add(Sample sample) {
        calculationDone = false;
        samples.add(sample);
        idVehicles.add(sample.getIdVehicle());
    }

    /**
     * Calcola velocità media, densita e flusso usando le velocità istantanee
     * fin qui inserite.
     */
    private void calculateEverything() {
        if (!calculationDone) {
            long minTime = Long.MAX_VALUE;
            for (Sample sample : samples) {
                long time = sample.getMinuteTime();
                if (time < minTime) {
                    minTime = time;
                }
            }
            minTime--; // Altrimenti non considero il campione che ha minTime...
            
            double speedSum = 0;
            double speedWeightSum = 0;
            double weightSum = 0;
            for (Sample sample : samples) {
                double speed = sample.getSpeed();
                speedSum += speed;
                
                long weight = sample.getMinuteTime() - minTime;
                speedWeightSum += speed * weight;
                weightSum += weight;
            }
            
            // La media pesata da più importanza ai campioni più recenti...
            weightedAvgSpeed = speedWeightSum / weightSum;

            // ... tuttavia la deviazione standard è calcolata sulla media aritmetica
            double arithmeticAvgSpeed = speedSum / samples.size();
            stdDevSpeed = 0;
            for (Sample sample : samples) {
                stdDevSpeed += (arithmeticAvgSpeed -  sample.getSpeed()) * (arithmeticAvgSpeed -  sample.getSpeed());
            }
            stdDevSpeed = Math.sqrt(stdDevSpeed);

            switch (densityModel) {
                case GREENSHIELD:
                    density = Euristics.calculateDensityGreenshield(edge, weightedAvgSpeed);
                    break;
                case RANDOM:
                    density = Euristics.calculateDensityRandom(edge);
                    break;
                default: // Modello NONE
                    density = Double.NaN;
            }

            flow = Double.isNaN(density) ? Double.NaN : weightedAvgSpeed * density;

            calculationDone = true;
        }
    }

    /**
     * @return la velocità media (km/h)
     */
    public double getAvgSpeed() {
        calculateEverything();
        return weightedAvgSpeed;
    }

    /**
     * @return la deviazione standard della velocità
     */
    public double getStdDevSpeed() {
        calculateEverything();
        return stdDevSpeed;
    }

    /**
     * @return la densità dei veicoli (veicoli/km)
     */
    public double getDensity() {
        calculateEverything();
        return density;
    }

    /**
     * @return il flusso veicolare (veicoli/h)
     */
    public double getFlow() {
        calculateEverything();
        return flow;
    }

    /**
     * @return il numero di veicoli
     */
    public long getVehiclesCount() {
        return idVehicles.size();
    }

    /**
     * @return il numero di campionamenti FCD
     */
    public long getSamplesCount() {
        return samples.size();
    }
}
