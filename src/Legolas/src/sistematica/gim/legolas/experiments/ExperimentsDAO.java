package sistematica.gim.legolas.experiments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.algorithm.DensityModel;
import sistematica.gim.legolas.entity.RoadGraphDirection;
import sistematica.gim.legolas.entity.TrafficStat;
import sistematica.pbutils.FormatLogger;

/**
 * Contiene metodi per l'accesso e la manipolazione degli esperimenti sul DB.
 */
public class ExperimentsDAO extends sistematica.pbutils.DAO {

    private static final FormatLogger logger = FormatLogger.getLogger(ExperimentsDAO.class);
    private Configuration configuration;
    
    /**
     * Crea una nuova istanza di ExperimentsDAO.
     * 
     * @param connection la connessione al DB
     */
    public ExperimentsDAO(Configuration configuration, Connection connection) {
        super(connection);
        this.configuration = configuration;
    }

    /**
     * Restituisce l'esperimento specificato o tutti gli esperimenti sul DB.
     * 
     * @param idExperiment l'ID dell'esperimento desiderato; se è null verranno restituiti tutti gli esperimenti
     * @return la lista degli esperimenti
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    private List<Experiment> getExperiments(Long idExperiment) throws SQLException {
        String query = configuration.SQL_GET_EXPERIMENTS;
        if (idExperiment != null) {
            query += " WHERE id_experiment = ?";
        }

        logger.debug("getExperiments: %s", formatQuery(query, idExperiment)); // Se non ci sono "?" funziona lo stesso

        List<Experiment> experiments = new LinkedList<Experiment>();

        PreparedStatement stmt = null;
        ResultSet res = null;

        try {
            stmt = connection.prepareStatement(query);
            if (idExperiment != null) {
                stmt.setLong(1, idExperiment);
            }
            res = stmt.executeQuery();

            long id;
            String description;
            String sources;
            DensityModel model;
            Date timestamp;
            while (res.next()) {
                id = res.getLong("id_experiment");
                description = res.getString("description");
                sources = res.getString("sources");
                model = DensityModel.fromString(res.getString("model"));
                timestamp = res.getTimestamp("timestamp");
                experiments.add(new Experiment(id, description, sources, model, timestamp));
            }

            connection.commit();
        } finally {
            close(res);
            close(stmt);
        }

        return experiments;
    }

    /**
     * Restituisce la lista degli esperimenti presenti su DB.
     * 
     * @return gli esperimenti presenti sul DB
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    public List<Experiment> getExperiments() throws SQLException {
        return getExperiments(null);
    }

    /**
     * Restituisce l'esperimento specificato.
     * 
     * @param id l'ID dell'esperimento desiderato
     * @return l'esperimento specificato o null se non è stato trovato
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    public Experiment getExperiment(long id) throws SQLException {
        List<Experiment> experiments = getExperiments(id);
        if (experiments.isEmpty()) {
            return null;
        } else {
            return experiments.get(0);
        }
    }

    /**
     * Cancella un esperimento dal database.
     * 
     * @param id ID dell'esperimento nel DB
     * @throws SQLException se c'è un errore nella query di cancellazione
     */
    public void deleteExperiment(long id) throws SQLException {
        logger.debug("deleteExperiment: %s", formatQuery(configuration.SQL_DELETE_EXPERIMENT, id));

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(configuration.SQL_DELETE_EXPERIMENT);
            stmt.setLong(1, id);
            stmt.executeUpdate();
            connection.commit();
        } finally {
            close(stmt);
        }
    }

    /**
     * Inserisce un esperimento sul DB.
     * 
     * @param experiment l'esperimento da inserire
     * @throws SQLException se c'è un errore nella query d'inserimento
     */
    public void insertExperiment(Experiment experiment) throws SQLException {
        logger.debug("insertExperiment: %s", formatQuery(configuration.SQL_INSERT_EXPERIMENT, experiment.getDescription(), experiment.getSources(), experiment.getDensityModel(), experiment.getTimestamp()));

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(configuration.SQL_INSERT_EXPERIMENT);
            stmt.setString(1, experiment.getDescription());
            stmt.setString(2, experiment.getSources());
            stmt.setString(3, experiment.getDensityModel().toString());
            stmt.setTimestamp(4, new Timestamp(experiment.getTimestamp().getTime()));
            stmt.executeUpdate();
            connection.commit();
        } finally {
            close(stmt);
        }
    }

    /**
     * Restituisce gli esperimenti che sono stati eseguiti.
     * 
     * @return la lista degli esperimenti eseguiti
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    public List<Experiment> getExecutedExperiments() throws SQLException {
        logger.debug("getExecutedExperiments: %s", configuration.SQL_GET_EXECUTED_EXPERIMENTS);

        List<Experiment> experiments = new LinkedList<Experiment>();
        PreparedStatement stmt = null;
        ResultSet res = null;

        try {
            stmt = connection.prepareStatement(configuration.SQL_GET_EXECUTED_EXPERIMENTS);
            res = stmt.executeQuery();

            long id;
            String description;
            String sources;
            DensityModel model;
            Date timestamp;
            while (res.next()) {
                id = res.getLong("id_experiment");
                description = res.getString("description");
                sources = res.getString("sources");
                model = DensityModel.fromString(res.getString("model"));
                timestamp = res.getTimestamp("timestamp");
                experiments.add(new Experiment(id, description, sources, model, timestamp));
            }

            connection.commit();
        } finally {
            close(res);
            close(stmt);
        }

        return experiments;
    }

    /**
     * Restituisce le statistiche di un esperimento.
     * 
     * @param idExperiment l'ID dell'esperimento sul DB
     * @return le statistiche dell'esperimento specificato
     * @throws SQLException se c'è un errore nell'esecuzione della query
     */
    public List<TrafficStat> getExperimentTrafficStats(long idExperiment) throws SQLException {
        List<TrafficStat> stats = new ArrayList<TrafficStat>();

        PreparedStatement stmt = null;
        ResultSet res = null;

        try {
            stmt = connection.prepareStatement(configuration.SQL_GET_EXPERIMENT_STATS);
            stmt.setLong(1, idExperiment);
            res = stmt.executeQuery();

            while (res.next()) {
                long idEdge = res.getLong("idno");
                RoadGraphDirection edgeDirection = RoadGraphDirection.fromInt(res.getInt("dir"));
                long samples = res.getLong("samples");
                long vehicles = res.getLong("vehicles");
                double avgSpeed = res.getDouble("avg_speed");
                double stdDevSpeed = res.getDouble("stddev_speed");
                double density = res.getDouble("density");
                double flow = res.getDouble("flow");
                stats.add(new TrafficStat(idEdge, edgeDirection, samples, vehicles, avgSpeed, stdDevSpeed, density, flow));
            }
        } finally {
            close(stmt);
            close(res);
        }

        return stats;
    }
}
