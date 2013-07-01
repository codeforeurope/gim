package sistematica.gim.legolas.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import sistematica.gim.legolas.entity.TrafficStat;

/**
 * Gestisce la scrittura dei risultati dell'elaborazione su DB. Le statistiche
 * sui singoli archi vanno aggiunte una ad una mediante il metodo 
 * {@link OutputTask#addStat(sistematica.gim.legolas.entity.TrafficStat)} e 
 * alla fine bisogna usare {@link OutputTask#flush()}.
 * 
 * <p><b>NOTA</b>: l'uso dei metodi add/flush consente di utilizzare 
 *       addBatch/executeBatch nella loro implementazione. Dalle prove fatte 
 *       tuttavia mi pare che i tempi dei batch siano gli stessi di quelli 
 *       ottenuti tramite singole executeUpdate; probabilmente MySQL non 
 *       ottimizza quest'operazione. Tuttavia flush va comunque richiamato
 *       <b>tassativamente</b> in quanto svolge anche altri compiti.
 */
public abstract class OutputTask extends Task {

    protected boolean statementsInitialized = false;
    private long edgesWithStats = 0;
    private long vehiclesCount = 0;

    /**
     * Crea una nuova istanza di OutputTask.
     * 
     * @param descriptor il descrittore del job di cui fa parte il task
     * @param connection la connessione al DB
     * @throws SQLException se c'è un errore nelle query o nella connessione al DB
     */
    public OutputTask(JobDescriptor descriptor, Connection connection) throws SQLException {
        super(descriptor, connection);
    }

    /**
     * Imposta il parametro del PreparedStatement al valore specificato o,
     * se NaN, a null.
     * 
     * @param s il PeparedStatement
     * @param position la posizione del parametro da impostare
     * @param value il valore del parametro da impostare (se è NaN viene impostato a null)
     * @throws SQLException se c'è un errore del DB
     */
    protected void setDoubleOrNull(PreparedStatement s, int position, double value) throws SQLException {
        if (Double.isNaN(value)) {
            s.setNull(position, Types.DOUBLE);
        } else {
            s.setDouble(position, value);
        }
    }

    /**
     * Restituisce il numero statistiche che sono state aggiunte all'output
     * fino a questo momento. Se viene richiamato quando tutte le statistiche
     * sono state raggiunte, ovvero dopo aver richiamato {@link OutputTask#flush},
     * restituisce il numero di tutte le statistiche aggiunte all'output.
     * 
     * @return il numero di statistiche aggiunte all'output fino a questo
     * momento
     */
    public long getEdgesWithStats() {
        return edgesWithStats;
    }

    /**
     * Restituisce il numero medio di veicoli per arco basandosi sulle statistiche
     * aggiunte all'output fino a questo momento. Se viene richiamato quando 
     * tutte le statistiche sono state raggiunte, ovvero dopo aver richiamato 
     * {@link OutputTask#flush}, restituisce la media complessiva dei veicoli per
     * arco del grafo stradale.
     * 
     * @return la media dei veicoli per arco basandosi sulle statistiche
     * aggiunte all'output fino a questo momento
     */
    public double getAvgVehiclesPerEdge() {
        return (double) vehiclesCount / edgesWithStats;
    }

    /**
     * Inizializza in modo lazy gli statement usati per le query al DB.
     * 
     * @throws SQLExceptions se c'è un errore nelle query al DB
     */
    protected abstract void initStatements() throws SQLException;

    /**
     * Aggiunge le statistiche su un particolare arco del grafo stradale a
     * quelle da salvare sul DB.
     * 
     * <p><b>NOTA:</b> questo metodo deve essere esteso dalle sottoclassi,
     * ovvero devono sovrascriverlo richiamando però questo metodo all'inizio
     * della nuova versione.
     * 
     * @param stat la velocità media su un arco del grafo stradale
     * @throws SQLException in caso di errore del driver JDBC
     */
    public void addStat(TrafficStat stat) throws SQLException {
        edgesWithStats++;
        vehiclesCount += stat.getVehiclesCount();

        if (!statementsInitialized) {
            initStatements();
            statementsInitialized = true;
        }
    }

    /**
     * Effettua delle operazioni di chiusura per garantire la coerenza dei dati
     * sul DB. Va richiamata dopo tutte le addStat del caso.
     * 
     * @throws SQLException in caso di errore del driver JDBC
     */
    public abstract void flush() throws SQLException;
}
