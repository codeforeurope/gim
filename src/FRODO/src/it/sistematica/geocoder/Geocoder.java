package it.sistematica.geocoder;

import it.sistematica.geocoder.data.RawData;
import it.sistematica.geocoder.util.GeoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class Geocoder
{
	private static Logger log = Logger.getLogger(Geocoder.class);
	
	private Connection m_conn;
	private double m_within_distance;
	private double m_delta_box;
	
	private String sql = "SELECT gid, idno, name, ST_Distance(the_geom, ST_GeomFromText(?, 4326)) as dist "
		+ " FROM strt "
		+ " WHERE the_geom && ?::box3d "
		+ " AND "
		+ " ST_Distance(the_geom, ST_GeomFromText(?, 4326)) < ? "
		+ " order by dist asc";
	
	public Geocoder(Connection conn, double within_distance, double delta_box)
	{
		this.m_conn = conn;
		this.m_within_distance = within_distance;
		this.m_delta_box = delta_box;
	}
	
	public List<RawData> geocode(List<RawData> list) throws SQLException
	{
		List<RawData> output_list = new ArrayList<RawData>();
		
		RawData rd = null;
		double latitude = -999;
		double longitude = -999;
		
		long lat0 = -1;
		long lon0 = -1;
		long lat1 = -1;
		long lon1 = -1;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		try
		{
			for(int i=0;i<list.size();i++)
			{
				rd = list.get(i);
				
				latitude = rd.getLat();
				longitude = rd.getLon();
				
				sql = "SELECT gid, idno, name, ST_Distance(the_geom, ST_GeomFromText(\'POINT(" + longitude + " " + latitude + " )\', 4326)) as dist, the_geom "
					+ " FROM strt "
					+ " WHERE the_geom && setSrid(\'BOX3D(" + (longitude - m_delta_box) + " " + (latitude - m_delta_box) + ",  " + (longitude + m_delta_box) + " " + (latitude + m_delta_box) + ")\'::box3d, 4326) "
					+ " AND "
					+ " ST_Distance(the_geom, ST_GeomFromText(\'POINT(" + longitude + " " + latitude + " )\', 4326)) < " + this.m_within_distance 
					+ " order by dist asc";
				
//				pstmt.setString(1, "\'POINT(" + longitude + " " + latitude + " )\'");
//
//				pstmt.setString(2, "\'BOX3D(" + (longitude - m_delta_box) + " " + (latitude - m_delta_box) + ",  " + (longitude + m_delta_box) + " " + (latitude + m_delta_box) + ")\'");
//				
//				pstmt.setString(3, "\'POINT(" + longitude + " " + latitude + " )\'");
//				
//				pstmt.setDouble(4, this.m_within_distance);
				
				pstmt = m_conn.prepareStatement(sql);
				
				rs = pstmt.executeQuery();
				
				if(rs.next())
				{
					rd.setEdgeId(rs.getLong("idno"));
					
					PGgeometry pgGeom = (PGgeometry)rs.getObject("the_geom");
					
					if(pgGeom != null)
					{
						Geometry geom = pgGeom.getGeometry();
						
						Point f_point = geom.getFirstPoint();
						
						lat0 = (long)(latitude*1000000);
						lon0 = (long)(longitude*1000000);
						lat1 = (long)(f_point.getY()*1000000);
						lon1 = (long)(f_point.getX()*1000000);
						
						rd.setDistanceFromStart(GeoUtils.getDistance(lat0, lon0, lat1, lon1));
					}
					
					output_list.add(rd);
				}
				
				if(rs != null)
					rs.close();
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			if(pstmt != null)
				pstmt.close();
			if(rs != null)
				rs.close();
		}
		
		return output_list;
	}
}
