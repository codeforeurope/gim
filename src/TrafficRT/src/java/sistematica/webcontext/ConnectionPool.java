package sistematica.webcontext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author A. Rossini - 18/ago/2008 Classe Wrapper per mantenere la
 *         compatibilità
 */
public class ConnectionPool
{
	private static final Logger log = Logger.getLogger(ConnectionPool.class);

	private static String dsName = "";

	/**
	 * 
	 */
	public ConnectionPool(String dsName)
	{
		super();
		this.dsName = dsName;
		// TODO Auto-generated constructor stub
	}

	public static Connection getConnection() throws SQLException
	{
		Connection conn = null;
		conn = DataSources.getConnection(dsName);
		return conn;
	}

	public static void returnConnection(Connection conn)
	{
		DataSources.returnConnection(conn);
	}

	public void managePoolFailure(Connection conn, Statement stmt, ResultSet rs)
	{
		DataSources.closeResources(stmt, rs);
		DataSources.returnConnection(conn);
	}

	/**
	 * 
	 * @param table
	 * @param field
	 * @param where
	 * @return
	 * @throws Exception
	 */
	public String dbLookup(Connection conn, String table, String field, String where) throws SQLException
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
				conn = DataSources.getConnection(dsName);
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
			log.error(this.getClass().getName() + ": " + e.getMessage());

			if (!isExternalConn)
			{
				DataSources.closeResources(stmt, rs);
				DataSources.returnConnection(conn);
				// managePoolFailure(conn,stmt,rs);

			}

			throw e;
		}
	}

	public String dbLookup(String table, String field, String where) throws SQLException
	{
		return dbLookup(null, table, field, where);
	}

}