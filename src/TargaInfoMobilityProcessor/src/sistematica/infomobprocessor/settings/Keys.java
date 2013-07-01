/**
 * 
 */
package sistematica.infomobprocessor.settings;

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
	
	public static final String KEY_DB_DRIVER  = "infomob.db.driverName";
	public static String DB_DRIVER = null;
	
	public static final String KEY_DB_URL     = "infomob.db.jdbcURL";
	public static String DB_URL = null;
	
	public static final String KEY_DB_USER    = "infomob.db.user";
	public static String DB_USER = null;
	
	public static final String KEY_DB_PWD     = "infomob.db.password";
	public static String DB_PWD = null;
		
	// #############################
	//   FTP SETTINGS 
	// #############################
	
	public static final String KEY_FTP_EXPORTER_ENABLE = "infomob.ftp.exporter.enable";
	public static Boolean FTP_EXPORTER_ENABLE = true;
	
	public static final String KEY_FTP_EXPORTER_POLLING_TIME_MIN = "infomob.ftp.exporter.polling.time.min";
	public static Integer FTP_EXPORTER_POLLING_TIME_MIN = 5;
	
	public static final String KEY_FTP_EXPORTER_SERVER_NAME  = "infomob.ftp.exporter.server.name";
	public static String FTP_EXPORTER_SERVER_NAME = null;
	
	public static final String KEY_FTP_EXPORTER_SERVER_PORT  = "infomob.ftp.exporter.server.port";
	public static Integer FTP_EXPORTER_SERVER_PORT = null;
	
	public static final String KEY_FTP_EXPORTER_SERVER_USER = "infomob.ftp.exporter.server.user";
	public static String FTP_EXPORTER_SERVER_USER = null;
	
	public static final String KEY_FTP_EXPORTER_SERVER_PWD  = "infomob.ftp.exporter.server.pwd";
	public static String FTP_EXPORTER_SERVER_PWD = null;
	
	public static final String KEY_FTP_EXPORTER_ARCHIVE_DIR = "infomob.ftp.exporter.archive.dir";
	public static String FTP_EXPORTER_ARCHIVE_DIR = null;
	
	public static final String KEY_FTP_EXPORTER_DEL_SERVER_FILE_ENABLE = "infomob.ftp.exporter.del.server.file.enable";
	public static Boolean FTP_EXPORTER_DEL_SERVER_FILE_ENABLE = false;
	
	public static final String KEY_FTP_EXPORTER_EXPORTED_FILE_LIST = "infomob.ftp.exporter.exported.file.list";
	public static String FTP_EXPORTER_EXPORTED_FILE_LIST = ".." + File.separatorChar + "cfg" + File.separatorChar + "ExportedFile.txt";
	
	public static final String KEY_FTP_EXPORTER_MAX_FILE_DOWNLOAD = "infomob.ftp.exporter.max.file.download";
	public static Integer FTP_EXPORTER_MAX_FILE_DOWNLOAD = 100;	
	
	
	// #############################
	//   RAW DATA PROCESSOR SETTINGS 
	// #############################	
	
	public static final String KEY_RAWDATA_PROCESSOR_ENABLE = "infomob.rawdata.processor.enable";
	public static Boolean RAWDATA_PROCESSOR_ENABLE = true;
	
	public static final String KEY_RAWDATA_PROCESSOR_POLLING_TIME_MIN = "infomob.rawdata.processor.time.min";
	public static Integer RAWDATA_PROCESSOR_POLLING_TIME_MIN = 5;
	
	public static final String KEY_RAWDATA_PROCESSOR_OK_DIR = "infomob.rawdata.processor.ok.dir";
	public static String RAWDATA_PROCESSOR_OK_DIR = null;
	
	public static final String KEY_RAWDATA_PROCESSOR_ERROR_DIR = "infomob.rawdata.processor.error.dir";
	public static String RAWDATA_PROCESSOR_ERROR_DIR = null;
	
	public static final String KEY_RAWDATA_PROCESSOR_EMPTY_DIR = "infomob.rawdata.processor.empty.dir";
	public static String RAWDATA_PROCESSOR_EMPTY_DIR = null;
	
	public static final String KEY_RAWDATA_PROCESSOR_MAX_FILE_PROCESS = "infomob.rawdata.processor.max.file.process";
	public static Integer RAWDATA_PROCESSOR_MAX_FILE_PROCESS = 10;
	
	public static final String KEY_RAWDATA_PROCESSOR_DATEFORMAT_BAD_FILE = "infomob.rawdata.processor.dateformat.bad.file";
	public static String RAWDATA_PROCESSOR_DATEFORMAT_BAD_FILE = "yyyyMMddHHmmss";
		
	public static final String KEY_RAWDATA_PROCESSOR_TABLE_NAME = "infomob.rawdata.processor.table.name";
	public static String RAWDATA_PROCESSOR_TABLE_NAME = "position_fcd";
	
	public static final String KEY_RAWDATA_PROCESSOR_DEL_OK_FILE = "infomob.rawdata.processor.del.ok.file";
	public static Boolean RAWDATA_PROCESSOR_DEL_OK_FILE = false;
	
	public static final String KEY_RAWDATA_PROCESSOR_DEL_ERROR_FILE = "infomob.rawdata.processor.del.error.file";
	public static Boolean RAWDATA_PROCESSOR_DEL_ERROR_FILE = false;
	
	public static final String KEY_RAWDATA_PROCESSOR_DEL_EMPTY_FILE = "infomob.rawdata.processor.del.empty.file";
	public static Boolean RAWDATA_PROCESSOR_DEL_EMPTY_FILE = false;
	
	public static final String KEY_RAWDATA_PROCESSOR_LATLON_LONG_ENABLE = "infomob.rawdata.processor.latlon.long.enable";
	public static Boolean RAWDATA_PROCESSOR_LATLON_LONG_ENABLE = true;
	
	public static final String KEY_RAWDATA_PROCESSOR_SOURCE_NAME = "infomob.rawdata.processor.source.name";
	public static String RAWDATA_PROCESSOR_SOURCE_NAME = "T";
	
	// #############################
	//  SEND MGR SETTINGS 
	// #############################
	
	public static final String KEY_SENDMGR_ENABLE = "infomob.sendmgr.enable";
	public static Boolean SENDMGR_ENABLE = false;
	
	public static final String KEY_SENDMGR_TIME_MIN = "infomob.sendmgr.time.min";
	public static Integer SENDMGR_TIME_MIN = 5;
	
	public static final String KEY_SENDMGR_TAG_NODE = "infomob.sendmgr.tag.node";
	public static String SENDMGR_TAG_NODE = "TARGA";
	
	public static final String KEY_SENDMGR_TAG_NUMPOS = "infomob.sendmgr.tag.numpos";
	public static String SENDMGR_TAG_NUMPOS = "TARGA_NUMPOS";
	
	public static final String KEY_SENDMGR_TAG_NUMVEH = "infomob.sendmgr.tag.numveh";
	public static String SENDMGR_TAG_NUMVEH = "TARGA_NUMVEH";
	
	public static final String KEY_SENDMGR_TAG_STATUS = "infomob.sendmgr.tag.status";
	public static String SENDMGR_TAG_STATUS = "TARGA_STATUS";
	
	public static final String KEY_VTLOGGER_TEXT_URL = "infomob.vtlogger.text.url";
	public static String VTLOGGER_TEXT_URL = "http://89.97.181.43/SiteTrackGIM/VTLoggerText.jsp";
	
	public static final String KEY_VTLOGGER_METRIC_URL = "infomob.vtlogger.metric.url";
	public static String VTLOGGER_METRIC_URL = "http://89.97.181.43/SiteTrackGIM/VTLoggerMetric.jsp";
	

}
