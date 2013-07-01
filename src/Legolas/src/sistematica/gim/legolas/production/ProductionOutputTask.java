package sistematica.gim.legolas.production;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.TrafficStat;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobType;
import sistematica.gim.legolas.scheduler.OutputTask;
import sistematica.pbutils.FormatLogger;

/**
 * Gestisce la scrittura dei risultati dell'elaborazione di un job real-time o
 * storico (vedi {@link JobType}) su DB.
 */
public class ProductionOutputTask extends OutputTask {

    private static final int STATEMENT_RETRY_TIMES = 3;
    private static final FormatLogger logger = FormatLogger.getLogger(ProductionOutputTask.class);
    private PreparedStatement stmtRealTimeInsert;
    private PreparedStatement stmtRealTimeDelete;
    private PreparedStatement stmtHistoricDelete;
    private PreparedStatement stmtHistoricInsert;
    private Timestamp timestamp;
    private Configuration configuration;
    private boolean hasDeletedHistoricalStats = false;
    private ChartSpeedOverSpace chartSpeedOverSpace;

    /**
     * Crea una nuova istanza di ProductionOutputTask.
     *
     * @param descriptor il descrittore del job di cui fa parte il task
     * @param connection la connessione al DB
     * @throws SQLException se c'è un errore nelle query o nella connessione al
     * DB
     */
    public ProductionOutputTask(JobDescriptor descriptor, Connection connection) throws SQLException {
        super(descriptor, connection);

        timestamp = new Timestamp(descriptor.getTimestamp().getTime());
        configuration = descriptor.getConfiguration();
        chartSpeedOverSpace = new ChartSpeedOverSpace(configuration, descriptor.getStreets(), timestamp);
    }

    @Override
    protected void initStatements() throws SQLException {
        if (descriptor.getJobType() == JobType.REAL_TIME) {
            logger.debug("OutputTask (insert live stats): %s", configuration.SQL_INSERT_LIVE_STATS);
            stmtRealTimeInsert = connection.prepareStatement(configuration.SQL_INSERT_LIVE_STATS);

            logger.debug("OutputTask (delete old live stats): %s", formatQuery(configuration.SQL_DELETE_LIVE_STATS, timestamp));
            stmtRealTimeDelete = connection.prepareStatement(configuration.SQL_DELETE_LIVE_STATS);
        }

        logger.debug("OutputTask (delete old historical stats): %s", formatQuery(configuration.SQL_DELETE_HISTORICAL_STATS, timestamp));
        stmtHistoricDelete = connection.prepareStatement(configuration.SQL_DELETE_HISTORICAL_STATS);

        logger.debug("OutputTask (insert historical stats): %s", configuration.SQL_INSERT_HISTORICAL_STATS);
        stmtHistoricInsert = connection.prepareStatement(configuration.SQL_INSERT_HISTORICAL_STATS);
    }

    @Override
    public void addStat(TrafficStat stat) throws SQLException {
        super.addStat(stat);

        // Aggiunge la statistica alla tabella dei dati real-time
        if (descriptor.getJobType() == JobType.REAL_TIME) {
            stmtRealTimeInsert.setLong(1, stat.getIdEdge());
            stmtRealTimeInsert.setInt(2, stat.getEdgeDirection().getValue());
            stmtRealTimeInsert.setLong(3, stat.getSamplesCount());
            stmtRealTimeInsert.setLong(4, stat.getVehiclesCount());
            setDoubleOrNull(stmtRealTimeInsert, 5, stat.getAvgSpeed());
            setDoubleOrNull(stmtRealTimeInsert, 6, stat.getStdDevSpeed());
            setDoubleOrNull(stmtRealTimeInsert, 7, stat.getDensity());
            setDoubleOrNull(stmtRealTimeInsert, 8, stat.getFlow());
            stmtRealTimeInsert.setTimestamp(9, timestamp);
            stmtRealTimeInsert.addBatch();
        }

        // Se ci sono dati storici pregressi che si sovrappongono con quelli
        // calcolati dal job storico corrente, vanno cancellati
        if (descriptor.getJobType() == JobType.HISTORIC && !hasDeletedHistoricalStats) {
            stmtHistoricDelete.setTimestamp(1, timestamp);
            tryExecuteUpdate(stmtHistoricDelete, STATEMENT_RETRY_TIMES);
            hasDeletedHistoricalStats = true;
        }

        // Aggiunge la statistica alla tabella dei dati storici
        stmtHistoricInsert.setLong(1, stat.getIdEdge());
        stmtHistoricInsert.setInt(2, stat.getEdgeDirection().getValue());
        stmtHistoricInsert.setLong(3, stat.getSamplesCount());
        stmtHistoricInsert.setLong(4, stat.getVehiclesCount());
        setDoubleOrNull(stmtHistoricInsert, 5, stat.getAvgSpeed());
        setDoubleOrNull(stmtHistoricInsert, 6, stat.getStdDevSpeed());
        setDoubleOrNull(stmtHistoricInsert, 7, stat.getDensity());
        setDoubleOrNull(stmtHistoricInsert, 8, stat.getFlow());
        stmtHistoricInsert.setTimestamp(9, timestamp);
        stmtHistoricInsert.addBatch();

        if (descriptor.getJobType() == JobType.REAL_TIME) {
            chartSpeedOverSpace.addStat(stat);
        }
    }

    @Override
    public void flush() throws SQLException {
        if (!statementsInitialized) { // Non ci sono statistiche quindi non c'è nulla da chiudere
            return;
        }

        try {
            if (descriptor.getJobType() == JobType.REAL_TIME) {
                logger.debug("Running real-time insertion batch...");
                stmtRealTimeInsert.executeBatch();

                logger.debug("Running real-time deletion query...");
                stmtRealTimeDelete.setTimestamp(1, timestamp);
                tryExecuteUpdate(stmtRealTimeDelete, STATEMENT_RETRY_TIMES);
            }

            logger.debug("Running historic insertion batch...");
            tryExecuteBatch(stmtHistoricInsert, STATEMENT_RETRY_TIMES);

            logger.debug("Commiting changes...");
            connection.commit();
            logger.debug("Commit done");
        } catch (SQLException ex) {
            connection.rollback();
            logger.warn("Rollback");
            throw ex;
        } finally {
            if (descriptor.getJobType() == JobType.REAL_TIME) {
                stmtRealTimeInsert.close();
                stmtRealTimeDelete.close();
            }

            stmtHistoricInsert.close();
        }

        if (configuration.CHART_SPEED_OVER_SPACE_ENABLED && descriptor.getJobType() == JobType.REAL_TIME) {
            chartSpeedOverSpace.flush();
        }
        
        logger.debug("Done.");
    }
}
