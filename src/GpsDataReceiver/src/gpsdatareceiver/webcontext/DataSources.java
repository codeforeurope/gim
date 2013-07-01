package gpsdatareceiver.webcontext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class DataSources
{
	private static String defaultDs;
	private static Hashtable<String, DataSource> datasurces = new Hashtable<String, DataSource>(10);
	private static Logger log = Logger.getLogger(DataSources.class);
	
	public static void addDataSources(DataSource ds,String dsName) throws Exception
	{
		if (ds != null)
			{
				if (!(datasurces.isEmpty()) && datasurces.containsKey(dsName))
					throw new Exception("DataSources '"+dsName+"' already defined for an apllication scoped object. Please choose a unique name!");
				
				try
				{
					if (datasurces.isEmpty()) defaultDs = dsName;
					
					datasurces.put(dsName, ds);
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					throw e;
				}
				
			}
	}
	
	private static Connection getConn(String DatasourcesName) 	
	{
		Connection conn = null;
		DataSource d = null;
		if (DatasourcesName == null) 
		{
			d = (DataSource)datasurces.get(defaultDs);
		}
		else 
		{
			d = (DataSource)datasurces.get(DatasourcesName);
		}

		try
		{
			conn = d.getConnection();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
			
		return conn;
		
	}
	
	/*
	 *  Gestione CONNESSIONE
	 */
	public static Connection getConnection(String DatasourcesName) 	
	{
		return getConn(DatasourcesName);
	}
	
	public static Connection getConnection()
	{
        return getConn(null);
	}

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
	
	public static void closeResources(Statement stmt, ResultSet rs)
    {
		if (stmt != null)
			try
			{
				stmt.close();
			}
			catch (SQLException e)
			{
				log.error(e.getMessage(), e);
			}
		if (rs != null)
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				log.error(e.getMessage(), e);
			}
    }
}
