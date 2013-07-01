package sistematica.webcontext;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.sql.DataSource;


import org.apache.log4j.Logger;
import sistematica.rdbms.Rdbms;
import sistematica.rdbms.RdbmsException;

public class DataSources
{
	private static final Logger log = Logger.getLogger(DataSources.class);

	private static Hashtable<String, DataSource> datasurces = new Hashtable<String, DataSource>(10);
	private static Hashtable<String, Rdbms> rdbmsHashTable = new Hashtable<String, Rdbms>();

	private static String defaultDs = "Dafault name not assigned";

	private static DataSources instance = new DataSources();
	
	public static int DATABASE_RECONNECTION_TRY_NUM;
	
	public static int DATABASE_RECONNECTION_INTERVAL;
	public static boolean DATABASE_RECONNECTION_SHUTDOWN_ENABLED;

	private static long lastSend = 0L;
	/**
	 * Costruttore
	 */
	private DataSources()
	{

	}

	/**
	 * Aggiungi un datasources
	 * 
	 * @param ds Datasources
	 * @param dsName nome del DataSources
	 * @throws SQLException
	 */
	public static void addDataSources(DataSource ds, String dsName) throws Exception
	{
		if (ds != null)
		{
			if (!(datasurces.isEmpty()) && datasurces.containsKey(dsName))
				throw new Exception("DataSources '" + dsName + 
					"' already defined for an application scoped object. Please choose a unique name!");

			if (datasurces.isEmpty())
				defaultDs = dsName; // default � il primo datasource che carica

			datasurces.put(dsName, ds);
		}
	}

	public static void removeDataSources(String dsName)
	{
		if (datasurces != null && !datasurces.isEmpty() && datasurces.containsKey(dsName))
			datasurces.remove(dsName);
	}

	private static Connection getConn(String dataSourceName) throws SQLException
	{
		DataSource d = null;
                System.out.println("..getConn");
                System.out.println("dataSourceName "+dataSourceName);
		if (dataSourceName == null)
		{
			d = (DataSource) datasurces.get(defaultDs);

		}
		else
		{
			d = (DataSource) datasurces.get(dataSourceName);
		}
                System.out.println(d);
		if (d != null)
		{
			Connection conn = d.getConnection();
			int iterations = 0;
                        System.out.println("conn"+conn);
			while(!(testConnection(conn)))
			{
//				sendMetric(1); 
				if (DATABASE_RECONNECTION_TRY_NUM >= iterations||DATABASE_RECONNECTION_TRY_NUM<0)
				{
					log.warn("Impossibile stabilire una connessione. Reistanziazione del datasource");
					String className = d.getClass().getName();
					try
					{
						DataSource ds = (DataSource) Class.forName(className).newInstance();
						removeDataSources("default");
						addDataSources(ds, "default");
						d = (DataSource) datasurces.get(defaultDs);
						conn = d.getConnection();
					}
					catch (Exception e)
					{
						log.error(e.getMessage(), e);
					}
					
					try
					{
						Thread.sleep(DATABASE_RECONNECTION_INTERVAL);
					}
					catch (InterruptedException e) {}
					
					iterations++;
				}
				else
				{
					log.error("Impossibile stabilire una connessione. Superato il numero massimo di tentativi");
					if (DATABASE_RECONNECTION_SHUTDOWN_ENABLED)
					{
						log.fatal("L'applicazione sara' terminata per l'impossibilita' di stabilire una connessione al DB");
						System.exit(-1);
					}
					else
					{
						throw new SQLException("Impossibile stabilire una connessione");
					}
				}
			} 
//			SismonLogger.getInstance().sendMetric("DB_ERROR", 0);
			return conn;
		}
		else
			throw new RuntimeException("No datasource : " + (dataSourceName != null ? defaultDs : dataSourceName));
	}

	
	/**
	 * Verifica se � presente un datasurce con come dsName
	 * 
	 * @param dsName
	 * @return true se � presente
	 */
	public static boolean containsDs(String dsName)
	{
		boolean result = false;

		if (dsName != null && dsName.length() > 0)
		{
			result = datasurces.containsKey(dsName);
		}

		return result;
	}

	/**
	 * Ritorna la connessione di default (quella relativa al primo datasources
	 * caricato)
	 * 
	 * @return Connection
	 */
	public static Connection getConnection() throws SQLException
	{
		return getConn(null);
	}

	/**
	 * Ritorna la connessione allo shared database
	 * 
	 * @return Connection
	 */
	// public static Connection getSDBConnection()
	// {
	// return getConn("java:comp/env/jdbc/shareddb");
	// }
	/**
	 * Ritorna la connessione relativa al primo datasources con nome
	 * DatasourcesName.
	 * 
	 * @param DatasourcesName
	 * @return Connection
	 */
	public static Connection getConnection(String DatasourcesName) throws SQLException
	{
		return getConn(DatasourcesName);
	}

	/**
	 * Restituisce la connessione
	 * 
	 * @param conn
	 */
	public static void returnConnection(Connection conn)
	{
		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * gestion HashTable
	 */
	public static Hashtable<String, Rdbms> getRdbmsHash()
	{
		return rdbmsHashTable;
	}

	public static void setRdbmshash(Hashtable<String, Rdbms> Hash)
	{
		rdbmsHashTable = Hash;
	}

	/**
	 * Gestion Rdbms
	 * 
	 * @throws SQLException
	 */

	public static Rdbms getRdbms(Connection conn) throws RdbmsException, SQLException
	{
		// estraggo il nome del pool dalla connessione
		String key = conn.getMetaData().getDatabaseProductName();
		return (Rdbms) rdbmsHashTable.get(key);

	}

    public static Connection unwrapConnection(Connection conn, Class expectedType) throws Exception
    {
        if (conn.getClass().isAssignableFrom(expectedType))
            return conn;
        else
        {
//            return (Connection) conn.unwrap(expectedType);
            Method[] ms = conn.getClass().getMethods();

            for (Method m : ms)
            {
                String mName = m.getName();
                if ("getRealConnection".equals(mName) || "getConnection".equals(mName))
                {
                    return (Connection) m.invoke(conn, (Object[]) null);
                }
            }

            throw new Exception("Cannot unwrap connection "+conn);
        }
    }

	@Deprecated
	public static Connection unwrapConnection(Connection conn)
	{
		try
		{
			Method m = conn.getClass().getMethod("getRealConnection", (Class[]) null);
			return (Connection) m.invoke(conn, (Object[]) null);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// ////////////////////////////////////////////////////////////
	// RETROCOMPATIBILITA'

	/**
	 * 
	 * @param conn Connection
	 * @param sql String
	 * @return String
	 * @throws SQLException
	 */
	public static String dbLookup(Connection conn, String sql) throws SQLException
	{
		return dbLookup(null, conn, sql);
	}

	/**
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static String dbLookup(String poolName, Connection conn, String sql) throws SQLException
	{
		String value = "";

		Statement stmt = null;
		ResultSet rs = null;
		boolean isExternalConn = true;

		try
		{
			if (conn == null)
			{
				conn = getConnection(poolName);
				isExternalConn = false;
			}

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next())
			{
				value = rs.getString(1);
			}

			rs.close();
			stmt.close();

			if (!isExternalConn)
				returnConnection(conn);
			return value;
		}
		catch (SQLException e)
		{
			log.error(DataSources.class.getName() + ": " + e.getMessage(), e);
			log.debug(DataSources.class.getName() + "SQL: " + sql);

			closeResources(stmt, rs);

			if (!isExternalConn)
				returnConnection(conn);

			throw e;
		}
	}

	/**
	 * 
	 * @param conn Connection
	 * @param table String
	 * @param field String
	 * @param where String
	 * @return String
	 * @throws SQLException
	 */
	public static String dbLookup(Connection conn, String table, String field, String where) throws SQLException
	{
		return dbLookup(null, conn, table, field, where);
	}

	/**
	 * 
	 * @param poolName String
	 * @param conn Connection
	 * @param table String
	 * @param field String
	 * @param where String
	 * @return String
	 * @throws SQLException
	 */
	public static String dbLookup(String poolName, Connection conn, String table, String field, String where) throws SQLException
	{
		String value = "";
		String sqlStr = "";

		if (table != null && table.equals("") == false)
			sqlStr = "SELECT " + field + " FROM " + table + " WHERE 1=1 ";
		else
			sqlStr = "SELECT " + field;

		if (where != null && where.length() > 0)
		{
			sqlStr += " AND " + where;
		}

		Statement stmt = null;
		ResultSet rs = null;
		boolean isExternalConn = true;

		try
		{
			if (conn == null)
			{
				conn = getConnection(poolName);
				isExternalConn = false;
			}

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlStr);

			if (rs.next())
			{

				value = rs.getString(1);
			}

			rs.close();
			stmt.close();

			if (!isExternalConn)
				returnConnection(conn);
			return value;
		}
		catch (SQLException e)
		{
			log.error(DataSources.class.getName() + ": " + e.getMessage(), e);
			log.debug(DataSources.class.getName() + "SQL: " + sqlStr);

			closeResources(stmt, rs);

			if (!isExternalConn)
				returnConnection(conn);

			throw e;
		}
	}

	/**
	 * 
	 * @param poolName String
	 * @param table String
	 * @param field String
	 * @param where String
	 * @return String
	 * @throws SQLException
	 */
	public static String dbLookup(String poolName, String table, String field, String where) throws SQLException
	{
		return dbLookup(poolName, null, table, field, where);
	}

	/**
	 * 
	 * @param table
	 * @param field
	 * @param where
	 * @return
	 * @throws SQLException
	 */
	public static String dbLookup(String table, String field, String where) throws SQLException
	{
		return dbLookup(null, null, table, field, where);
	}

	/**
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static String dbLookup(String sql) throws SQLException
	{
		return dbLookup(null, sql);
	}

	public static String getTimeStamp(Connection conn, String table, String field, String where) throws SQLException
	{
		String value = "";
		String sqlStr = "";

		if (table != null && table.equals("") == false)
			sqlStr = "SELECT " + field + " FROM " + table + " WHERE 1=1 ";
		else
			sqlStr = "SELECT " + field;

		if (where != null && where.length() > 0)
		{
			sqlStr += " AND " + where;
		}

		Statement stmt = null;
		ResultSet rs = null;
		boolean isExternalConn = true;

		try
		{
			if (conn == null)
			{
				// conn = getConnection(poolName);
				isExternalConn = false;
			}

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlStr);

			if (rs.next())
			{

				value = String.valueOf(rs.getTimestamp(1));
			}

			rs.close();
			stmt.close();

			if (!isExternalConn)
				returnConnection(conn);
			return value;
		}
		catch (SQLException e)
		{
			log.error(DataSources.class.getName() + ": " + e.getMessage(), e);
			log.debug(DataSources.class.getName() + "SQL: " + sqlStr);

			closeResources(stmt, rs);

			if (!isExternalConn)
				returnConnection(conn);

			throw e;
		}

	}

	/**
	 * 
	 * @param stmt Statement
	 * @param rs ResultSet
	 */
	public static void closeResources(Statement stmt, ResultSet rs)
	{

		if (stmt != null)
			try
			{
				stmt.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (rs != null)
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public static DataSources getPools()
	{
		// TODO Auto-generated method stub
		return instance;
	}

	public static ConnectionPool getDefaultPool()
	{
		return new ConnectionPool(defaultDs);
	}
	
	public static DataSource getDefault()
	{
		return (DataSource) datasurces.get(defaultDs);
	}

	private static boolean testConnection(Connection conn)
	{
		boolean res = false;
		try
		{
			if (!conn.isClosed())
				res = true;
			return res;
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
		}
		return res;

	}
}