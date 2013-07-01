package sistematica.gim.legolas.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sistematica.gim.legolas.Configuration;
import sistematica.pbutils.DateTime;
import sistematica.pbutils.FormatLogger;
import sistematica.pbutils.SimplePoint;

/**
 * Rappresenta il grafo stradale. 
 * 
 * <p>Tra le altre cose viene utilizzata per distinguere il senso di percorrenza
 * di un veicolo su un arco stradale qualora si trovi su un arco percorribile 
 * in entrambi i sensi.
 */
public class RoadGraph extends sistematica.pbutils.DAO {

    private static final FormatLogger logger = FormatLogger.getLogger(RoadGraph.class);
    private static final Pattern REAL_NUMBER_REGEX = Pattern.compile("\\d+(\\.\\d+)?");
    private Map<Long, Edge> graph = new HashMap<Long, Edge>(); // {idEdge, edge}

    /**
     * Crea una nuova istanza di RoadGraph, caricando contestualmente
     * tutti gli archi del grafo stradale dal DB (le loro geometrie ed altre
     * caratteristiche utili per i calcoli di Legolas).
     * 
     * @param configuration la configurazione del programma
     * @param connection la connessione al DB
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    public RoadGraph(Configuration configuration, Connection connection) throws SQLException {
        super(connection);

        long startTime = System.currentTimeMillis();
        logger.info("Loading road graph...");

        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = connection.prepareStatement(configuration.SQL_GET_ROADGRAPH);
            res = stmt.executeQuery();
            while (res.next()) {
                // Tira fuori stringhe come questa:
                // MULTILINESTRING((12.5681600570679 41.8826103210449,12.5693798065186 41.8825607299805))
                String multiLineString = res.getString("shape");
                long id = res.getLong("id");

                Matcher matcher = REAL_NUMBER_REGEX.matcher(multiLineString);
                List<SimplePoint> points = new LinkedList<SimplePoint>();
                boolean goodShape = true;

                while (matcher.find()) {
                    double x = Double.parseDouble(matcher.group()); // Latitudine = x (da Greenwich verso est)
                    if (matcher.find()) {
                        double y = Double.parseDouble(matcher.group()); // Longitudine = y (dall'equatore in su)
                        points.add(new SimplePoint(x, y));
                    } else {
                        goodShape = false;
                        logger.warn("Shape %d has an odd number of coordinates", id);
                    }
                }

                if (goodShape) { // Aggiungo l'edge solo se la shape è corretta
                    RoadGraphDirection direction = RoadGraphDirection.fromInt(res.getInt("direction"));
                    String name = res.getString("name");
                    double speed = res.getDouble("speed");
                    double lanes = res.getDouble("lanes");
                    graph.put(id, new Edge(id, points, direction, name, speed, lanes));
                }
            }

            long elapsedMsecs = System.currentTimeMillis() - startTime;
            logger.info("%d edges loaded in %s.", graph.size(), DateTime.msecsToStr(elapsedMsecs));
        } finally {
            close(stmt);
            close(res);
        }
    }

    /**
     * Controlla se un dato arco è contenuto nel grafo.
     * 
     * @param id l'ID dell'arco da controllare
     * @return true se l'arco con l'ID specificato è contenuto nel grafo, false altrimenti
     */
    public boolean containsEdge(long id) {
        return graph.containsKey(id);
    }

    /**
     * Restituisce la lista di punti dell'arco specificato. I punti sono sempre
     * ordinati nel senso convenzionale (vedi l'enumerazione {@link Direction}).
     * @param id l'ID dell'arco desiderato
     * @return un oggetto rappresentante l'arco richiesto o null se l'arco non 
     *         esiste
     */
    public Edge getEdge(long id) {
        return graph.get(id);
    }
}
