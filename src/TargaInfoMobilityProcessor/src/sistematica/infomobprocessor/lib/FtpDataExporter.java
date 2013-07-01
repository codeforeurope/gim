/**
 * 
 */
package sistematica.infomobprocessor.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import sistematica.infomobprocessor.settings.Keys;
import sistematica.infomobprocessor.utils.FtpConnection;
import sistematica.infomobprocessor.utils.Loader;
import visualtrack.logger.client.VTLoggerText;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

/**
 * @author gsilvestri
 *
 */
public class FtpDataExporter extends Thread
{
	public static Logger m_log4j = Logger.getLogger(FtpDataExporter.class);
	
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
		
		FtpConnection ftpConn = null;
		FTPClient ftpClient = null;
		
		String ftpFile = null;
		
		String[] listFiles = null;
		ArrayList<String> listFilesAL = null;
		int index = 0;
		
		String serverFtpName = Keys.FTP_EXPORTER_SERVER_NAME;
		int serverFtpPort = Keys.FTP_EXPORTER_SERVER_PORT;
		String userFtp = Keys.FTP_EXPORTER_SERVER_USER;   
		String passwordFtp = Keys.FTP_EXPORTER_SERVER_PWD;
		
		String archieveDir = Keys.FTP_EXPORTER_ARCHIVE_DIR;
		
		String newFileName = null;
		FileOutputStream fos = null;
		File fileDownloaded = null;
		
		Loader loader = null;
		
		if(! Keys.FTP_EXPORTER_DEL_SERVER_FILE_ENABLE)
		{
			loader = Loader.getInstance();
		}
		
		int count = 0;
		
		while(!stop)
		{
			try
			{
				if(System.currentTimeMillis() - start > (Keys.FTP_EXPORTER_POLLING_TIME_MIN * 60000) )
				{
					m_log4j.info("Exporting raw data file from FTP " + Keys.FTP_EXPORTER_SERVER_USER + "@" + Keys.FTP_EXPORTER_SERVER_NAME + "...");
					
					ftpConn = new FtpConnection(serverFtpName, serverFtpPort, userFtp, passwordFtp);
					ftpClient = ftpConn.getFtpConnection();
					
					if (ftpClient != null)
					{
						ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
						
						m_log4j.trace("FTP Connection is " + (ftpClient.isConnected() ? "OK" : "CLOSED"));
						
						listFiles = ftpClient.listNames();
						Arrays.sort(listFiles);
						listFilesAL = getArrayListFromArray(listFiles);
						
						if (loader == null || loader.getLastFileExported() == null || loader.getLastFileExported().length() == 0)
							index = 0;
						else
							index = listFilesAL.indexOf(loader.getLastFileExported()) + 1;
						
						count = 0;
						
						if (listFilesAL.size() > 0)
						{
							m_log4j.info("In the current dir there are " + listFilesAL.size() + " files ...");
							m_log4j.info("Starting the download (max file to download = " + Keys.FTP_EXPORTER_MAX_FILE_DOWNLOAD + ") ...");
						}
						
						for (int i = index; i < listFilesAL.size(); i++)
						{
							ftpFile = listFilesAL.get(i);
							count++;

							m_log4j.trace("FTPFile: " + ftpFile);

							newFileName = archieveDir + File.separatorChar + ftpFile;
							fileDownloaded = new File(newFileName);
							fos = new FileOutputStream(fileDownloaded);

							ftpClient.retrieveFile(ftpFile, fos);

//							m_log4j.debug("ftpClient.completePendingCommand() = " + ftpClient.completePendingCommand());

							if (fos != null)
							{
								fos.flush();
								fos.close();
							}

							if (fileDownloaded.exists())
							{
								m_log4j.debug("Ok, " + fileDownloaded + " correctly downloaded from the FTP Server..");

								if (Keys.FTP_EXPORTER_DEL_SERVER_FILE_ENABLE)
								{
									if (ftpClient.deleteFile(ftpFile))
									{
										m_log4j.debug("Ok, " + ftpFile + " correctly deleted from the FTP Server.");
									}
									else
									{
										m_log4j.error("Error deleting file " + ftpFile + " from the FTP Server.");
									}
								}
								else
								{
									loader.updatedLastFileExported(fileDownloaded.getName());
//									loader.addFile(fileDownloaded.getName());
								}
							}
							else
							{
								m_log4j.error("Error downloading file " + ftpFile + " from the FTP Server.");
							}

							if (count == Keys.FTP_EXPORTER_MAX_FILE_DOWNLOAD)
							{
//								stopFtpDataExporter();
								break;
							}

							Thread.sleep(25);
						}
						m_log4j.info("Ok, " + count + " files correctly downloaded.");
						if(fileDownloaded != null)
							m_log4j.info("Last file downloaded is " + fileDownloaded.getName() + ". ");
						
						if(Keys.SENDMGR_ENABLE)
							vttlog.info(Keys.SENDMGR_TAG_NODE, "Ok, " + count + " files correctly downloaded from the FTP Server.", System.currentTimeMillis());
						
						ftpConn.returnFtpConnection(ftpClient);
						ftpConn.closeFtpConnection();
						m_log4j.trace("FTP Connection is " + (ftpClient.isConnected() ? "OK" : "CLOSED"));
					}
					else
					{
						m_log4j.warn("The FTP Client is null... problem on FTP connection... retrying later...");
						
						if(Keys.SENDMGR_ENABLE)
						{
							vttlog.warning(Keys.SENDMGR_TAG_NODE, "The FTP Client is null... problem on FTP connection... retrying later...", System.currentTimeMillis());
						}
						
						Thread.sleep(25);
					}
					
					if(!stop)
						m_log4j.info("FtpDataExporter executed..is sleeping now ("+Keys.FTP_EXPORTER_POLLING_TIME_MIN+" minutes)...");

					
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
					vttlog.error(Keys.SENDMGR_TAG_NODE, "Exception in FtpDataExporter Thread: " + e.getStackTrace(), System.currentTimeMillis());
				
				start = System.currentTimeMillis();
				m_log4j.warn("Warning, downloaded only " + count + " files.");
				
				if(fileDownloaded != null)
					m_log4j.warn("Last file downloaded is " + fileDownloaded.getName() + ". ");
				
				if (!Keys.FTP_EXPORTER_DEL_SERVER_FILE_ENABLE)
				{
					if(fileDownloaded != null)
						loader.updatedLastFileExported(fileDownloaded.getName());
				}
				else
				{
					//TODO
					//procedura per cancellare il file in caso in cui l'ftp si blocca
					//per non produrre posizioni duplicate nel DB.
				}
				
			}
			finally
			{
				if (ftpConn != null && ftpClient != null && ftpClient.isConnected())
				{
					ftpConn.returnFtpConnection(ftpClient);
					ftpConn.closeFtpConnection();
				}
			}
		}
		
		m_log4j.info("FtpDataExporter terminated.. bye!");
		
	}
	
	private ArrayList<String> getArrayListFromArray(String[] listFiles)
	{
		ArrayList<String> listFilesAL = new ArrayList<String>();
		
		for(int i = 0; i < listFiles.length; i++)
			listFilesAL.add(listFiles[i]);
		
		return listFilesAL;
	}

	public void stopFtpDataExporter()
	{
		stop = true;
		m_log4j.info("FtpDataExporter is stopping now..");
	}
	
}
