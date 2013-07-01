package sistematica.gim.legolas.experiments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.TrafficStat;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.OutputTask;
import sistematica.pbutils.FormatLogger;

/**
 * Gestisce la scrittura sul DB dei risultati di un job sperimentale.
 */
public class ExperimentalOutputTask extends OutputTask {

    private static final FormatLogger logger = FormatLogger.getLogger(ExperimentalOutputTask.class);
    private Configuration configuration;
    private PreparedStatement stmtExperimentalInsert;
    private PreparedStatement stmtExperimentalDelete;
    private Experiment experiment;

    /**
     * Crea una nuova istanza di ExperimentalOutputTask.
     * 
     * @param descriptor il descrittore del job di cui fa parte il task
     * @param connection la connessione al DB
     * @param experiment l'esperimento a cui si riferisce il task
     * @throws SQLException se c'è un errore nelle query o nella connessione al DB
     */
    public ExperimentalOutputTask(JobDescriptor descriptor, Connection connection) throws SQLException {
        super(descriptor, connection);
        this.configuration = descriptor.getConfiguration();
        this.experiment = ((ExperimentalJobDescriptor) descriptor).getExperiment();
    }

    @Override
    protected void initStatements() throws SQLException {
        logger.debug("OutputTask (experiment): %s", configuration.SQL_INSERT_EXPERIMENTAL_STATS);
        stmtExperimentalInsert = connection.prepareStatement(configuration.SQL_INSERT_EXPERIMENTAL_STATS);

        logger.debug("OutputTask (delete old experiment): %s", configuration.SQL_DELETE_EXPERIMENTAL_STATS);
        stmtExperimentalDelete = connection.prepareStatement(configuration.SQL_DELETE_EXPERIMENTAL_STATS);

        try {
            stmtExperimentalDelete.setLong(1, experiment.getId());
            stmtExperimentalDelete.executeUpdate();
            connection.commit();
        } finally {
            stmtExperimentalDelete.close();
        }
    }

    @Override
    public void addStat(TrafficStat stat) throws SQLException {
        super.addStat(stat);

        Timestamp timestamp = new Timestamp(descriptor.getTimestamp().getTime());

        stmtExperimentalInsert.setLong(1, experiment.getId());
        stmtExperimentalInsert.setLong(2, stat.getIdEdge());
        stmtExperimentalInsert.setInt(3, stat.getEdgeDirection().getValue());
        stmtExperimentalInsert.setLong(4, stat.getSamplesCount());
        stmtExperimentalInsert.setLong(5, stat.getVehiclesCount());
        setDoubleOrNull(stmtExperimentalInsert, 6, stat.getAvgSpeed());
        setDoubleOrNull(stmtExperimentalInsert, 7, stat.getStdDevSpeed());
        setDoubleOrNull(stmtExperimentalInsert, 8, stat.getDensity());
        setDoubleOrNull(stmtExperimentalInsert, 9, stat.getFlow());
        stmtExperimentalInsert.setTimestamp(10, timestamp);
        stmtExperimentalInsert.addBatch();
    }

    @Override
    public void flush() throws SQLException {
        try {
            stmtExperimentalInsert.executeBatch();
            connection.commit();
        } catch(SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            stmtExperimentalInsert.close();
        }
    }
}
