/**
 * 
 */
package sistematica.famasimporter.settings;

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
	
	public static final String KEY_DB_DRIVER = "famas.db.driverName";
	public static String DB_DRIVER = null;
	
	public static final String KEY_DB_URL = "famas.db.jdbcURL";
	public static String DB_URL = null;
	
	public static final String KEY_DB_USER = "famas.db.user";
	public static String DB_USER = null;
	
	public static final String KEY_DB_PWD = "famas.db.password";
	public static String DB_PWD = null;
	
	// #############################
	//   WEBSERVICE SETTINGS 
	// #############################
	
	public static final String KEY_WS_URL = "famas.ws.url";
	public static String WS_URL = "http://217.199.0.156:8090/RPC2";
	
	public static final String KEY_WS_IP = "famas.ws.ip";
	public static String WS_IP = "217.199.0.156";
	
	public static final String KEY_WS_PORT = "famas.ws.port";
	public static Integer WS_PORT = 8090;
	
	public static final String KEY_WS_POLLING_TIME_MIN = "famas.ws.polling.time.min";
	public static Double WS_POLLING_TIME_MIN = 15d;
	
	public static final String KEY_WS_ARCHIVE_BASE_DIR = "famas.ws.archive.base.dir";
	public static String WS_ARCHIVE_BASE_DIR = ".." + File.separatorChar + "archive";
	
	public static final String KEY_WS_AGGREGATI_ENABLED = "famas.ws.aggregati.enabled";
	public static Boolean WS_AGGREGATI_ENABLED = true;
	
	public static final String KEY_WS_AGGREGATI_METHOD_NAME = "famas.ws.aggregati.method.name";
	public static String WS_AGGREGATI_METHOD_NAME = "panama.getAggregati";
	
	public static final String KEY_WS_AGGREGATI_LASTID_FILE = "famas.ws.aggregati.lastid.file";
	public static String WS_AGGREGATI_LASTID_FILE = ".." + File.separatorChar + "cfg" + File.separatorChar + "lastid_aggregati.txt";
	
	public static final String KEY_WS_AGGREGATI_ARCHIVE_ENABLED = "famas.ws.aggregati.archive.enabled";
	public static Boolean WS_AGGREGATI_ARCHIVE_ENABLED = true;
	
	public static final String KEY_WS_AGGREGATI_ARCHIVE_DIR = "famas.ws.aggregati.archive.dir";
	public static String WS_AGGREGATI_ARCHIVE_DIR = "aggregati";
	
	public static final String KEY_WS_AGGREGATI_DB_WRITE_ENABLED = "famas.ws.aggregati.db.write.enabled";
	public static Boolean WS_AGGREGATI_DB_WRITE_ENABLED = true;
	
	public static final String KEY_WS_VEICOLARI_ENABLED = "famas.ws.veicolari.enabled";
	public static Boolean WS_VEICOLARI_ENABLED = false;
	
	public static final String KEY_WS_VEICOLARI_METHOD_NAME = "famas.ws.veicolari.method.name";
	public static String WS_VEICOLARI_METHOD_NAME = "panama.getDatiVeicoli";
	
	public static final String KEY_WS_VEICOLARI_LASTID_FILE = "famas.ws.veicolari.lastid.file";
	public static String WS_VEICOLARI_LASTID_FILE = ".." + File.separatorChar + "cfg" + File.separatorChar + "lastid_veicolari.txt";
	
	public static final String KEY_WS_VEICOLARI_ARCHIVE_ENABLED = "famas.ws.veicolari.archive.enabled";
	public static Boolean WS_VEICOLARI_ARCHIVE_ENABLED = true;
	
	public static final String KEY_WS_VEICOLARI_ARCHIVE_DIR = "famas.ws.veicolari.archive.dir";
	public static String WS_VEICOLARI_ARCHIVE_DIR = "veicolari";
}
