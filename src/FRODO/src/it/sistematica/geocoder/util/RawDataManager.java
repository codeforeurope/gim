package it.sistematica.geocoder.util;

import it.sistematica.geocoder.Main;
import it.sistematica.geocoder.data.RawData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class RawDataManager
{
	private static Logger log = Logger.getLogger(RawDataManager.class);
	
	private Connection m_conn;
	
	private String sql = "select id_pos, latitude, longitude, speed, dir, timestamp, "
		+ " satellites "
		+ " from position_fcd where id_pos > ? and id_pos < (? + " + Main.NUM_OF_ELABORATION + ") order by id_pos";
	
//	private String sql = "select id_pos, latitude, longitude, speed, dir, timestamp, "
//		+ " satellites "
//		+ " from position_fcd where id_pos > 5627519 and id_pos <=  5718798 and source = 'S' order by id_pos";  // 5718798
	
//	private String sql = "select id_pos, latitude, longitude, speed, dir, timestamp, satellites " 
//			+ "from position_fcd "
//			+ " where source = 'T' "
//			+ " and timestamp > TIMESTAMP('2011-10-01 00:00:00')";
	
	private String insert = "insert into position_fcd_geo(id_pos, idno, dist_from_start) values(?, ?, ?)";
	
	public RawDataManager(Connection conn)
	{
		this.m_conn = conn;
	}
	
	public List<RawData> readRawData(long last_id) throws SQLException
	{
		List<RawData> list = new ArrayList<RawData>();
		RawData rd = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			pstmt = m_conn.prepareStatement(sql);
			pstmt.setLong(1, last_id);
			pstmt.setLong(2, last_id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				rd = new RawData();
				
				rd.setId_pos(rs.getLong("id_pos"));
				rd.setTimestamp(rs.getTimestamp("timestamp"));
				rd.setLat((double)rs.getDouble("latitude")/1000000d);
				rd.setLon((double)rs.getDouble("longitude")/1000000d);
				rd.setHeading(rs.getInt("dir"));
				rd.setSpeed(rs.getDouble("speed"));
				rd.setSatInUse(rs.getInt("satellites"));
				
				list.add(rd);
			}
		}
		finally
		{
			if(pstmt != null)
				pstmt.close();
			if(rs != null)
				rs.close();
		}
		
		return list;
	}
	
	public void writeRawData(List<RawData> list) throws SQLException
	{
		PreparedStatement pstmt = null;
		
		try
		{
			pstmt = m_conn.prepareStatement(insert);
			
			RawData rd = null;
			for(int i=0;i<list.size();i++)
			{
				rd = list.get(i);
				
				pstmt.setLong(1, rd.getId_pos());
				pstmt.setLong(2, rd.getEdgeId());
				pstmt.setDouble(3, rd.getDistanceFromStart());
				
				pstmt.executeUpdate();
				
//				log.debug("		" + rd.getId_pos() + ", " + rd.getEdgeId() + ", " + rd.getDistanceFromStart() + ": RESULT " + result);
			}
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			if(pstmt != null)
				pstmt.close();
		}
	}
}
