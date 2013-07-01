package sistematica.gim.legolas.scheduler;

import java.sql.Connection;
import sistematica.gim.legolas.algorithm.Euristics;
import sistematica.gim.legolas.entity.RoadGraphDirection;
import sistematica.gim.legolas.entity.Edge;
import sistematica.gim.legolas.entity.Sample;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.algorithm.HeadingCalculator;
import sistematica.pbutils.FormatLogger;

/**
 * Gestisce la lettura dei dati di ingresso (le rilevazioni FCD) per il calcolo 
 * dei parametri macroscopici del traffico veicolare, ovvero le velocità 
 * istantanee dei singoli veicoli.
 */
public class InputTask extends Task {

    private static final FormatLogger logger = FormatLogger.getLogger(InputTask.class);
    private Configuration configuration;
    private RoadGraph roadGraph;
    private HeadingCalculator headingCalculator;
    private PreparedStatement statement;
    private ResultSet resultSet;
    private Set<Long> edges = new HashSet<Long>();
    private Set<Long> bidirectionalEdges = new HashSet<Long>();
    private long samples = 0;
    private long samplesOnBidirectionalEdges = 0;

    /**
     * Crea una nuova istanza di InputTask.
     * 
     * @param descriptor il descrittore del job di cui fa parte il task
     * @param connection la connessione al DB
     * @throws SQLException se c'è un errore nelle query o nella connessione al DB
     */
    public InputTask(JobDescriptor descriptor, Connection connection) throws SQLException {
        super(descriptor, connection);
        configuration = descriptor.getConfiguration();
        roadGraph = descriptor.getRoadGraph();
        headingCalculator = new HeadingCalculator(configuration);
    }

    /**
     * Prepara la query per i dati di input configurando opportunamente un
     * {@link PreparedStatement}.
     * 
     * @return il {@link PreparedStatement} per la query di selezione
     */
    private PreparedStatement prepareStatement() throws SQLException {
        String sql = configuration.SQL_GET_FCD;

        // Se sto facendo un esperimento potrei dover selezionare un sottinsieme 
        // delle sorgenti dati FCD

        String sources = descriptor.getSources();
        if (sources != null && sources.length() > 0) {
            // Sarebbe stato molto meglio salvare le sorgenti su una lista...
            // Le seguenti tre righe trasformano una stringa del tipo "O, Z, P"
            // in "'O','Z','P'
            sources = sources.replaceAll("\\s+", "");
            sources = sources.replaceAll(",", "','");
            sources = "'" + sources + "'";
            sql += configuration.SQL_GET_FCD_SOURCES_FILTER.replaceAll("%SOURCES%", sources);
        }

        Long maxInputAgeMinutes = descriptor.getConfiguration().MAX_INPUT_AGE_MINUTES;
        Timestamp upperBound = new Timestamp(descriptor.getTimestamp().getTime());
        Timestamp lowerBound = new Timestamp(descriptor.getTimestamp().getTime() - maxInputAgeMinutes * 60 * 1000);

        logger.debug("InputTask: %s", formatQuery(sql, lowerBound, upperBound));
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setTimestamp(1, lowerBound);
        stmt.setTimestamp(2, upperBound);
        return stmt;
    }

    /**
     * Restituisce il prossimo campionamento (rilevazione FCD) disponibile o
     * null se sono finite.
     * 
     * @return la prossima rilevazione FCD disponibile o null se sono finite
     * @throws SQLException se c'è un errore nelle query al DB
     */
    public Sample getNextSample() throws SQLException {
        if (resultSet == null) {
            statement = prepareStatement();
            resultSet = statement.executeQuery();
        }

        if (resultSet.next()) {
            Sample next = new Sample();

            samples++;
            descriptor.getMetrics().addSample();

            next.setIdVehicle(resultSet.getString("id_vehicle"));
            next.setSpeed(resultSet.getDouble("speed"));
            next.setLatitude(resultSet.getDouble("latitude"));
            next.setLongitude(resultSet.getDouble("longitude"));
            next.setHeading(resultSet.getDouble("vehicle_heading"));
            next.setTimestamp(resultSet.getTimestamp("timestamp"));

            // Aggiungo il campione a quelli utilizzati per il calcolo
            // dell'heading tramite le posizioni successive 
            headingCalculator.addSample(next);

            long idEdge = resultSet.getLong("edge");
            Edge edge = roadGraph.getEdge(idEdge);

            // Aggiorna le metriche
            if (!edges.contains(idEdge)) {
                descriptor.getMetrics().addEdgeWithStats();
                edges.add(idEdge);
            }

            double freeFlowSpeed = edge.getFreeFlowSpeed();
            freeFlowSpeed = freeFlowSpeed == -1 ? Double.NaN : freeFlowSpeed;

            double edgeLanes = edge.getLanes();
            edgeLanes = edgeLanes == -1 ? Euristics.getLanes(freeFlowSpeed) : edgeLanes;

            RoadGraphDirection edgeDirection = edge.getDirection();
            if (edgeDirection == RoadGraphDirection.BOTH) {
                // Aggiorna le metriche
                samplesOnBidirectionalEdges++;
                descriptor.getMetrics().addSampleOnBidirectionalEdge();
                if (!bidirectionalEdges.contains(idEdge)) {
                    descriptor.getMetrics().addBidirectionalEdge();
                    bidirectionalEdges.add(idEdge);
                }

                // Se l'arco è bidirezionale, le statistiche riguardano soltanto
                // metà delle corsie totali
                edgeLanes /= 2;
                edgeLanes = edgeLanes < 1 ? 1 : edgeLanes; // ... mai meno di una corsia!

                // Ricalcola l'heading usando le posizioni consecutive dello
                // stesso veicolo, se necessario, ovvero:
                // 1) se l'heading ha il valore -1 o comunque un valore < 0;
                // 2) se la velocità del veicolo è troppo bassa e la rilevazione
                //    GPS non è affidabile.
                if (next.getHeading() < 0 || next.getSpeed() < configuration.MIN_FCD_SPEED_BEFORE_STOP) {
                    next.setHeading(headingCalculator.getHeading(next));
                }

                // Actual calculation
                edgeDirection = Euristics.calculateSampleDirection(roadGraph, next, edge);
            }

            // In caso di archi bidirezionali direzione può venire modificata;
            // anche la velocità media ed il numero di corsie possono essere
            // modificati.
            // Non effettuo modifico nell'edge originale, il cui riferimento è 
            // contenuto nel road graph, bensì in una copia.
            Edge copy = new Edge(edge);
            copy.setDirection(edgeDirection);
            copy.setLanes(edgeLanes);
            next.setEdge(copy);

            // TODO: se c'è qualcosa che non va (es. non ho la velocità di quel
            // tratto di strada stampa un warning e restituisci il risultato
            // di una chiamata ricorsiva, tanto c'è il caso base (quando
            // resultSet.next() è false) che restituisce null.
            return next;

        } else {
            close(resultSet);
            close(statement);

            return null;
        }
    }

    /**
     * @return il numero di campioni letti fino a questo momento
     */
    public long getSamples() {
        return samples;
    }

    /**
     * @return il numero di campioni letti fino a questo momento collocati su
     * archi percorribili in entrambi i sensi di marcia
     */
    public long getSamplesOnBidirectionalEdges() {
        return samplesOnBidirectionalEdges;
    }
}
