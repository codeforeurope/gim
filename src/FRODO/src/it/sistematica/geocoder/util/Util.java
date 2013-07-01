package it.sistematica.geocoder.util;

import it.sistematica.geocoder.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import visualtrack.logger.client.VTLoggerMetric;
import visualtrack.logger.client.VTLoggerText;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

public class Util
{
	private static Logger log = Logger.getLogger(Util.class);
	
	private static Properties m_cfg;
	private static String geocoderPropFile = "geocoder.properties";
	
	private static String last_id_select = "select value from configuration where key = ? ";
	private static String last_id_insert = "update configuration set value = ? where key = ? ";
	
	private static final String LAST_ID_PROCESSED_KEY = "IN_LAST_ID_PROCESSED";
	
	private static Util util = new Util();
	
	private static VTLoggerMetric vtmlog;
	private static VTLoggerText vttlog;
	
	private Properties readProperties()
	{
		if(m_cfg == null)
		{
			try
			{
				InputStream enea_properties = getClass().getClassLoader().getResourceAsStream(geocoderPropFile);  //ClassLoader.getSystemResourceAsStream(spatialPropFile);
				m_cfg = new Properties();
				m_cfg.load(enea_properties);
			}
			catch(IOException ioe)
			{
				log.error(ioe.getMessage(), ioe);
			}
		}
		
		return m_cfg;
	}
	
	public static String getProperty(String key)
	{
		return util.readProperties().getProperty(key);
	}
	
	public static String getProperty(Properties p, String key)
	{
		String value = null;
		if(p != null)
		{
			value = p.getProperty(key);
			if(value == null)
				value = util.readProperties().getProperty(key);
		}
		else
		{
			value = util.readProperties().getProperty(key);
		}

		return value;
	}
	
	public static long getLastIdProcessed(Connection conn) throws Exception
	{
		long last_id = -1;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			pstmt = conn.prepareStatement(last_id_select);
			pstmt.setString(1, LAST_ID_PROCESSED_KEY);
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
				last_id = Long.parseLong(rs.getString("value"));
		}
		finally
		{
			if(pstmt != null)
				pstmt.close();
			if(rs != null)
				rs.close();
		}
		
		return last_id;
	}
	
	public static void setLastIdProcessed(Connection conn, long last_id) throws Exception
	{
		PreparedStatement pstmt = null;
		
		try
		{
			pstmt = conn.prepareStatement(last_id_insert);
			pstmt.setLong(1, last_id);
			pstmt.setString(2, LAST_ID_PROCESSED_KEY);
			
			pstmt.executeUpdate();
		}
		finally
		{
			if(pstmt != null)
				pstmt.close();
		}
	}
	
	public static void sendMail(String subject, Exception e)
	{
		String msg = e.getMessage() + "\n\n";
		
		StackTraceElement[] stacks = e.getStackTrace();
		for(int i=0;i<stacks.length;i++)
		{
			StackTraceElement stack = stacks[i];
			msg += stack.toString() + "\n";
		}
		
		sendMail(subject, msg);
	}
	
	public static void sendMail(String subject, String msg)
	{
		try
		{
			String host = Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.host");
			String port = Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.port");
			String username = Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.user");
			String password = Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.password");
	 
		    String from_add = Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.from");
		    String to_add = Util.getProperty(Main.custom_properties, "geocoder.mail.smtp.to");
	
		    // Get system properties
		    Properties props = new Properties();
	
		    // Setup mail server
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.port", port);
		    
		    password = new String(Base64Utils.decode(password.getBytes()));
		    
		    MyAuthenticator auth = new MyAuthenticator(username, password);
	
		    // Get session
		    Session session = Session.getDefaultInstance(props, auth);
		    InternetAddress from = new InternetAddress(from_add);
		    InternetAddress to = new InternetAddress(to_add);
	
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(from);
		    message.addRecipient(Message.RecipientType.TO, to);
	
		    message.setSubject(subject);
		    message.setText(msg);
		    Transport.send(message); 
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	public static void initSismon() throws MalformedURLException
	{
		VTLoggerThreadPool.init(Integer.parseInt(Util.getProperty(Main.custom_properties, "visual.track.thread.number")), 
								Integer.parseInt(Util.getProperty(Main.custom_properties, "visual.track.thread.post.timeout")));
		
		vtmlog = new VTLoggerMetric(Util.getProperty(Main.custom_properties, "visual.track.metric.url"), true);
		vttlog = new VTLoggerText(Util.getProperty(Main.custom_properties, "visual.track.log.url"), true);
	}
	
	public static void sismonSendMetric(String metrica, String value)
	{
		vtmlog.sendValue(metrica, value, System.currentTimeMillis()); 
	}
	
	public static void closeSismon()
	{
		VTLoggerThreadPool.terminateExecutorService(false); 
	}
	
	public static void sismonSendLog(String tag, String log)
	{
		vttlog.info(tag, log, System.currentTimeMillis()); 
	}
}

