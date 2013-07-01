/**
 * 
 */
package sistematica.infomobprocessor.utils;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * @author gsilvestri
 *
 */
public class FtpConnection
{
	private Logger m_log4j = Logger.getLogger(FtpConnection.class);
	
	private FTPClient ftpClient = null;
	
	private String  serverFtpName = "";
	private Integer serverFtpPort = -1;
	private String  userFtp = "";
	private String  passwordFtp = "";
	
	public FtpConnection(String serverFtpName, int serverFtpPort, String userFtp, String passwordFtp)
	{
		this.serverFtpName = serverFtpName;
		this.serverFtpPort = serverFtpPort;
		this.userFtp = userFtp;      
		this.passwordFtp = passwordFtp;   
	}
	
	public FTPClient getFtpConnection()
	{
		int reply;
		
		try
		{
			this.ftpClient = new FTPClient();
			this.ftpClient.setDataTimeout(5000);
			
			m_log4j.debug("Connecting to FTP server... ");
			
			if(serverFtpPort != null)
				ftpClient.connect(serverFtpName, serverFtpPort);
			else
				ftpClient.connect(serverFtpName);
			
			m_log4j.debug(ftpClient.getReplyString());
			reply = ftpClient.getReplyCode();
			
			if (!FTPReply.isPositiveCompletion(reply))
			{
				ftpClient.disconnect();
				m_log4j.error("FTP server refused connection.");
				this.ftpClient = null;
			}
			else
			{
				m_log4j.debug("Ok, connected to " + serverFtpName + ".");
				
				m_log4j.debug("Logging user '" + userFtp +"' to the FTP server... ");
				if(ftpClient.login(userFtp, passwordFtp))
				{
					m_log4j.info("Ok, user '" + userFtp + "' is logged.");
				}
				else
				{
					ftpClient.disconnect();
					m_log4j.error("Error logging user '" + userFtp + "' to " + serverFtpName + ". ");
					this.ftpClient = null;
				}
			}
		}
		catch (Exception e)
		{
			m_log4j.error(e,e);
			
			if (ftpClient != null && ftpClient.isConnected())
			{
				try
				{
					ftpClient.logout();
					ftpClient.disconnect();
					m_log4j.info("Disconnected to " + serverFtpName + ".");
				}
				catch (IOException er)
				{
					m_log4j.error(er,er);
					this.ftpClient = null;
				}
			}
			
			this.ftpClient = null;
		}
		
		return this.ftpClient;
	}
	
	public void returnFtpConnection(FTPClient ftpClient)
	{
		this.ftpClient = ftpClient;  
	}
	
	public void closeFtpConnection()
	{
		if (ftpClient != null && ftpClient.isConnected())
		{
			try
			{
				ftpClient.logout();
				ftpClient.disconnect();
				m_log4j.debug("Disconnected to " + serverFtpName + ".");
			}
			catch (IOException e)
			{
				m_log4j.error(e,e);
			}
		}
	}	
}
