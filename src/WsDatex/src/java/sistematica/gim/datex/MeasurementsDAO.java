package sistematica.gim.datex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class MeasurementsDAO {

    private static final Logger logger = Logger.getLogger(MeasurementsDAO.class);
    private static final String SQL_GET_MEASUREMENTS = "SELECT"
            + " strt,"
            + " sdir,"
            + " inst,"
            + " COALESCE(sped, 0) sped,"
            + " COALESCE(flow, 0) flow"
            + " FROM rlnk_rltm "
            + " ORDER BY strt"
            + " LIMIT ? OFFSET ?";
    private DataSource dataSource;

    public MeasurementsDAO() throws NamingException {
        Context initContext = new InitialContext();
        String dsName = Configuration.getInstance().DATASOURCE;
        dataSource = (DataSource) initContext.lookup(dsName);
    }

    private long[] adjustIndexes(long from, long to) {
        long[] indexes = new long[2];
        long min = Configuration.getInstance().MIN_GRAPH_ID;
        long max = Configuration.getInstance().MAX_GRAPH_ID;
        from = Math.abs(from);
        to = Math.abs(to);
        if (from < to) {
            indexes[0] = from < min ? min : from;
            indexes[1] = to > max ? max : to;
        } else {
            indexes[0] = to < min ? min : to;
            indexes[1] = from > max ? max : from;
        }
        return indexes;
    }

    private List<Measurement> getRandomMeasurements(long from, long to) {
        List<Measurement> measurements = new LinkedList<Measurement>();

        Random rand = new Random();
        for (long i = from; i <= to; i++) {
            Measurement m = new Measurement();
            m.setLink(i);
            m.setInstant(Resource.DATEX_DATE_FORMAT.format(new Date()));
            m.setSpeed(rand.nextDouble() * 130);
            m.setFlow(rand.nextDouble() * 1000);
            measurements.add(m);

            Measurement m2 = new Measurement();
            m2.setLink(-i);
            m2.setInstant(Resource.DATEX_DATE_FORMAT.format(new Date()));
            m2.setSpeed(rand.nextDouble() * 130);
            m2.setFlow(rand.nextDouble() * 1000);
            measurements.add(m2);
        }

        return measurements;
    }

    private List<Measurement> getDBMeasurements(long from, long to) throws SQLException {
        List<Measurement> measurements = new LinkedList<Measurement>();

        Connection connection = null;
        ResultSet results = null;
        PreparedStatement statement = null;
        try {
            long limit = to - from;
            long offset = from - 1;
            logger.debug("SQL: " + SQL_GET_MEASUREMENTS.replaceFirst("\\?", "" + limit).replaceFirst("\\?", "" + offset));
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(SQL_GET_MEASUREMENTS);
            statement.setLong(1, limit);
            statement.setLong(2, offset);
            results = statement.executeQuery();
            while (results.next()) {
                Measurement m = new Measurement();
                m.setLink(results.getLong("strt") * results.getLong("sdir"));
                m.setInstant(Resource.DATEX_DATE_FORMAT.format(results.getTimestamp("inst")));
                m.setSpeed(results.getDouble("sped"));
                m.setFlow(results.getDouble("flow"));
                measurements.add(m);
            }
            results.close();
            statement.close();
        } finally {
            if (results != null) {
                results.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }


        return measurements;
    }

    public List<Measurement> getMeasurements(long from, long to) throws SQLException {
        long[] indexes = adjustIndexes(from, to);

        if (Configuration.getInstance().RANDOM_RESULTS) {
            return getRandomMeasurements(indexes[0], indexes[1]);
        } else {
            return getDBMeasurements(indexes[0], indexes[1]);
        }
    }
}
