/**
 * 
 */
package sistematica.infomobprocessor.data;

import sistematica.infomobprocessor.settings.Keys;


/**
 * @author gsilvestri
 *
 */
public class InfoMobRawData
{
	String vehicleId;
	String timestamp;
	String timestampGps;
	String gpsStaus;
	String speed;
	String dir;
	String satellites;
	String odometer;
	String latitude;
	String longitude;
	String key;
	String event;
	String type;
	
	public InfoMobRawData()
	{
		super();
	}

	public InfoMobRawData(String vehicleId, String timestamp, String timestampGps, String gpsStaus, String speed, String dir, String satellites, String odometer, String latitude,
			String longitude, String key, String event, String type)
	{
		super();
		this.vehicleId = vehicleId;
		this.timestamp = timestamp;
		this.timestampGps = timestampGps;
		this.gpsStaus = gpsStaus;
		this.speed = speed;
		this.dir = dir;
		this.satellites = satellites;
		this.odometer = odometer;
		
		if(Keys.RAWDATA_PROCESSOR_LATLON_LONG_ENABLE)
		{
			longitude = longitude.replace(".", "");
			latitude = latitude.replace(".", "");
		}
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.key = key;
		this.event = event;
		this.type = type;
	}

	public String getVehicleId()
	{
		return vehicleId;
	}

	public void setVehicleId(String vehicleId)
	{
		this.vehicleId = vehicleId;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getTimestampGps()
	{
		return timestampGps;
	}

	public void setTimestampGps(String timestampGps)
	{
		this.timestampGps = timestampGps;
	}

	public String getGpsStaus()
	{
		return gpsStaus;
	}

	public void setGpsStaus(String gpsStaus)
	{
		this.gpsStaus = gpsStaus;
	}

	public String getSpeed()
	{
		return speed;
	}

	public void setSpeed(String speed)
	{
		this.speed = speed;
	}

	public String getDir()
	{
		return dir;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
	}

	public String getSatellites()
	{
		return satellites;
	}

	public void setSatellites(String satellites)
	{
		this.satellites = satellites;
	}

	public String getOdometer()
	{
		return odometer;
	}

	public void setOdometer(String odometer)
	{
		this.odometer = odometer;
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		if(Keys.RAWDATA_PROCESSOR_LATLON_LONG_ENABLE)
		{
			latitude = latitude.replace(".", "");
		}

		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		if(Keys.RAWDATA_PROCESSOR_LATLON_LONG_ENABLE)
		{
			longitude = longitude.replace(".", "");
		}
		
		this.longitude = longitude;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getEvent()
	{
		return event;
	}

	public void setEvent(String event)
	{
		this.event = event;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
	
}
