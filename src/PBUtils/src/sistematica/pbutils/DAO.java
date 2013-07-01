/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sistematica.pbutils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Dalla terminologia squisitamente Microsoft, Data Access Object, ovvero una
 * classe contenente metodi per interagire con un database.
 *
 * <p>NOTA: la connessione al DB è disponibile anche nelle classi derivate
 * tramite il membro dato "connection".
 */
public abstract class DAO {

    /**
     * Il DataBase Management System usato da tutti i DAO.
     */
    public static DBMS dbms = DBMS.ORACLE;
    /**
     * Il logger (Log4J).
     */
    private static final FormatLogger logger = FormatLogger.getLogger(DAO.class);
    /**
     * La connessione al DB.
     */
    protected Connection connection;

    /**
     * Crea una nuova istanza di DAO.
     *
     * @param connection la connessione al database
     */
    public DAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Sostituisce i punti interrogativi delle query per i
     * {@link PreparedStatement} con i valori a cui fanno effettivamente
     * riferimento.
     *
     * @param query la query coi punti interrogativi del {@link PreparedStatement}
     * @param values i valori dei parametri da sostituire ai punti interrogativi
     * @return la query con i valori effettivi dei parametri
     */
    protected String formatQuery(String query, Object... values) {
        String ret = query;

        if (values != null) {
            for (Object o : values) {
                if (o == null) {
                    ret = ret.replaceFirst("\\?", "NULL");
                } else if (o.getClass() == String.class) {
                    ret = ret.replaceFirst("\\?", "'" + o.toString() + "'");
                } else if (o.getClass() == Timestamp.class || o.getClass() == Date.class) {
                    switch (dbms) {
                        case MYSQL:
                            ret = ret.replaceFirst("\\?", String.format("TIMESTAMP('%s')", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(o)));
                            break;
                        case POSTGRES:
                            ret = ret.replaceFirst("\\?", String.format("TIMESTAMP '%s'", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(o)));
                            break;
                        default: // Oracle, for backwards compatibility
                            ret = ret.replaceFirst("\\?", String.format("TO_DATE('%s','dd-mm-yyyy hh24:mi:ss')", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(o)));
                    }
                } else {
                    ret = ret.replaceFirst("\\?", o.toString());
                }
            }
        }
        return ret;
    }

    /**
     * Chiude un oggetto JDBC (es. ResultSet, Statement, etc...).
     *
     * @param o l'oggetto JDBC
     */
    protected void close(Object o) {
        if (o != null) {
            try {
                if (o instanceof ResultSet) {
                    ((ResultSet) o).close();
                } else if (o instanceof Statement) {
                    ((Statement) o).close();
                } else if (o instanceof PreparedStatement) {
                    ((PreparedStatement) o).close();
                } else if (o instanceof CallableStatement) {
                    ((CallableStatement) o).close();
                } else if (o instanceof Connection) {
                    ((Connection) o).close();
                } else {
                    throw new RuntimeException("Cannot close a " + o.getClass().getName());
                }
            } catch (SQLException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    /**
     * Cerca di eseguire statement.executeUpdate() un certo numero di volte,
     * ritentando in caso di SQLTransactionRollbackException (il server DB ha
     * effettuato il rollback di sua spontanea iniziativa, ad esempio per
     * problemi di locking).
     *
     * @param statement lo statement di cui bisogna fare l'executeUpdate
     * @param times il numero di tentativi
     * @throws SQLException in caso di errori nell'esecuzione della query
     */
    protected void tryExecuteUpdate(PreparedStatement statement, int times) throws SQLException {
        boolean done = false;

        while (!done && times > 0) {
            try {
                statement.executeUpdate();
                done = true;
            } catch (SQLTransactionRollbackException ex) {
                --times;
                logger.warn("Transaction roll-back by DB, trying %s other times.", ex, times);
            }
        }
    }

    /**
     * Cerca di eseguire statement.executeBatch() un certo numero di volte,
     * ritentando in caso di SQLTransactionRollbackException (il server DB ha
     * effettuato il rollback di sua spontanea iniziativa, ad esempio per
     * problemi di locking).
     *
     * @param statement lo statement di cui bisogna fare l'executeUpdate
     * @param times il numero di tentativi
     * @throws SQLException in caso di errori nell'esecuzione della query
     */
    protected void tryExecuteBatch(PreparedStatement statement, int times) throws SQLException {
        boolean done = false;

        while (!done && times > 0) {
            try {
                statement.executeBatch();
                done = true;
            } catch (SQLTransactionRollbackException ex) {
                --times;
                logger.warn("Transaction roll-back by DB, trying %s other times.", ex, times);
            }
        }
    }
}
