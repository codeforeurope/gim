package sistematica.sintelimporter.lib;

import java.io.IOException;

import org.apache.log4j.Logger;

import sistematica.sintelimporter.data.Message;
import sistematica.sintelimporter.settings.Keys;

public class ConnectionManager extends Thread
{
	private Logger log4j = Logger.getLogger(ConnectionManager.class);
	
	public SocketChannel socketChannel = null;
	boolean socketInitialiazed = false;
	private final int max_attempts = Keys.MAX_CONNECTIONS_ATTEMPTS;
	private int failureConnection = 1;
	private ChannelReader channelReader = null;
	private String xmlMsg = null;
	
	private String serverIp = null;
	private int serverPort = 0;
	private String moviTraffTag = null;
	
	public ConnectionManager(String serverIp, int serverPort, String tag)
	{
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.moviTraffTag = tag;
	}
	
	private boolean init(String ip, int port, int messageType)
	{
		boolean res = false;
		
		this.socketChannel = new SocketChannel();
		
		while(failureConnection <= max_attempts && this.socketChannel.socket == null)
		{
			failureConnection = this.socketChannel.init(failureConnection, ip, port);
			
			if(failureConnection == 0 || this.socketChannel.socket != null)
			{
				log4j.info("Ok, Socket created.");
				res = true;
				break;
			}
		}
		
		if(failureConnection <= max_attempts)
		{
			try
			{
				log4j.info("Socket is closed ? " + this.socketChannel.socket.isClosed());
				
				if(this.socketChannel.socket != null)
				{
					log4j.info("Starting ChannelReader thread...");
					this.channelReader = new ChannelReader(this.socketChannel.socket.getInputStream(), moviTraffTag, messageType);
					this.channelReader.start();
				}
			}
			catch (IOException e)
			{
				log4j.error(e,e);
			}
		}
		else
		{
			log4j.warn("Exceeded the maximum number of connection attempts [max attempts = " + max_attempts + "]");
			log4j.warn("Impossible to connect to the server " + ip + ":" + port);
		}
		
		return res;
	}
	
	public void sendMessage(int messageType)
	{
		String msg = "";
		if(socketChannel == null)
		{
			socketInitialiazed = init(this.serverIp, this.serverPort, messageType);
			
			if(socketInitialiazed)
			{
				msg = Message.getMessage(messageType);				
				if(messageType == Message.TYPE_SET_TIME)
				{
					//l'ora corrente meno un ora(da gmt a utc) in secondi
					msg = String.format(msg, ((System.currentTimeMillis()/1000) - 3600) , 0);
				}
				
				log4j.info(msg);
				
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					log4j.error(e,e);
				}
				this.socketChannel.sendMsg(msg.getBytes());
				
				try
				{
					this.channelReader.join();
				}
				catch (InterruptedException e)
				{
					log4j.error(e,e);
				}
				
				log4j.info("Ok, message correctly send and response received.. closing the socket..");
				try
				{
					if(this.socketChannel.socket != null && !this.socketChannel.socket.isClosed())
					{
						this.socketChannel.socket.close();
					}
					else
					{
						log4j.info("The socket is closed or null... nothing to do.");
					}
					
				}
				catch (Exception e)
				{
					log4j.error(e,e);
				}
				
				if(this.socketChannel.socket != null && this.socketChannel.socket.isClosed())
				{
					log4j.info("Ok, socket correctly closed.");
				}
			}
			else
			{
				log4j.error("Socekt not correctly initialized.. ");
			}
			
			log4j.info("======================================================");
		}
		
	}
	
	public void setXmlMsg(String xmlMsg)
	{
		this.xmlMsg = xmlMsg;
	}
	
	
}
