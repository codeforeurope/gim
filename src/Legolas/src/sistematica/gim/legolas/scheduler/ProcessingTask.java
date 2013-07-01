package sistematica.gim.legolas.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import sistematica.gim.legolas.algorithm.TrafficModel;
import sistematica.gim.legolas.entity.Edge;
import sistematica.gim.legolas.entity.Sample;
import sistematica.gim.legolas.entity.TrafficStat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Calcola le statistiche (velocità media, densità e flusso) sugli archi del
 * grafo stradale a partire dalle velocità istantanee dei veicoli.
 */
public class ProcessingTask extends Task {

    private Map<Edge, TrafficModel> stats = new HashMap<Edge, TrafficModel>();
    private Iterator<Entry<Edge, TrafficModel>> statsIterator;

    /**
     * Crea una nuova istanza di ProcessingTask.
     * 
     * @param descriptor il descrittore del job di cui fa parte il task
     * @param connection la connessione al DB
     * @throws SQLException se c'è un errore nelle query o nella connessione al DB
     */
    public ProcessingTask(JobDescriptor descriptor, Connection connection) throws SQLException {
        super(descriptor, connection);
    }

    /**
     * Aggiunge una velocità istantanea a quelle da considerare per il calcolo.
     * 
     * @param sample una rilevazione della velocità istantanea
     */
    public void addSample(Sample sample) {
        Edge edge = sample.getEdge();

        if (!stats.containsKey(edge)) {
            stats.put(edge, new TrafficModel(descriptor.getDensityModel(), edge));
        }

        stats.get(edge).add(sample);
    }

    /**
     * Restituisce i risultati dell'elaborazione statistica, uno per volta,
     * chiamata dopo chiamata. Va chiamato dopo aver fatto <b>tutte</b> le
     * {@link ProcessingTask#addSample(sistematica.gim.legolas.entity.Sample) }
     * del caso.
     * 
     * @return le statistiche elaborate su un arco del grafo stradale
     */
    public TrafficStat getNextStat() {
        if (statsIterator == null) {
            statsIterator = stats.entrySet().iterator();
        }

        if (statsIterator.hasNext()) {
            Entry<Edge, TrafficModel> entry = statsIterator.next();
            Edge edge = entry.getKey();
            TrafficModel model = entry.getValue();
            long samples = model.getSamplesCount();
            long vehicles = model.getVehiclesCount();
            double avgSpeed = model.getAvgSpeed();
            double stdDevSpeed = model.getStdDevSpeed();
            double density = model.getDensity();
            double flow = model.getFlow();
            return new TrafficStat(edge.getId(), edge.getDirection(), samples, vehicles, avgSpeed, stdDevSpeed, density, flow);
        } else {
            return null;
        }
    }
}
