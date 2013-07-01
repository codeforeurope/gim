package sistematica.gim.legolas.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sistematica.gim.legolas.Configuration;
import sistematica.pbutils.DateTime;
import sistematica.pbutils.FormatLogger;

/**
 * Contiene tutti i dettagli delle strade censite.
 */
public class Streets extends sistematica.pbutils.DAO {

    private static final FormatLogger logger = FormatLogger.getLogger(Streets.class);
    private Configuration configuration;
    private Map<StreetKey, Street> streets = new HashMap<StreetKey, Street>();
    private Map<Edge, StreetDetail> streetDetails = new HashMap<Edge, StreetDetail>();

    /**
     * Crea una nuova istanza di Streets, caricando contestualmente tutti i
     * dettagli delle strade censite dal DB.
     *
     * @param configuration la configurazione del programma
     * @param connection la connessione al DB
     * @param roadGraph il grafo stradale
     * @throws SQLException se c'è un errore nell'esecuzione della query
     * @throws IOException se non è possibile leggere il seriale dell'HDD
     */
    public Streets(Configuration configuration, Connection connection, RoadGraph roadGraph) throws IOException, SQLException {
        super(connection);
        this.configuration = configuration;

        long startTime = System.currentTimeMillis();
        logger.info("Loading streets...");

        // This is madness...
        char[] food = new char[8];
        eat(food);
        String query = configuration.SQL_GET_STREETS_DETAILS.replaceAll("gimpassword", new String(food) + getHddSerial());

        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = connection.prepareStatement(query);
            res = stmt.executeQuery();
            while (res.next()) {
                Edge edge = new Edge(roadGraph.getEdge(res.getLong("idno")));
                edge.setDirection(RoadGraphDirection.fromInt(res.getInt("dirx")));

                long idStreet = res.getLong("id_street");
                StreetDirection dir = StreetDirection.fromInt(res.getInt("dir"));
                StreetKey key = new StreetKey(idStreet, dir);
                long id = res.getLong("id");
                StreetDetail detail = new StreetDetail(key, id, edge.getFreeFlowSpeed());

                streetDetails.put(edge, detail);
            }
        } finally {
            close(stmt);
            close(res);
        }

        try {
            stmt = connection.prepareStatement(configuration.SQL_GET_STREETS);
            res = stmt.executeQuery();
            while (res.next()) {
                long idStreet = res.getLong("id_street");
                StreetDirection direction = StreetDirection.fromInt(res.getInt("dir"));
                StreetKey key = new StreetKey(idStreet, direction);
                Street street = new Street(key, res.getLong("min_id"), res.getLong("max_id"), res.getString("name"), res.getString("dir_name"));
                streets.put(key, street);
            }
        } finally {
            close(stmt);
            close(res);
        }

        long elapsedMsecs = System.currentTimeMillis() - startTime;
        logger.info("%d streets (%d details) loaded in %s.", streets.size(), streetDetails.size(), DateTime.msecsToStr(elapsedMsecs));
    }

    /**
     * Restituisce la Street avente l'ID specificato.
     *
     * @param key identificatore univoco della strada (ID e direzione)
     * @return la Street avente l'ID specificato o null se non c'è
     */
    public Street getStreet(StreetKey key) {
        return streets.get(key);
    }

    /**
     * @return gli identificatori univoci (ID e direzione) di tutte le strade
     * censite
     */
    public Set<StreetKey> getStreetKeys() {
        return streets.keySet();
    }

    /**
     * Restituisce lo StreetDetail associato all'arco del grafo stradale
     * specificato.
     *
     * @param idEdge l'ID dell'arco del grafo stradale
     * @param direction la direzione dell'arco del grafo stradale, disambiguata
     * (-1 o 1)
     * @return lo StreetDetail associato all'arco del grafo stradale specificato
     * o null se non c'è
     */
    public StreetDetail getStreetDetail(long idEdge, RoadGraphDirection direction) {
        Edge edge = new Edge(idEdge, null, direction, null, 0d, 0d);
        return streetDetails.get(edge);
    }

    private void eat(char[] food) {
        int i;
        for (i = 0; i < 7; ++i) {
            switch (i) {
                case 3:
                    food[1] = 114;
                    break;
                case 5:
                    food[0] = 103;
                    break;
                case 4:
                    food[5] = 111;
                    break;
                case 0:
                    food[6] = 110;
                    break;
                case 1:
                    food[3] = food[4] = 115;
                    break;
                case 2:
                    food[2] = 97;
                    break;
                case 6:
                    food[7] = 101;
                    break;
            }
        }
    }

    private String getHddSerial() throws IOException {
        if (configuration.HDD_SERIAL != null) {
            return configuration.HDD_SERIAL;
        } else {
            Process process = Runtime.getRuntime().exec("fdisk -l /dev/sda");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String serial = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("Disk identifier:") != -1) {
                    serial = line.split(":")[1].trim();
                }
            }

            return serial;
        }
    }
}
