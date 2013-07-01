/**
 * 
 */
package sistematica.infomobprocessor.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import sistematica.infomobprocessor.data.InfoMobRawData;
import sistematica.infomobprocessor.settings.Keys;
import sistematica.infomobprocessor.utils.Loader;
import visualtrack.logger.client.VTLoggerText;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

/**
 * @author gsilvestri
 *
 */
public class ParseFileInfoMob
{
	
	public static Logger m_log4j = Logger.getLogger(ParseFileInfoMob.class);
	
	public static ArrayList<InfoMobRawData> parseFile(File file) throws IOException
	{		
		ArrayList<InfoMobRawData> listRawData = null;
		
		String line = null;
		String[] lineArr = null;
		BufferedReader bufRdr = null;
		int index = 0;
		
		bufRdr = new BufferedReader(new FileReader(file));
			
		while( (line = bufRdr.readLine()) != null )
		{				
			lineArr = line.split("\t");
			line = line.replaceAll("\t", ";");
							
			m_log4j.debug("Find " + lineArr.length + " args for line n." + (index+1));
			m_log4j.debug(line);

			if(lineArr.length < 12 || lineArr.length > 13)
			{
				m_log4j.error("Illegal data number (" + lineArr.length + " args).Skipping the line:");
				m_log4j.error(line);
			}
			else
			{
				index++;
				if(index == 1)
				{
					listRawData = new ArrayList<InfoMobRawData>();
				}
				
				if(lineArr.length == 13)
				{
					listRawData.add(new InfoMobRawData(lineArr[0], lineArr[1], lineArr[2], lineArr[3], lineArr[4], lineArr[5], lineArr[6], lineArr[7], lineArr[8], lineArr[9], lineArr[10], lineArr[11], lineArr[12]));
				}
				else if (lineArr.length == 12)
				{
					listRawData.add(new InfoMobRawData(lineArr[0], lineArr[1], lineArr[2], lineArr[3], lineArr[4], lineArr[5], lineArr[6], lineArr[7], lineArr[8], lineArr[9], lineArr[10], lineArr[11], null));
				}
			}
		}
		m_log4j.debug("Read " + index + " lines.");
		
		if(bufRdr != null)
			bufRdr.close();
		
		return listRawData;
		
	}
	
	public static int writeRawData(ArrayList<InfoMobRawData> listRawData, Connection conn) throws FileNotFoundException, IOException, SQLException
	{
		
		VTLoggerText vttlog = null;
		try
		{
			if (Keys.SENDMGR_ENABLE)
			{
				VTLoggerThreadPool.init(5, 30000);
				vttlog = new VTLoggerText(Keys.VTLOGGER_TEXT_URL, true);
			}
		}
		catch (Exception e)
		{
			m_log4j.info(e,e);
		}
		
		PreparedStatement ps = null;
		int res = 0;
		InfoMobRawData data = null;
		
//		String query = "insert into position_fcd (id_vehicle,speed,timestamp,latitude,longitude,dir,id_panelsession,quality,deltapos,deltatime) values (?,?,STR_TO_DATE(?,'%d-%c-%Y %H:%i:%s'),?,?,?,?,?,?,?)";
		String query = " insert into " +
					   Keys.RAWDATA_PROCESSOR_TABLE_NAME +
					   " (id_vehicle, timestamp, timestamp_gps, gps_status, dir, speed, satellites, odometer, latitude, longitude, `key`, event, vehicle_type, source) " +
					   " values " +
					   " (?, STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		Loader loader = null;
		
		try
		{
			ps = conn.prepareStatement(query);
			
			if(Keys.SENDMGR_ENABLE)
		    {
		    	loader = Loader.getInstance();
		    }
			
			for(int i = 0; i < listRawData.size(); i++ )
			{
				data = listRawData.get(i);
				
				ps.setString(1,  data.getVehicleId());
				ps.setString(2,  data.getTimestamp());
				
//				if(!data.getTimestampGps().equals("-1"))
				if(isDate(data.getTimestampGps()))
					ps.setString(3,  data.getTimestampGps());
				else
					ps.setNull(3, java.sql.Types.VARCHAR);
				
				ps.setString(4,  data.getGpsStaus());
				ps.setString(5,  data.getDir());
				ps.setString(6,  data.getSpeed());
				ps.setString(7,  data.getSatellites());
				ps.setString(8,  data.getOdometer());
				ps.setString(9,  data.getLatitude());
			    ps.setString(10, data.getLongitude());
				ps.setString(11, data.getKey());
				ps.setString(12, data.getEvent());
			    
				if(data.getType() != null)
					ps.setString(13, data.getType());
				else
					ps.setNull(13, java.sql.Types.VARCHAR);
				
				ps.setString(14, Keys.RAWDATA_PROCESSOR_SOURCE_NAME);
			    
				m_log4j.trace(ps.toString().substring(ps.toString().indexOf(":") + 1 ));
				
			    ps.addBatch();
			    
			    if(Keys.SENDMGR_ENABLE)
			    {
			    	loader.addSinglePosition();
			    	loader.updateVehicleList(Long.parseLong(data.getVehicleId()));
			    }
			}

			int resArr[] = ps.executeBatch();
			
			for(int i=0; i<resArr.length; i++)
			{
				if(resArr[i] == Statement.SUCCESS_NO_INFO)
				{
					res++;
				}
				else if(resArr[i] >= 0)
				{
					res++;
				}
				else if(resArr[i] == Statement.EXECUTE_FAILED)
				{}
			}
			
			if(ps != null)
				ps.close();
		}
		catch (Exception e)
		{
			m_log4j.error(e,e);
			
			if(Keys.SENDMGR_ENABLE)
				vttlog.error(Keys.SENDMGR_TAG_NODE, "Error inserting positions into the DB: " + e.getStackTrace(), System.currentTimeMillis());
		}
		
		return res;
	}
	
	 public static boolean isDate(String date) 
	 {
		 String time = "(\\s(([01]?\\d)|(2[0123]))[:](([012345]\\d)|(60))"
	       + "[:](([012345]\\d)|(60)))?";
	 
	    String day = "(([12]\\d)|(3[01])|(0?[1-9]))"; // 01 up to 31
	    String month = "((1[012])|(0\\d))"; // 01 up to 12
	    String year = "\\d{4}";
	 
	    // define here all date format
	    ArrayList<Pattern> patterns = new ArrayList<Pattern>();
	    patterns.add(Pattern.compile(day + "[-.]" + month + "[-.]" + year + time));
	    patterns.add(Pattern.compile(year + "-" + month + "-" + day + time));
	    // here you can add more date formats if you want
	 
	    // check dates
	    for (Pattern p : patterns)
	      if (p.matcher(date).matches())
	        return true;
	 
	    return false;
	 
	  }

}
