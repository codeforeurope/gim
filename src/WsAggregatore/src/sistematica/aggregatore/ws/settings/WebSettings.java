package sistematica.aggregatore.ws.settings;


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
    
    // FCD SOURCE
    public static final String KEY_FCD_SOURCE = "fcd.source";
    public static String FCD_SOURCE = "'T', 'P'";
    
    // GRAPH SETTINGS
    public static final String KEY_GRAPH_NAME = "graph.name";
    public static String GRAPH_NAME = "Navteq";
    
    public static final String KEY_GRAPH_VERSION = "graph.version";
    public static String GRAPH_VERSION = "2011";
    
    // TEST
    public static final String KEY_TEST_FILE_NAME = "test.file.name";
    public static String TEST_FILE_NAME = null;

//    // AMBIENTE
//    public static final String KEY_NOME_AMBIENTE = "nome.ambiente";
//    public static String NOME_AMBIENTE = null;
    
}
