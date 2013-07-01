/**
 * 
 */
package sistematica.sintelimporter.settings;

import java.io.File;

import sistematica.apptemplate.cfg.BaseSettings;

/**
 * @author gsilvestri
 *
 */
public class Keys extends BaseSettings
{
	// #############################
	//   DATABASE 
	// #############################
	
	public static final String KEY_DB_DRIVER = "sintel.db.driverName";
	public static String DB_DRIVER = null;
	
	public static final String KEY_DB_URL = "sintel.db.jdbcURL";
	public static String DB_URL = null;
	
	public static final String KEY_DB_USER = "sintel.db.user";
	public static String DB_USER = null;
	
	public static final String KEY_DB_PWD = "sintel.db.password";
	public static String DB_PWD = null;
	
	// #############################
	//   WEBSERVICE SETTINGS 
	// #############################
	
	public static final String KEY_CONFIGURATION_FILE = "sintel.configuration.file";
	public static String CONFIGURATION_FILE = "." + File.separatorChar + "cfg" + File.separatorChar + "application.properties";
	
	public static final String KEY_ARCHIVE_XML_ENABLED = "sintel.archive.xml.enabled";
	public static Boolean ARCHIVE_XML_ENABLED = true;
	
	public static final String KEY_ARCHIVE_XML_DIR = "sintel.archive.xml.dir";
	public static String ARCHIVE_XML_DIR = ".." + File.separatorChar + "archive";
	
	public static final String KEY_MOVITRAFF_NUMBER = "sintel.movitraff.number";
	public static Integer MOVITRAFF_NUMBER = 1;
	
	public static final String KEY_POLLING_TIME_MIN = "sintel.polling.time.min";
	public static Integer POLLING_TIME_MIN = 1;
	
	public static final String KEY_MAX_CONNECTIONS_ATTEMPTS = "sintel.max.connections.attempts";
	public static Integer MAX_CONNECTIONS_ATTEMPTS = 3;
	
	
}
