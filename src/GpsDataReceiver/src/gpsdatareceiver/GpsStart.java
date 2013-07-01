package gpsdatareceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class GpsStart
 */
public class GpsStart extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final String SEGMENT_MAP_SESSION_PARAM = "SEGMENT_MAP";
	
	private static Logger log = Logger.getLogger(GpsStart.class);

    public GpsStart()
    {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// start tracking sessio
		Connection conn = null;
		
		try
		{
			InputStream is = request.getInputStream();
			
			String start_msg = convertStreamToString(is);
			String node_desc = null;
			
			log.debug("START MSG ...................... " + start_msg);
			
/*
			String[] start_msg_split = start_msg.split("-");
			
			try
			{
				node_desc = start_msg_split[1];
			}
			catch(Exception ex)
			{
				log.error("NODE DESCRIPTION CAN NOT BE READ.....", ex);
			}
			
			if(node_desc != null)
			{
				conn = DataSources.getConnection();
				
				conn.setAutoCommit(false);
				
				TripDAO tripDao = DAOFactory.get(TripDAO.class);
				SegmentDAO segDao = DAOFactory.get(SegmentDAO.class);
				NodeDAO nodeDao = DAOFactory.get(NodeDAO.class);
				VehicleDAO vehicleDao = DAOFactory.get(VehicleDAO.class);
				DriverDAO driverDao = DAOFactory.get(DriverDAO.class);
				
				//---------------------------
				//DRIVER
				//---------------------------
				// Estraggo i driver
				
				long grId = 16; // SISTEMATICA
				long driverId = -1;
				
				List<Driver> list_driver = driverDao.getDrivers(conn, (int)grId);
				for(int i=0;i<list_driver.size();i++)
				{
					Driver d = list_driver.get(i);
					
					if(d.getId() == 43) // SISTEMATICA DEFAULT DRIVER
						driverId = d.getId();
				}
				
				if(driverId != -1)
				{
					//---------------------------
					//NODE
					//---------------------------
					long nodeId = nodeDao.getNodeIdByDescription(conn, node_desc);
					long vehicleId = vehicleDao.getVehicleIdFromNodeId(conn, new Long(nodeId));
					
					log.debug("NODE_ID.........................." + nodeId);
					log.debug("VEHICLE_ID......................." + vehicleId);
					
					//---------------------------
					//TRIP
					//---------------------------
					TripData trip = new TripData();
					trip.setDesc("test");
					trip.setGroupId(grId);
					trip.setNodeId(nodeId);
					trip.setVehicleId(vehicleId);
					trip.setDriverId(driverId);
					
					this.createTrip(conn, trip);
													
					//---------------------------
					//SEGMENT
					//---------------------------
					Segment seg = new Segment();
					seg.setNodeId((int)nodeId);
					seg.setVehicleId((int)vehicleId);
					seg.setStartDate(new Date(System.currentTimeMillis()));
					seg.setStatus(" ".charAt(0));
					
					segDao.createSegment(conn, seg);
					
					//---------------------------
					//REL TRIP - SEG
					//---------------------------
					this.addTripSegments(conn, trip, seg);
	
					conn.commit();
					
					log.debug("TRIP " + trip.getId() + " ADDED!");
					
					// metto in sessione il segment_id

					SegmentManager segMng = SegmentManager.getInstance();
					segMng.addSegment(node_desc, seg.getId());
										
					log.debug("ADDED " + seg.getId() + " TO " + node_desc);
				}
				else
					log.warn("DRIVER NOT FOUND!!");
			}
			
*/
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
			try
			{
				if(conn != null) conn.rollback();
			}
			catch(SQLException sqle)
			{}
		}
		finally
		{
			try
			{
				if(conn != null) conn.setAutoCommit(true);
			}
			catch(SQLException sqle)
			{}
		}
	}
	
	private String convertStreamToString(InputStream is) throws IOException
	{
		if (is != null)
		{
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try
			{
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, n);
				}
			}
			finally
			{
				is.close();
			}
			return writer.toString();
		}
		else
		{       
			return "";
		}
	}
}
