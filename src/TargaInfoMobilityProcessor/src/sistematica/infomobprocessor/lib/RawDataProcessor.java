/**
 * 
 */
package sistematica.infomobprocessor.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import sistematica.infomobprocessor.data.InfoMobRawData;
import sistematica.infomobprocessor.settings.Keys;
import sistematica.infomobprocessor.utils.DbConnection;
import sistematica.infomobprocessor.utils.FileUtils;
import visualtrack.logger.client.VTLoggerText;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

/**
 * @author gsilvestri
 *
 */
public class RawDataProcessor extends Thread
{
	public static Logger m_log4j = Logger.getLogger(RawDataProcessor.class);
	
	private String archieveDir = Keys.FTP_EXPORTER_ARCHIVE_DIR;
	private String processedDir = Keys.RAWDATA_PROCESSOR_OK_DIR;
	private String errorProcessedDir = Keys.RAWDATA_PROCESSOR_ERROR_DIR;
	private String emptyProcessedDir = Keys.RAWDATA_PROCESSOR_EMPTY_DIR;

	private SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy-MM-dd");
	
	private int maxFileToProcess = Keys.RAWDATA_PROCESSOR_MAX_FILE_PROCESS;
	
	private boolean stop = false;
	
	public void run()
	{
		Long start = 0L;
		
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
		
		while(!stop)
		{
			try
			{
				if(System.currentTimeMillis() - start > (Keys.RAWDATA_PROCESSOR_POLLING_TIME_MIN * 60000) )
				{
					m_log4j.info("Starting RawDataProcessor...");
					
					processDirectory(archieveDir);
					
					m_log4j.info("RawDataProcessor executed..is sleeping now ("+Keys.RAWDATA_PROCESSOR_POLLING_TIME_MIN+" minutes)...");
					
					start = System.currentTimeMillis();
				}
				else
				{
					Thread.sleep(100);
				}
				
			}
			catch (Exception e)
			{
				m_log4j.error(e,e);
				
				if(Keys.SENDMGR_ENABLE)
					vttlog.error(Keys.SENDMGR_TAG_NODE, "Exception in RawDataProcessor Thread: " + e.getStackTrace(), System.currentTimeMillis());
			}
		}	
		
		m_log4j.info("RawDataProcessor terminated.. bye!");
	}
	
	public void processDirectory(String archieveDir) throws Exception
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
		
		ArrayList<InfoMobRawData> listRawData = null;
		int count = 0;
		
		int countProc = 0;
		int countEmpty = 0;
		int countErr = 0;
		
		File fileToMove = null;
		String newNameErr = null;
		
		FileOutputStream fos = null;		
		String errore = null;
		int length = -1;
		Writer stackTrace = null;
		PrintWriter pw = null;
		
		File baseDir = new File(archieveDir);
//		File processedDir = new File(archieveDir + File.separatorChar + this.processedDir);
//		File errorProcessedDir = new File(archieveDir + File.separatorChar + this.errorProcessedDir);
//		File emptyProcessedDir = new File(archieveDir + File.separatorChar + this.emptyProcessedDir);

		
		m_log4j.info("Looking " + baseDir.getName() + " directory...");

		m_log4j.debug("Retrieving list of RawData files...");
		String[] raw_file_list = baseDir.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return ((name.endsWith(".txt") || name.endsWith(".TXT")) && name.startsWith("TrafficData")) ;
			}
		});
		Arrays.sort(raw_file_list);
		m_log4j.debug("Ok, list correctly retrieved ");

		// limit to the number of files to process per time
		int file_to_process_count = raw_file_list.length;

		if (maxFileToProcess > 0 && file_to_process_count > maxFileToProcess)
			file_to_process_count = maxFileToProcess;
		
		File raw_file = null;
		
		DbConnection dbConn = null;
		Connection conn = null;
		
		try
		{

			dbConn = new DbConnection(Keys.DB_DRIVER, Keys.DB_URL, Keys.DB_USER, Keys.DB_PWD);
			if (file_to_process_count > 0)
			{
				conn = dbConn.getConnection();
			}
			
			for (int i = 0; i < file_to_process_count; i++)
			{
				raw_file = new File(baseDir, raw_file_list[i]);
				m_log4j.debug("Processing file " + raw_file.getName() + "...");
				try
				{
					//PARSA IL FILE
					listRawData = ParseFileInfoMob.parseFile(raw_file);
					
					if(listRawData != null)
					{
						countProc++;
						
						m_log4j.debug("File " + raw_file.getName() + " has " + listRawData.size() + " records... " );
	
						//SCRIVE I RECORD DEL FILE SUL DB
						count = ParseFileInfoMob.writeRawData(listRawData, conn);
						
						if(count == listRawData.size())
						{
							m_log4j.debug("Ok, inserted " + count + " record...( list size: "+listRawData.size()+")");
						}
						else
						{
							m_log4j.warn("Warning, only " + count + " record inserted ...( list size: "+listRawData.size()+")");
						}

						if (!Keys.RAWDATA_PROCESSOR_DEL_OK_FILE)
						{
							//PATCH PER DIRECTORY GIORNALIERE
							String archiveDailyDir =  archieveDir + File.separatorChar + this.processedDir + File.separatorChar + getTodayDate();
							FileUtils.makeDir(archiveDailyDir);
							
							fileToMove = new File(archiveDailyDir, raw_file.getName());
							if (fileToMove.exists())
							{
								fileToMove = new File(archiveDailyDir, getTimestamp(new Date()) + "_" + raw_file.getName());
							}
							if (!raw_file.renameTo(fileToMove))
							{
								m_log4j.error("An error occurred moving the file");
							}
							else
							{
								m_log4j.debug("File " + raw_file.getName() + " correctly processed and moved into " + archiveDailyDir);
							}
						}
						else
						{
							if(!raw_file.delete())
							{
								m_log4j.error("An error occurred deleting the file " + raw_file.getName());
							}
							else
							{
								m_log4j.debug("File " + raw_file.getName() + " correctly processed and deleted.");
							}
						}
					}
					else
					{
						countEmpty++;
						
						if (!Keys.RAWDATA_PROCESSOR_DEL_EMPTY_FILE)
						{
							m_log4j.debug("File " + raw_file.getName() + " has no records... will be moved into empty-files dir...");
							
							//PATCH PER DIRECTORY GIORNALIERE
							String archiveEmptyDailyDir =  archieveDir + File.separatorChar + this.emptyProcessedDir + File.separatorChar + getTodayDate();
							FileUtils.makeDir(archiveEmptyDailyDir);
							
							fileToMove = new File(archiveEmptyDailyDir, raw_file.getName());
							if (fileToMove.exists())
							{
								fileToMove = new File(archiveEmptyDailyDir, getTimestamp(new Date()) + "_" + raw_file.getName());
							}
							if (!raw_file.renameTo(fileToMove))
							{
								m_log4j.error("An error occurred moving the file");
							}
							else
							{
								m_log4j.debug("File " + raw_file.getName() + " is empty and moved into " + archiveEmptyDailyDir);
							}
						}
						else
						{
							m_log4j.debug("File " + raw_file.getName() + " has no records... will be removed...");
							if(!raw_file.delete())
							{
								m_log4j.error("An error occurred deleting the file " + raw_file.getName());
							}
							else
							{
								m_log4j.debug("File " + raw_file.getName() + " is empty and deleted.");
							}
						}
					}
				}
				catch(Exception e) 
				{
					m_log4j.error(e,e);
					
					if(Keys.SENDMGR_ENABLE)
						vttlog.error(Keys.SENDMGR_TAG_NODE, "Exception in RawDataProcessor Thread: " + e.getStackTrace(), System.currentTimeMillis());
					
					countErr++;
					
					if (!Keys.RAWDATA_PROCESSOR_DEL_ERROR_FILE)
					{
						try
						{
							m_log4j.error("The file " + raw_file.getName() + " is BAD (see the stacktrace at the end of the file)...will be moved into error dir..");

							//Scrive lo Stack Trace alla fine del file:
							fos = new FileOutputStream(raw_file, true);
							errore = new String("" + e);
							length = errore.length();
							stackTrace = new StringWriter();

							pw = new PrintWriter(stackTrace);
							e.printStackTrace(pw);

							fos.write("\n".getBytes());

							for (int ii = 0; ii < length - 1; ii++)
								fos.write("*".getBytes());

							fos.write(("\n" + (stackTrace.toString())).getBytes());
							fos.flush();
							fos.close();

							pw.close();
							stackTrace.close();

							newNameErr = raw_file.getName() + "_BAD_" + getTimestamp(new Date());
							
							//PATCH PER DIRECTORY GIORNALIERE
							String archiveErrDailyDir =  archieveDir + File.separatorChar + this.errorProcessedDir + File.separatorChar + getTodayDate();
							FileUtils.makeDir(archiveErrDailyDir);
							
							fileToMove = new File(archiveErrDailyDir, newNameErr);
							if (fileToMove.exists())
							{
								fileToMove = new File(archiveErrDailyDir, getTimestamp(new Date()) + "_" + newNameErr);
							}

							if (!raw_file.renameTo(fileToMove))
							{
								m_log4j.error("An error occurred moving the file.");
							}
							else
							{
								m_log4j.debug("File " + newNameErr + " moved into " + archiveErrDailyDir);
							}
						}
						catch (Exception ex)
						{
							m_log4j.error(ex, ex);
						}
					}
					else
					{
						m_log4j.error("The file " + raw_file.getName() + " is BAD...will be removed...");
						if(!raw_file.delete())
						{
							m_log4j.error("An error occurred deleting the file " + raw_file.getName());
						}
						else
						{
							m_log4j.debug("File " + raw_file.getName() + " deleted.");
						}
					}
				}
			}
			
			if (file_to_process_count > 0)
			{
				m_log4j.info("-----------------------");
				m_log4j.info("Ok, " + file_to_process_count + " files processed:");
				m_log4j.info("  FILE CORRECTLY PROCESSED = " + countProc);
				m_log4j.info("  FILE EMPTY               = " + countEmpty);
				m_log4j.info("  FILE WITH ERRORS         = " + countErr);
				m_log4j.info("-----------------------");
				
				if(Keys.SENDMGR_ENABLE)
					vttlog.info(Keys.SENDMGR_TAG_NODE, "Ok, " + file_to_process_count + " files processed: FILE CORRECTLY PROCESSED = " + countProc +"|FILE EMPTY = " + countEmpty + "|FILE WITH ERRORS = " + countErr, System.currentTimeMillis());
			}
		}
		catch(Exception e)
		{
			m_log4j.error(e,e);
			
			if(Keys.SENDMGR_ENABLE)
				vttlog.error(Keys.SENDMGR_TAG_NODE, "Exception in RawDataProcessor Thread: " + e.getStackTrace(), System.currentTimeMillis());
		}
		finally
		{
			//CLOSE CONN
			dbConn.returnConnection(conn);
			dbConn.closeConnection();
		}

		m_log4j.info("Ok, " + baseDir.getName() + " directory processed.");
	}

	public String getTimestamp(Date d) throws Exception
	{
		return (new SimpleDateFormat(Keys.RAWDATA_PROCESSOR_DATEFORMAT_BAD_FILE).format(d));
	}
	
	private String getTodayDate()
	{
		String res = "";
		try
		{
			res = sdfToday.format(new Date(System.currentTimeMillis()));
		}
		catch(Exception e)
		{
			m_log4j.error("Error while creating today date as a String.", e);
		}
		
		return res;	
	}

	public void stopRawDataProcessor()
	{
		stop = true;
		m_log4j.info("RawDataProcessor is stopping now..");
	}
	
}
