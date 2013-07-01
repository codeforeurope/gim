/**
 * 
 */
package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sistematica.infomobprocessor.settings.Keys;
import sistematica.infomobprocessor.utils.DbConnection;


/**
 * @author gsilvestri
 *
 */
public class TestConnectionMySql
{
	public static Logger m_log4j = Logger.getLogger(TestConnectionMySql.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		PropertyConfigurator.configure("../cfg/application.properties");
		
		m_log4j.info("=====================");
		m_log4j.info(" TestConnectionMySql ");
		m_log4j.info("=====================");
		
//		infomob.processor.db.driverName=com.mysql.jdbc.Driver
//		infomob.processor.db.jdbcURL=jdbc:mysql://172.17.8.12:3306/gim
//		infomob.processor.db.user=gim
//		infomob.processor.db.password=gim
		
		Keys.DB_DRIVER = "com.mysql.jdbc.Driver";
		Keys.DB_URL = "jdbc:mysql://172.17.8.12:3306/gim";
		Keys.DB_USER = "gim";
		String DB_PWD = "gim";
		
		DbConnection dbConn = new DbConnection(Keys.DB_DRIVER, Keys.DB_URL, Keys.DB_USER, DB_PWD);
		
		Connection conn = dbConn.getConnection();
		
		Statement st = null;
		ResultSet rs = null;
		try
		{
			m_log4j.info("Connection is " + (!conn.isClosed()?"alive":"not alive") );
			
			st = conn.createStatement();
			rs = st.executeQuery("SELECT tail,head,dirx FROM strt where idno in (55508992,55508993,55508994,55508995)");
			
			while(rs.next())
			{
				m_log4j.info("tail:" + rs.getString("tail") + " # head:" + rs.getString("head") + " # dirx:" + rs.getString("dirx") );
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			dbConn.returnConnection(conn);
			dbConn.closeConnection();
			
			try
			{
				m_log4j.info("Connection is " + (!conn.isClosed()?"alive":"not alive") );
				
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

	}

}
