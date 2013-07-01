package sistematica.gim.sisasdiss.conf;


/**
 * Contiene i valori delle proprietà  di configurazione. Tali valori vengono
 * riempiti all'inizializzazione del WebAppListener mediante la classe
 * Configuration.
 * 
 * I valori sono contenuti nel file /WEB-INF/nome_contesto.properties e le
 * chiavi hanno i nomi specificati in questa classe.
 */
public class WebSettings extends BaseSettings
{
	// #############################
	// PROJECT NAME
	// #############################
	
    public static final String KEY_PROJECT_NAME = "project.name";
    public static String PROJECT_NAME = null;
    
	// #############################
    // DATABASE
	// #############################
    
    public static final String KEY_DATASOURCE_NAME = "data.source.name";
    public static String DATASOURCE_NAME = null;
    
    public static final String KEY_FCD_SOURCE_NAME = "fcd.source.name";
    public static String FCD_SOURCE_NAME = null;
    
    public static final String KEY_FCD_ARCHIVE_FILE_ENABLE = "fcd.archive.file.enable";
    public static Boolean FCD_ARCHIVE_FILE_ENABLE = false;
    
    public static final String KEY_FCD_ARCHIVE_FILE_DIR = "fcd.archive.file.dir";
    public static String FCD_ARCHIVE_FILE_DIR = "/opt/webservice/SisasDiss/archive";

    public static final String KEY_DB_WRITE_ENABLE = "db.write.enable";
    public static Boolean DB_WRITE_ENABLE = true;
    
    public static final String KEY_DB_WRITE_SQLINSERT = "db.write.sqlinsert";
    public static String DB_WRITE_SQLINSERT = "insert INTO DatiAggregati (DatiAggregati.IdAggregato, DatiAggregati.IdSezione, DatiAggregati.DataOra, DatiAggregati.Periodicita, DatiAggregati.IdCorsia, DatiAggregati.NumeroVeicoli, DatiAggregati.VelocitaMedia, DatiAggregati.VelocitaMassima, DatiAggregati.VelocitaMinima) values (?,?,STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'),?,?,?,?,?,?)";

    public static final String KEY_DISS_PERIODICITA = "diss.periodicita";
    public static String DISS_PERIODICITA = "5";
    
	// #############################
	//  SEND MGR SETTINGS 
	// #############################
	
	public static final String KEY_SENDMGR_ENABLE = "sisasdiss.sendmgr.enable";
	public static Boolean SENDMGR_ENABLE = false;
	
	public static final String KEY_SENDMGR_TIME_MIN = "sisasdiss.sendmgr.time.min";
	public static Integer SENDMGR_TIME_MIN = 5;
	
	public static final String KEY_SENDMGR_TAG_NODE = "sisasdiss.sendmgr.tag.node";
	public static String SENDMGR_TAG_NODE = "SISASDISS";
	
	public static final String KEY_SENDMGR_TAG_NUMPOS = "sisasdiss.sendmgr.tag.numpos";
	public static String SENDMGR_TAG_NUMPOS = "SISASDISS_NUMPOS";
	
	public static final String KEY_SENDMGR_TAG_NUMFILE = "sisasdiss.sendmgr.tag.numfile";
	public static String SENDMGR_TAG_NUMFILE = "SISASDISS_NUMFILE";
	
	public static final String KEY_SENDMGR_TAG_STATUS = "sisasdiss.sendmgr.tag.status";
	public static String SENDMGR_TAG_STATUS = "SISASDISS_STATUS";
	
	public static final String KEY_VTLOGGER_TEXT_URL = "sisasdiss.vtlogger.text.url";
	public static String VTLOGGER_TEXT_URL = "http://89.97.181.43/SiteTrackGIM/VTLoggerText.jsp";
	
	public static final String KEY_VTLOGGER_METRIC_URL = "sisasdiss.vtlogger.metric.url";
	public static String VTLOGGER_METRIC_URL = "http://89.97.181.43/SiteTrackGIM/VTLoggerMetric.jsp";
}
