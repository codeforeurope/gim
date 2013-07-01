package sistematica.gim.sisasdiss.servlet;


/**
 * Bean rappresentante un campionamento FCD di Infomobility.
 */
public class SisasDissData
{
	private String tagDissuasore;
	private String timestamp;
	private String speed;
	private String limitExceeded;
	private String progressive;

	public SisasDissData()
	{
		super();
	}

	public SisasDissData(String tagDissuasore, String timestamp, String speed, String limitExceeded, String progressive)
	{
		super();
		this.tagDissuasore = tagDissuasore;
		this.timestamp = timestamp;
		this.speed = speed;
		this.limitExceeded = limitExceeded;
		this.progressive = progressive;
	}
	
	/**
	 * @return the tagDissuasore
	 */
	public String getTagDissuasore()
	{
		return tagDissuasore;
	}

	/**
	 * @param tagDissuasore the tagDissuasore to set
	 */
	public void setTagDissuasore(String tagDissuasore)
	{
		this.tagDissuasore = tagDissuasore;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
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
	
	/**
	 * @return the limitExceeded
	 */
	public String getLimitExceeded()
	{
		return limitExceeded;
	}

	/**
	 * @param limitExceeded the limitExceeded to set
	 */
	public void setLimitExceeded(String limitExceeded)
	{
		this.limitExceeded = limitExceeded;
	}

	/**
	 * @return the progressive
	 */
	public String getProgressive()
	{
		return progressive;
	}

	/**
	 * @param progressive the progressive to set
	 */
	public void setProgressive(String progressive)
	{
		this.progressive = progressive;
	}
	
}
