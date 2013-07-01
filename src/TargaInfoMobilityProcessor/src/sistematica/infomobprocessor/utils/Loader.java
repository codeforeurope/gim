/**
 * 
 */
package sistematica.infomobprocessor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import sistematica.infomobprocessor.settings.Keys;

/**
 * @author gsilvestri
 *
 */
public class Loader
{
	public static Logger m_log4j = Logger.getLogger(Loader.class);
	
	private ArrayList<String> filesExportedList = null;
	private String lastFileExported = null;
	
	private ArrayList<Long> listVehicles = null;
	private long positions = 0L;
	
	private static Loader instance = null;
	
	public static Loader getInstance()
	{
		if( instance == null )
			instance = new Loader();
		return instance;
	}
	
	private Loader()
	{
		this.filesExportedList = new ArrayList<String>();
		this.lastFileExported = "";
		this.listVehicles = new ArrayList<Long>();
		this.positions = 0L;
	}
	
	public void loadFileExportedFromFile()
	{
		m_log4j.info("Loading list of RawData File already exported from file " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " ... ");
		
		BufferedReader bufRdr = null;
		try
		{
			File file = new File(Keys.FTP_EXPORTER_EXPORTED_FILE_LIST);
			if(file.exists())
			{
				int index = 0;
				String line = null;
				bufRdr = new BufferedReader(new FileReader(file));
				
				while( (line = bufRdr.readLine()) != null )
				{
					index++;
					filesExportedList.add(line);
				}
				
				if(index > 0)
				{
					m_log4j.info("Ok, loaded " + index + " RawData File already exported.");
				}
				else
				{
					m_log4j.info("Ok, file " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " exists, but is empty... Nothing to do. ");
				}
			}
			else
			{
				m_log4j.info("File " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " doesn't exist... Nothing to do. ");
			}
		}
		catch (Exception e)
		{
			m_log4j.error(e,e);
		}
		finally
		{
			try
			{
				if(bufRdr != null)
				bufRdr.close();
			}
			catch (IOException e)
			{
				m_log4j.error(e,e);
			}
		}
	}
	
	public void flushFilesExportedList()
	{
		File file = null;
		FileOutputStream file_os = null;
		PrintStream output = null;
		
		m_log4j.info("Flushing the list of the file exported into the file " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + "... ");
		try
		{
			file = new File(Keys.FTP_EXPORTER_EXPORTED_FILE_LIST);
			
			file_os = new FileOutputStream(file, false);
			output = new PrintStream(file_os);
			
			for (int i = 0; i < this.filesExportedList.size(); i++)
			{
				output.println(this.filesExportedList.get(i));
			}
			
			output.flush();
			output.close();
			file_os.close();
			
			m_log4j.info("Ok, flushing terminated: writed " + this.filesExportedList.size() + " file/s. ");

		}
		catch(Exception e)
		{
			m_log4j.error(e, e);
			file = null;
		}
	}
	
	public void addFile(String fileName)
	{
		this.filesExportedList.add(fileName);
	}
	
	public boolean listContainsFile(String fileName)
	{
		return this.filesExportedList.contains(fileName);
	}
	
	public void getLastFileExportedFromFile()
	{
		m_log4j.info("Loading last raw data file already exported from file " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " ... ");
		
		BufferedReader bufRdr = null;
		try
		{
			File file = new File(Keys.FTP_EXPORTER_EXPORTED_FILE_LIST);
			
			if(file.exists())
			{
				int index = 0;
				String line = null;
				bufRdr = new BufferedReader(new FileReader(file));
				
				if( (line = bufRdr.readLine()) != null )
				{
					index++;
					this.lastFileExported = line;
				}
				
				if(index > 0)
				{
					m_log4j.info("Ok, loaded " + this.lastFileExported + " last raw data file exported.");
				}
				else
				{
					m_log4j.info("Ok, file " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " exists, but is empty... Nothing to do. ");
				}
			}
			else
			{
				m_log4j.info("File " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " doesn't exist... Nothing to do. ");
			}
		}
		catch (Exception e)
		{
			m_log4j.error(e,e);
		}
		finally
		{
			try
			{
				if(bufRdr != null)
				bufRdr.close();
			}
			catch (IOException e)
			{
				m_log4j.error(e,e);
			}
		}
	}
	
	public void flushLastFileExported()
	{
		File file = null;
		FileOutputStream file_os = null;
		PrintStream output = null;
		
		m_log4j.info("Flushing last raw data file exported into the file " + Keys.FTP_EXPORTER_EXPORTED_FILE_LIST + " ... ");
		try
		{
			file = new File(Keys.FTP_EXPORTER_EXPORTED_FILE_LIST);
			
			file_os = new FileOutputStream(file, false);
			output = new PrintStream(file_os);
			
			output.println(this.lastFileExported);
			
			output.flush();
			output.close();
			file_os.close();
			
			m_log4j.info("Ok, flushing terminated: last raw data file exported is " + this.lastFileExported );

		}
		catch(Exception e)
		{
			m_log4j.error(e, e);
			file = null;
		}
	}
	
	public void updatedLastFileExported(String fileName)
	{
		this.lastFileExported = fileName;
	}
	
	public String getLastFileExported()
	{
		return this.lastFileExported;
	}
	
	public void updateVehicleList(long vehicleId)
	{
		if(this.listVehicles.contains(vehicleId))
		{
			
			m_log4j.trace("The list of vehicles already conteins this vehicleId ("  + vehicleId + ")... Nothing to do." );
		}
		else
		{
			this.listVehicles.add(vehicleId);
			m_log4j.trace("Ok, New vehicleId("  + vehicleId + ") added in the list of vehicles.");
		}
	}
	
	public long getVehicleListSize()
	{
		return this.listVehicles.size();
	}
	
	public void clearVehicleList()
	{
		this.listVehicles.clear();
		m_log4j.trace("Ok, the list of vehicles now is clear.");
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

}
