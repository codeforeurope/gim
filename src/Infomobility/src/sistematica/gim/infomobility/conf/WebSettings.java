package sistematica.gim.infomobility.conf;


/**
 * Contiene i valori delle proprietà di configurazione. Tali valori vengono
 * riempiti all'inizializzazione del WebAppListener mediante la classe
 * Configuration.
 * 
 * I valori sono contenuti nel file /WEB-INF/nome_contesto.properties e le
 * chiavi hanno i nomi specificati in questa classe.
 */
public class WebSettings extends BaseSettings
{
	// PROJECT NAME
    public static final String KEY_PROJECT_NAME = "project.name";
    public static String PROJECT_NAME = null;
		
    // DATABASE
    public static final String KEY_DATASOURCE_NAME = "data.source.name";
    public static String DATASOURCE_NAME = null;
    
    public static final String KEY_FCD_SOURCE_NAME = "fcd.source.name";
    public static String FCD_SOURCE_NAME = null;
    
    public static final String KEY_FCD_LATLON_LONG_ENABLE = "fcd.latlon_long.enable";
    public static Boolean FCD_LATLON_LONG_ENABLE = false;
    
    public static final String KEY_FCD_ARCHIVE_FILE_ENABLE = "fcd.archive.file.enable";
    public static Boolean FCD_ARCHIVE_FILE_ENABLE = false;
    
    public static final String KEY_FCD_ARCHIVE_FILE_DIR = "fcd.archive.file.dir";
    public static String FCD_ARCHIVE_FILE_DIR = "/opt/webservice/Infomobility/archive";
    
	// #############################
	//  SEND MGR SETTINGS 
	// #############################
	
	public static final String KEY_SENDMGR_ENABLE = "infomob.sendmgr.enable";
	public static Boolean SENDMGR_ENABLE = false;
	
	public static final String KEY_SENDMGR_TIME_MIN = "infomob.sendmgr.time.min";
	public static Integer SENDMGR_TIME_MIN = 5;
	
	public static final String KEY_SENDMGR_TAG_NODE = "infomob.sendmgr.tag.node";
	public static String SENDMGR_TAG_NODE = "INFOMOBILITY";
	
	public static final String KEY_SENDMGR_TAG_NUMPOS = "infomob.sendmgr.tag.numpos";
	public static String SENDMGR_TAG_NUMPOS = "INFOMOBILITY_NUMPOS";
	
	public static final String KEY_SENDMGR_TAG_NUMFILE = "infomob.sendmgr.tag.numfile";
	public static String SENDMGR_TAG_NUMFILE = "INFOMOBILITY_NUMFILE";
	
	public static final String KEY_SENDMGR_TAG_STATUS = "infomob.sendmgr.tag.status";
	public static String SENDMGR_TAG_STATUS = "INFOMOBILITY_STATUS";
	
	public static final String KEY_VTLOGGER_TEXT_URL = "infomob.vtlogger.text.url";
	public static String VTLOGGER_TEXT_URL = "http://89.97.181.43/SiteTrackGIM/VTLoggerText.jsp";
	
	public static final String KEY_VTLOGGER_METRIC_URL = "infomob.vtlogger.metric.url";
	public static String VTLOGGER_METRIC_URL = "http://89.97.181.43/SiteTrackGIM/VTLoggerMetric.jsp";
}
