/**
 * 
 */
package sistematica.sintelimporter.data;

/**
 * @author gsilvestri
 *
 */
public class MoviTraff
{
	public String ip = "";
	public int port = 0;
	public String tag = "";
	
	public MoviTraff(String ip, int port, String tag)
	{
		super();
		this.ip = ip;
		this.port = port;
		this.tag = tag;
	}

	/**
	 * @return the ip
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
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
}
