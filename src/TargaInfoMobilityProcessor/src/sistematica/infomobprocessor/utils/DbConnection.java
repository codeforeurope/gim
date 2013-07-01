/**
 * 
 */
package sistematica.infomobprocessor.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author gsilvestri
 *
 */
public class DbConnection
{
	private Logger m_log4j = Logger.getLogger(DbConnection.class);
	private Connection m_conn = null;
	
	private int m_maxAttemps = 3;
	
	private String dbDriver = "";
	private String dbUrl    = "";
	private String dbUser   = "";
	private String dbPwd    = "";
	
	/**
	 * Costruttore della classe DbConnection per la gestione della connessione al DB
	 * @param dbDriver Driver di connessione
	 * @param dbUrl Url del DB
	 * @param dbUser User
	 * @param dbPwd Password
	 */
	public DbConnection(String dbDriver,String dbUrl,String dbUser,String dbPwd)
	{
		 this.dbDriver  = dbDriver ;
	     this.dbUrl     = dbUrl    ;
	     this.dbUser    = dbUser   ;
	     this.dbPwd     = dbPwd    ;
	}
	 /**  
	 * Verifica se la connessione al DB è attiva, rinnovandola in caso di connessione chiusa
	 * @return la connessione al DB
	 */
	public Connection getConnection()
	{
		int attempts = 0;
		while(!connIsAlive())
		{
			attempts++;
			if (attempts > this.m_maxAttemps) 
			{
				m_log4j.fatal("Unable to retrieve the database connection ....");
				this.m_conn = null;
				break;
			}
			else
			{
				renewConnection(attempts);
			}
		}
		
		return this.m_conn;
	}
	
	/**
	 * Verifica se la connessione al DB è attiva
	 * @return <code>true</code> se la connessione è attiva, <code>false</code> altrimenti
	 */
	public boolean connIsAlive()
	{
		boolean ret = true;
		Statement stmt = null;
		try
		{
			String sqlTest = "SELECT 0 FROM DUAL";
			m_log4j.trace("Try connection by query '"+sqlTest+"' ....");
			stmt = this.m_conn.createStatement();
			stmt.execute(sqlTest);
			m_log4j.trace("Connection is alive ...");
		}
		catch (Exception e)
		{
			m_log4j.trace("Connection is not alive ... try to renew ...");			
			ret=false;
		}
		finally
		{
			close(stmt);
		}
		return ret;
	}
	
	/**
	 * Stabilisce una connessione al DB
	 * @param attempts numero di tentativi di connessioe effettuati
	 */
	public void renewConnection(int attempts)
	{
		m_log4j.trace("Acquisition a new connection .... attempt "+attempts);
		try
		{
			Class.forName(dbDriver);
			this.m_conn = DriverManager.getConnection(dbUrl,dbUser, dbPwd);
		}
		catch (Exception e)
		{
			m_log4j.error(e.getMessage());
		}
	}
	
	/**
	 * Rilascia la connessione al DB
	 * @param conn la connessione al DB
	 */
	public void returnConnection(Connection conn)
	{
		this.m_conn = conn;  
	}
	
	/**
	 * Chiude la connessione al DB
	 */
	public void closeConnection()
	{
		close(this.m_conn);
	}
	
	/**
	 * Chiude gli oggetti che sono istanza di elementi di tipo SQL
	 * @param o
	 */
	private void close(Object o)
	{
		if (o != null)
		{
			try
			{
				if (o instanceof ResultSet)
					((ResultSet) o).close();
				else if (o instanceof Statement)
					((Statement) o).close();
				else if (o instanceof PreparedStatement)
					((PreparedStatement) o).close();
				else if (o instanceof CallableStatement)
					((CallableStatement) o).close();
				else if (o instanceof Connection)
					((Connection)o).close();
				else
					throw new RuntimeException("Cannot close a " + o.getClass().getName());
			}
			catch (SQLException e)
			{
				m_log4j.warn(e.getMessage());
			}
		}
	}
}
