package sistematica.gim.legolas.metrics;

import java.lang.management.ManagementFactory;
import sistematica.pbutils.DateTime;

/**
 * Contiene una serie di metriche che descrivono il comportamento del programma.
 * È una classe thread safe.
 */
public class RuntimeMetrics {

    private long realTimeJobs = 0;
    private long historicJobs = 0;
    private long experimentalJobs = 0;
    private long jobErrors = 0;
    private long samples = 0;
    private long samplesOnBidirectionalEdges = 0;
    private long edgesWithStats = 0;
    private long bidirectionalEdges = 0;

    /**
     * @return l'uptime del programma in millisecondi
     */
    public long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    /**
     * Aggiunge un job real time a quelli già eseguiti e in esecuzione.
     */
    public synchronized void addRealTimeJob() {
        realTimeJobs++;
    }

    /**
     * @return il numero di job real time eseguiti e in esecuzione
     */
    public synchronized long getRealTimeJobs() {
        return realTimeJobs;
    }

    /**
     * Aggiunge un job storico a quelli già eseguiti e in esecuzione.
     */
    public synchronized void addHistoricJob() {
        historicJobs++;
    }

    /**
     * @return il numero di job storici eseguiti e in esecuzione
     */
    public synchronized long getHistoricJobs() {
        return historicJobs;
    }

    /**
     * Aggiunge un job sperimentali a quelli già eseguiti e in esecuzione.
     */
    public synchronized void addExperimentalJob() {
        experimentalJobs++;
    }

    /**
     * @return il numero di job sperimentali eseguiti e in esecuzione
     */
    public synchronized long getExperimentalJobs() {
        return experimentalJobs;
    }

    /**
     * Aggiunge un job terminato con un'eccezione.
     */
    public synchronized void addJobError() {
        jobErrors++;
    }

    /**
     * @return il numero di job terminati con un'eccezione
     */
    public synchronized long getJobErrors() {
        return jobErrors;
    }

    /**
     * Aggiunge un campione alla lista di quelli letti fino a questo momento.
     */
    public synchronized void addSample() {
        samples++;
    }

    /**
     * Restituisce il numero di campioni (le rilevazioni delle velocità
     * istantanee) letti fino a questo momento.
     * 
     * @return il numero di campioni letti
     */
    public synchronized long getSamples() {
        return samples;
    }

    /**
     * Aggiunge un campione a quelli posizionati su archi percorribili in
     * entrambi i sensi di marcia.
     */
    public synchronized void addSampleOnBidirectionalEdge() {
        samplesOnBidirectionalEdges++;
    }

    /**
     * Restituisce il numero di campioni posizionati su archi del grafo
     * stradale percorribili in entrambe le direzioni; per tali campioni è
     * necessario determinare il senso di percorrenza dell'arco.
     * 
     * @return il numero di campioni posizionati su archi percorribili in
     * entrambe le direzioni
     */
    public synchronized long getSamplesOnBidirectionalEdges() {
        return samplesOnBidirectionalEdges;
    }

    /**
     * Aggiunge un arco del grafo stradale per il quale sono state elaborate
     * le statistiche.
     */
    public synchronized void addEdgeWithStats() {
        edgesWithStats++;
    }

    /**
     * @return il numero di archi del grafo stradale per cui sono state
     * elaborate statistiche
     */
    public synchronized long getEdgesWithStats() {
        return edgesWithStats;
    }

    /**
     * Aggiunge un arco percorribile in entrambi i sensi di marcia.
     */
    public synchronized void addBidirectionalEdge() {
        bidirectionalEdges++;
    }

    /**
     * Restituisce il numero di campioni posizionati su archi del grafo
     * stradale percorribili in entrambe le direzioni; per tali campioni è
     * necessario determinare il senso di percorrenza dell'arco.
     * 
     * @return il numero di campioni posizionati su archi percorribili in
     * entrambe le direzioni
     */
    public synchronized long getBidirectionalEdges() {
        return bidirectionalEdges;
    }

    /**
     * @return la rappresentazione JSON delle metriche
     */
    public synchronized String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(String.format("\"uptime\": \"%s\",", DateTime.msecsToStr(getUptime())));
        builder.append(String.format("\"jobs\": %d,", realTimeJobs + historicJobs));
        builder.append(String.format("\"jobErrors\": %d,", jobErrors));
        builder.append(String.format("\"realTimeJobs\": %d,", realTimeJobs));
        builder.append(String.format("\"historicJobs\": %d,", historicJobs));
        builder.append(String.format("\"experimentalJobs\": %d,", experimentalJobs));
        builder.append(String.format("\"samples\": %d,", samples));
        builder.append(String.format("\"samplesOnBidirectionalEdges\": %d,", samplesOnBidirectionalEdges));
        builder.append(String.format("\"edgesWithStats\": %d,", edgesWithStats));
        builder.append(String.format("\"bidirectionalEdges\": %d,", bidirectionalEdges));
        builder.append(String.format("\"edgesWithStats\": %d,", edgesWithStats));
        builder.append("}");
        return builder.toString();
    }
}
