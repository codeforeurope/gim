package sistematica.gim.infomobility.servlet;

import sistematica.gim.infomobility.conf.WebSettings;

/**
 * Bean rappresentante un campionamento FCD di Infomobility.
 */
public class InfoMobRawData
{
	private String vehicleId;
	private String timestamp;
	private String timestampGps;
	private String gpsStaus;
	private String speed;
	private String dir;
	private String satellites;
	private String odometer;
	private String latitude;
	private String longitude;
	private String key;
	private String event;
	private String type;

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

		if (WebSettings.FCD_LATLON_LONG_ENABLE)
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

	public double getDoubleSpeed()
	{
		return Double.parseDouble(speed);
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

	public double getDoubleLatitude()
	{
		return Double.parseDouble(latitude) / 1E6;
	}

	public void setLatitude(String latitude)
	{
		if (WebSettings.FCD_LATLON_LONG_ENABLE)
		{
			latitude = latitude.replace(".", "");
		}

		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public double getDoubleLongitude()
	{
		return Double.parseDouble(longitude) / 1E6;
	}

	public void setLongitude(String longitude)
	{
		if (WebSettings.FCD_LATLON_LONG_ENABLE)
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
