/**
 * 
 */
package sistematica.gim.sisasdiss.servlet;

/**
 * @author gsilvestri
 *
 */
public class SisasDiss
{
	String id = "";
	String tag = "";
	String desc = "";
	String lat = "";
	String lon = "";
	String heading = "";
	String maxIdAggregato = "";
	
	public SisasDiss(){}

	public SisasDiss(String id, String tag, String desc, String lat, String lon, String heading, String maxIdAggregato)
	{
		super();
		this.id = id;
		this.tag = tag;
		this.desc = desc;
		this.lat = lat;
		this.lon = lon;
		this.heading = heading;
		this.maxIdAggregato = maxIdAggregato;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the tag
	 */
	public String getTag()
	{
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag)
	{
		this.tag = tag;
	}

	/**
	 * @return the desc
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	/**
	 * @return the lat
	 */
	public String getLat()
	{
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(String lat)
	{
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public String getLon()
	{
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(String lon)
	{
		this.lon = lon;
	}

	/**
	 * @return the heading
	 */
	public String getHeading()
	{
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading)
	{
		this.heading = heading;
	}
	
	public double getDoubleLatitude()
	{
		return Double.parseDouble(this.lat) / 1E6;
	}
	
	public double getDoubleLaongitude()
	{
		return Double.parseDouble(this.lat) / 1E6;
	}

	/**
	 * @return the maxIdAggregato
	 */
	public String getMaxIdAggregato()
	{
		return maxIdAggregato;
	}

	/**
	 * @param maxIdAggregato the maxIdAggregato to set
	 */
	public void setMaxIdAggregato(String maxIdAggregato)
	{
		this.maxIdAggregato = maxIdAggregato;
	}
	
	
	
}
