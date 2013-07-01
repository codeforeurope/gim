/**
 * 
 */
package sistematica.sintelimporter.lib;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import sistematica.sintelimporter.utils.BinaryDecoder;

/**
 * @author gsilvestri
 *
 */
public class SocketChannel
{
	private Logger log4j = Logger.getLogger(SocketChannel.class); 
	
	public Socket socket = null;
	OutputStream os = null;
	InputStream is = null;
	Boolean isClose = false;
	
	public int init(int failureConnection, String ip, int port)
	{
		log4j.info("Create Socket (IP = " + ip + " PORT = " + port + ")... "+(failureConnection>0?" [Connection attempt n. "+failureConnection+"]":""));
		try
		{
			this.socket = new Socket(ip, port);
			os = this.socket.getOutputStream();
			is = this.socket.getInputStream();
			failureConnection = 0;
		}
		catch (Exception e) 
		{
			log4j.error(e,e);
			failureConnection++;
			return failureConnection;
		}
		
		return failureConnection;
	}
	
	public boolean sendMsg(byte[] message)
	{
		boolean ok= false;
		byte[] encoded = null;
		try
		{
			// ENCODED
			encoded = encodeMsg(message);
			log4j.info("Ok, message encoded.. sending..");	

			os.write(encoded, 0, encoded.length);
			os.flush();
			log4j.info("Ok, message send.. flush..");
			ok = true;

			
			//NOT ENCODED
//			os.write(message, 0, message.length);
//			os.flush();
//			log4j.info("Ok, message send.. flush..");
			
		}
		catch (Exception e)
		{
			log4j.error(e,e);
		}
		return ok;
	}
	
	public byte[] encodeMsg(byte[] msg)
	{
		byte[] newMsg = null;
		int payloadLength = 0;
		try
		{
			payloadLength = msg.length;
			newMsg = new byte[payloadLength + 6];
			
			BinaryDecoder.encode(0x01, 1, newMsg, 0);
			BinaryDecoder.encode(payloadLength, 4, newMsg, 1);
			
			for(int i = 0; i < payloadLength; i++)
			{
				newMsg[i+5] = msg[i];
			}
			
			BinaryDecoder.encode(0x00, 1, newMsg, payloadLength+5);
			
			if(log4j.isTraceEnabled())
			{
				for(int i = 0; i < newMsg.length; i++)
				{
					log4j.trace("newMsg[" + i + "] = " + Integer.toHexString(newMsg[i]));
				}
			}
			
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		
		return newMsg;		
	}
}
