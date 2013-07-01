package it.sistematica.geocoder.util;

import it.sistematica.geocoder.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DbManager
{
	public static final int SYSTEM = 0;
	public static final int SPATIAL = 1;
	
	private String driver;
	private String url;
	private String user;
	private String pass;
	
	private static DbManager system_mng;
	private static DbManager spatial_mng;
	
	private static Logger log = Logger.getLogger(DbManager.class);
			
	private DbManager(int type)
	{
		init(type);
	}
	
	public static synchronized DbManager getInstance(int type)
	{
		if(type == SYSTEM)
		{
			if(system_mng == null)
				system_mng = new DbManager(type);
			
			return system_mng;
		}
		else
		{
			if(spatial_mng == null)
				spatial_mng = new DbManager(type);
			
			return spatial_mng;
		}
	}
	
	private void init(int type)
	{
		log.debug("Initializing DataBase properties.....");
		
		if(type == SYSTEM)
		{
			this.driver = Util.getProperty(Main.custom_properties, "geocoder.db.system.driver");
			this.url = Util.getProperty(Main.custom_properties, "geocoder.db.system.url");
			this.user = Util.getProperty(Main.custom_properties, "geocoder.db.system.user");
			this.pass = Util.getProperty(Main.custom_properties, "geocoder.db.system.pass");
		}
		else
		{
			this.driver = Util.getProperty(Main.custom_properties, "geocoder.db.spatial.driver");
			this.url = Util.getProperty(Main.custom_properties, "geocoder.db.spatial.url");
			this.user = Util.getProperty(Main.custom_properties, "geocoder.db.spatial.user");
			this.pass = Util.getProperty(Main.custom_properties, "geocoder.db.spatial.pass");
		}
		
		log.debug(".....Done");
	}
	
	public Connection getConnessione() throws Exception
	{
		Class.forName(this.driver);
		
		return DriverManager.getConnection(this.url,this.user,this.pass);
	}
	
	public static void closeConnection(Connection conn)
	{
		try
		{
			if(conn != null)
				conn.close();
		}
		catch(SQLException e)
		{}
	}
	
	public static void closeResources(Statement st, ResultSet rs)
	{
		try
		{
			if(st != null)
				st.close();
		}
		catch(SQLException e)
		{}
		
		try
		{
			if(rs != null)
				rs.close();
		}
		catch(SQLException e)
		{}
	}
}

