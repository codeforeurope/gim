/**
 * 
 */
package sistematica.aggregatore.ws.rest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import sistematica.aggregatore.ws.rest.jaxb.TTType;
import sistematica.mobiworkdb.ds.ConnectionPool;

/**
 * @author gsilvestri
 *
 */
public class DbManager
{
    private static Logger log4j = Logger.getLogger(DbManager.class);
    
    private ConnectionPool m_pool = null;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public DbManager(ConnectionPool m_pool)
    {
    	this.m_pool = m_pool;
    }
    
    public ArrayList<TTType> getTTTypeData(Date startTime, Date endTime, String source)
    {
    	ArrayList<TTType> list = null;
    	TTType type = null;
    	
    	Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		
		//QUERY CON SORGENTI LENTA!
//		String sql = "select " + 	
//					    " ts.`idno`, " +
//						" ts.`dir`, " +
//						" ts.`vehicles`, " +
//						" ts.`avg_speed`, " +
//						" ts.`stddev_speed`, " +
//						" ts.`density`, " +
//						" ts.`flow`, " +
//						" ts.`timestamp` " +
//						" from " +
//						" `gim`.`traffic_stats_history` ts " +
//						" inner join " +
//						" `gim`.`position_fcd_geo` pfg on pfg.`idno` = ts.`idno` " +
//						" inner join " +
//						" `gim`.`position_fcd`  pf on pf.`id_pos` = pfg.`id_pos` and pf.source in (" + source + ") " +
//						" where " +
//						" ts.timestamp >= '" + sdf.format(startTime) + "' " +
//						" and " +
//						" ts.timestamp <= '" + sdf.format(endTime) + "' ";
		
		String sql = "select " + 	
	    " ts.`idno`, " +
		" ts.`dir`, " +
		" ts.`vehicles`, " +
		" ts.`avg_speed`, " +
		" ts.`stddev_speed`, " +
		" ts.`density`, " +
		" ts.`flow`, " +
		" ts.`timestamp` " +
		" from " +
		" `gim`.`traffic_stats_history` ts " +
		" where " +
		" ts.timestamp >= '" + sdf.format(startTime) + "' " +
		" and " +
		" ts.timestamp <= '" + sdf.format(endTime) + "' ";

		try
		{
			log4j.info("SELECT SQL = " + sql);
			
			conn = m_pool.getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);

			while(rs.next())
			{
				if(list == null)
				{
					list = new ArrayList<TTType>();
				}
				
				type = new TTType();
				
				//Setto l'ID dell'arco
				type.setLcd1(new BigInteger(rs.getString("idno")));
				
				//Si abusa del protocollo SIMONE utilizzando il campo lcd2 per inviare la direzione
				type.setLcd2(new BigInteger(rs.getString("dir")));
				
				
				type.setNVehicles(rs.getInt("vehicles"));
				type.setSpeed(new BigDecimal(rs.getString("avg_speed")));
				type.setStdDev(new BigDecimal(rs.getString("stddev_speed")));
				
				//Si abusa del protocollo SIMONE utilizzando il campo estimatedSpeed per inviare il flusso
				type.setEstimatedSpeed(new BigDecimal(rs.getString("flow")));
//				type.setTime(new BigDecimal(rs.getString("density")));
				
				list.add(type);
			}
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		finally
		{
			close(rs);
			close(stmt);
			close(conn);
		}
		
		return list;
    }
    
    private void close(Object o)
	{
		if (o != null)
		{
			try
			{
				if (o instanceof PreparedStatement)
				{
					((PreparedStatement) o).close();
				}
				else if (o instanceof Statement)
				{
					((Statement) o).close();
				}
				else if (o instanceof ResultSet)
				{
					((ResultSet) o).close();
				}
				else if (o instanceof CallableStatement)
				{
					((CallableStatement) o).close();
				}
				else if (o instanceof Connection)
				{
					((Connection) o).close();
				}
			}
			catch (Exception e)
			{
				log4j.error("Error Closing object " + o.getClass().getName());
			}
		}
	}

}
