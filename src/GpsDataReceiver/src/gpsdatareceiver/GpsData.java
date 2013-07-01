package gpsdatareceiver;

import java.util.Date;

public class GpsData
{
    private Date timestamp;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Float speed;
    private Integer heading;
    private Integer satellitesInView;
    private Integer satellitesUsed;
    private boolean valid;

    public GpsData()
    {
        timestamp = null;
        latitude = null;
        longitude = null;
        speed = null;
        heading = null;
        satellitesInView = null;
        satellitesUsed = null;
        valid = false;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setSpeed(Float speed)
    {
        this.speed = speed;
    }

    public void setHeading(Integer heading)
    {
        this.heading = heading;
    }

    public void setSatellitesInView(Integer satellitesInView)
    {
        this.satellitesInView = satellitesInView;
    }

    public void setSatellitesUsed(Integer satellitesUsed)
    {
        this.satellitesUsed = satellitesUsed;
    }

    public void setValid(boolean valid)
    {

        this.valid = valid;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public Float getSpeed()
    {
        return speed;
    }

    public Integer getHeading()
    {
        return heading;
    }

    public Integer getSatellitesInView()
    {
        return satellitesInView;
    }

    public Integer getSatellitesUsed()
    {
        return satellitesUsed;
    }

    public boolean isValid()
    {

        return valid;
    }

    public String toString()
    {
        String str = "";
        str += "(VALID: " + valid + "), ";
        str += "timestamp: " + timestamp.toString() + ", ";
        str += "latitude: " + latitude + ", ";
        str += "longitude: " + longitude + ", ";
        str += "speed: " + speed + ", ";
        str += "heading: " + heading + ", ";
        str += "satellitesInView: " + satellitesInView + ", ";
        str += "satellitesUsed: " + satellitesUsed;

        return str;
    }

}
