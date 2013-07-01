package sistematica.gim.legolas.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import sistematica.gim.legolas.Configuration;
import sistematica.pbutils.FormatLogger;

/**
 * Contiene metodi per l'accesso e la manipolazione delle sorgenti di dati raw
 * FCD sul DB.
 */
public class SourcesDAO extends sistematica.pbutils.DAO {

    private static final FormatLogger logger = FormatLogger.getLogger(SourcesDAO.class);
    private static Configuration configuration;

    /**
     * Crea una nuova istanza di SourcesDAO.
     * 
     * @param connection la connessione al DB
     */
    public SourcesDAO(Configuration configuration, Connection connection) {
        super(connection);
        this.configuration = configuration;
    }

    /**
     * Restituisce la lista delle sorgenti di dati raw FCD presenti sul DB.
     * 
     * @return la lista delle sorgenti
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    public List<Source> getSources() throws SQLException {
        logger.debug("getSources: %s", configuration.SQL_GET_SOURCES);

        List<Source> sources = new LinkedList<Source>();
        PreparedStatement stmt = null;
        ResultSet res = null;

        try {
            stmt = connection.prepareStatement(configuration.SQL_GET_SOURCES);
            res = stmt.executeQuery();

            String id;
            String description;
            while (res.next()) {
                id = res.getString("id_source");
                description = res.getString("description");
                sources.add(new Source(id, description));
            }

            connection.commit();
        } finally {
            close(res);
            close(stmt);
        }

        return sources;
    }
}
