/**
 * 
 */
package sistematica.gim.sisasdiss.utils;

import org.apache.log4j.Logger;

/**
 * @author gsilvestri
 *
 */
public class Loader
{
	public static Logger m_log4j = Logger.getLogger(Loader.class);
	
	private long files = 0L;
	private long positions = 0L;
	private long timestlastrun = 0L;
	
	private static Loader instance = null;
	
	public static Loader getInstance()
	{
		if( instance == null )
			instance = new Loader();
		return instance;
	}
	
	private Loader()
	{
		this.files = 0L;
		this.positions = 0L;
		this.timestlastrun = 0L;
	}
	
	public void addFile()
	{
		m_log4j.trace("Updating the number of files (now is " + this.files + ")..." );
		this.files++;
		m_log4j.trace("Ok, added single file... Now the number of files is " + this.files);
	}
	
	public long getFiles()
	{
		return this.files;
	}
	
	public void resetFiles()
	{
		this.files = 0L;
		m_log4j.trace("Ok, files reset... The number of files now is " + this.files);
	}
	
	public void updatePositions(long positionsNumber)
	{
		m_log4j.trace("Updating the number of positions (now is " + this.positions + ")..." );
		this.positions += positionsNumber;
		m_log4j.trace("Ok, added " + positionsNumber + " positions... Now the number of positions is " + this.positions);
	}
	
	public void addSinglePosition()
	{
		m_log4j.trace("Updating the number of positions (now is " + this.positions + ")..." );
		this.positions++;
		m_log4j.trace("Ok, added single position... Now the number of positions is " + this.positions);
	}
	
	public long getPositions()
	{
		return this.positions;
	}
	
	public void resetPositions()
	{
		this.positions = 0L;
		m_log4j.trace("Ok, position reset... The number of positions now is " + this.positions);
	}
	
	public Long getTimestlastrun()
	{
		return this.timestlastrun;
	}
	
	public void updateTimestampLastRun()
	{
		this.timestlastrun = System.currentTimeMillis();
	}

}
