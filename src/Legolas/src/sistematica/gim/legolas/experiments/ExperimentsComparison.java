package sistematica.gim.legolas.experiments;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.TrafficStat;
import sistematica.pbutils.FormatLogger;

/**
 * Rappresenta il confronto di due esperimenti
 */
public class ExperimentsComparison {

    private static final FormatLogger logger = FormatLogger.getLogger(ExperimentsComparison.class);
    private ExperimentsDAO dao;
    private Experiment a;
    private Experiment b;
    private String description;
    private long edgesA;
    private long edgesB;
    private long sharedEdges;
    private double speedDiffAvg;
    private double speedDiffStdDev;
    private double densityDiffAvg;
    private double densityDiffStdDev;
    private double flowDiffAvg;
    private double flowDiffStdDev;

    /**
     * Crea una nuova istanza di ExperimentsComparison.
     * 
     * @param configuration la configurazione del programma
     * @param connection la connessione al DB
     * @param idA l'ID sul DB del primo esperimento
     * @param idB l'ID sul DB del secondo esperimento
     * @throws SQLException se c'è un errore nell'interazione col DB
     */
    public ExperimentsComparison(Configuration configuration, Connection connection, long idA, long idB) throws SQLException {
        dao = new ExperimentsDAO(configuration, connection);
        a = dao.getExperiment(idA);
        b = dao.getExperiment(idB);
        compare();
    }

    /**
     * Calcola tutti i parametri di confronto tra i due esperimenti.
     * 
     * @throws SQLException se c'è un errore nelle query
     */
    private void compare() throws SQLException {
        description = "Whole graph";

        List<TrafficStat> statsListA = dao.getExperimentTrafficStats(a.getId());
        edgesA = statsListA.size();

        List<TrafficStat> statsListB = dao.getExperimentTrafficStats(b.getId());
        edgesB = statsListB.size();

        // Il TreeSet è ordinato in base alla tupla (arco, direzione)
        TreeSet<TrafficStat> statsSetA = new TreeSet<TrafficStat>(statsListA);
        TreeSet<TrafficStat> statsSetB = new TreeSet<TrafficStat>(statsListB);
        statsSetA.retainAll(statsSetB);
        statsSetB.retainAll(statsSetA);

        // Gli archi condivisi sono la cardinalità dell'intersezione
        sharedEdges = statsSetA.size();;

        // La media delle differenze dei parametri velocità, densità e flusso
        speedDiffAvg = 0;
        densityDiffAvg = 0;
        flowDiffAvg = 0;
        Iterator<TrafficStat> itA = statsSetA.iterator();
        Iterator<TrafficStat> itB = statsSetB.iterator();
        TrafficStat nextA;
        TrafficStat nextB;

        while (itA.hasNext() && itB.hasNext()) {
            nextA = itA.next();
            nextB = itB.next();

            speedDiffAvg += nextA.getAvgSpeed() - nextB.getAvgSpeed();
            densityDiffAvg += nextA.getDensity() - nextB.getDensity();
            flowDiffAvg += nextA.getFlow() - nextB.getFlow();
        }
        speedDiffAvg /= sharedEdges;
        densityDiffAvg /= sharedEdges;
        flowDiffAvg /= sharedEdges;

        // La deviazione standard delle differenze dei parametri velocità, densità e flusso
        speedDiffStdDev = 0;
        densityDiffStdDev = 0;
        flowDiffStdDev = 0;
        itA = statsSetA.iterator();
        itB = statsSetB.iterator();
        while (itA.hasNext() && itB.hasNext()) {
            nextA = itA.next();
            nextB = itB.next();

            speedDiffStdDev += Math.pow(speedDiffAvg - (nextA.getAvgSpeed() - nextB.getAvgSpeed()), 2);
            densityDiffStdDev += Math.pow(densityDiffAvg - (nextA.getDensity() - nextB.getDensity()), 2);
            flowDiffStdDev += Math.pow(flowDiffAvg - (nextA.getFlow() - nextB.getFlow()), 2);
        }
        speedDiffStdDev = Math.sqrt(speedDiffStdDev);
        densityDiffStdDev = Math.sqrt(densityDiffStdDev);
        flowDiffStdDev = Math.sqrt(flowDiffStdDev);
    }

    /**
     * @return la descrizione di questa comparazione
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return il numero di archi del primo esperimento
     */
    public long getEdgesA() {
        return edgesA;
    }

    /**
     * @return il numero di archi del secondo esperimento
     */
    public long getEdgesB() {
        return edgesB;
    }

    /**
     * @return il numero di archi in comune tra i due esperimenti
     */
    public long getSharedEdges() {
        return sharedEdges;
    }

    /**
     * @return la differenza media delle velocità
     */
    public double getSpeedDiffAvg() {
        return speedDiffAvg;
    }

    /**
     * @return la deviazione standard della differenza delle velocità
     */
    public double getSpeedDiffStdDev() {
        return speedDiffStdDev;
    }

    /**
     * @return la differenza media delle densità
     */
    public double getDensityDiffAvg() {
        return densityDiffAvg;
    }

    /**
     * @return la deviazione standard della differenza delle densità
     */
    public double getDensityDiffStdDev() {
        return densityDiffStdDev;
    }

    /**
     * @return la differenza media del flusso
     */
    public double getFlowDiffAvg() {
        return flowDiffAvg;
    }

    /**
     * @return la deviazione standard della differenza del flusso
     */
    public double getFlowDiffStdDev() {
        return flowDiffStdDev;
    }
}
