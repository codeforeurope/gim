package sistematica.gim.legolas;

/**
 * Parametri di configurazione del programma.
 */
public class Configuration extends sistematica.pbutils.Configuration {

    /**
     * Il programma è in modalità sperimentale (per default, no)?
     */
    public Boolean MODE_EXPERIMENTAL = false;
    /**
     * Classe del driver JDBC da utilizzare (con tanto di package).
     */
    public String DB_DRIVER;
    /**
     * URL del DB, nel formato del driver JDBC.
     */
    public String DB_URL;
    /**
     * Utente del DB.
     */
    public String DB_USER;
    /**
     * Password del DB.
     */
    public String DB_PASSWORD;
    /**
     * Numero di minuti che intercorrono tra un'esecuzione e l'altra del calcolo
     * real-time.
     *
     * <p>Il comportamento è identico a quello del demone cron, ovvero se
     * specifico un PERIOD_MINUTES pari a 15 minuti il calcolo verrà eseguito
     * ogni volta che l'orologio segna 0, 15, 30 e 45 minuti.
     */
    public Integer PERIOD_MINUTES;
    /**
     * Il numero massimo di minuti di differenza rispetto alla data attuale
     * oltre il quale le velocità istantanee vengono scartate perché troppo
     * vecchie. Se è null vengono considerate tutte le velocità istantanee.
     */
    public Long MAX_INPUT_AGE_MINUTES;
    /**
     * Il numero massimo di secondi di differenza tra due campionamenti FCD
     * consecutivi di uno stesso veicolo oltre il quale non possono più essere
     * usati per calcolare l'heading.
     */
    public Long MAX_CONSECUTIVE_FCD_SECONDS_FOR_HEADING = 20l * 60l;
    /**
     * La velocità in Km/h sotto la quale il campionamento FCD può essere
     * considerato fermo.
     */
    public Long MIN_FCD_SPEED_BEFORE_STOP = 1l;
    /**
     * Il modello matematico da usare per l'analisi macroscopica del traffico.
     */
    public String TRAFFIC_MODEL = "Greenshield";
    /**
     * Il file su cui sono scritti i log.
     */
    public String LOG_FILE;
    /**
     * La porta su cui verrà messo in ascolto il web server embedded.
     */
    public Integer WEB_PORT = 16182;
    /**
     * La password per accedere all'interfaccia web.
     */
    public String WEB_PASSWORD;
    /**
     * Abilita o meno l'invio dei dati di log a VisualTrack.
     */
    public Boolean VT_LOGGER_ENABLED = false;
    /**
     * L'URL della JSP di VTLogger, per l'invio delle metriche al manager.
     */
    public String VT_LOGGER_URL;
    /**
     * Numero di thread usati da VTLogger.
     */
    public Integer VT_LOGGER_THREADS = 5;
    /**
     * Timeout in lettura delle connessioni del logger.
     */
    public Integer VT_LOGGER_TIMEOUT_SECS = 5;
    /**
     * Intervallo di invio delle statistiche.
     */
    public Integer METRICS_PERIOD_SECS = 300;
    /**
     * Abilita la creazione dei file SVG con il grafico della velocità rispetto
     * allo spazio per tutte le strade censite.
     */
    public Boolean CHART_SPEED_OVER_SPACE_ENABLED = false;
    /**
     * La directory dove andranno salvate le SVG del diagramma velocità/spazio
     * per le strade censite.
     */
    public String CHART_SPEED_OVER_SPACE_DIR;
    /**
     * La lista dei colori (#RRGGBB), separati da virgola, delle serie del
     * grafico; da questo valore viene dedotto implicitamente il numero delle
     * serie.
     */
    public String CHART_SPEED_OVER_SPACE_SERIES;
    /**
     * Larghezza del diagramma.
     */
    public Double CHART_SPEED_OVER_SPACE_WIDTH;
    /**
     * Altezza del diagramma.
     */
    public Double CHART_SPEED_OVER_SPACE_HEIGHT;
    /**
     * Margine sinistro del diagramma.
     */
    public Double CHART_SPEED_OVER_SPACE_MARGIN_LEFT;
    /**
     * Margine destro del diagramma.
     */
    public Double CHART_SPEED_OVER_SPACE_MARGIN_RIGHT;
    /**
     * Margine superiore del diagramma.
     */
    public Double CHART_SPEED_OVER_SPACE_MARGIN_TOP;
    /**
     * Margine inferiore del diagramma.
     */
    public Double CHART_SPEED_OVER_SPACE_MARGIN_BOTTOM;
    /**
     * Query per inserire una statistica real-time (live).
     */
    public String SQL_INSERT_LIVE_STATS;
    /**
     * Query per cancellare le vecchie statistiche real-time (live) sulla base
     * del loro timestamp.
     */
    public String SQL_DELETE_LIVE_STATS;
    /**
     * Query per inserire le statistiche sull'archivio storico.
     */
    public String SQL_INSERT_HISTORICAL_STATS;
    /**
     * Query per cancellare delle statistiche dall'archivio storico sulla base
     * del loro timestamp.
     */
    public String SQL_DELETE_HISTORICAL_STATS;
    /**
     * Query per leggere l'elenco delle sorgenti di dati FCD.
     */
    public String SQL_GET_SOURCES;
    /**
     * Query per l'inserimento di statistiche generate da un esperimento.
     */
    public String SQL_INSERT_EXPERIMENTAL_STATS;
    /**
     * Query per la cancellazione di statistiche sperimentali sulla base dell'ID
     * dell'esperimento.
     */
    public String SQL_DELETE_EXPERIMENTAL_STATS;
    /**
     * Query per leggere la lista dei template di esperimenti disponibili.
     */
    public String SQL_GET_EXPERIMENTS;
    /**
     * Query per leggere il template di uno specifico esperimento, dato il suo
     * ID.
     */
    public String SQL_GET_EXPERIMENT;
    /**
     * Query per cancellare un template di esperimento.
     */
    public String SQL_DELETE_EXPERIMENT;
    /**
     * Query per l'inserimento di un template di esperimento.
     */
    public String SQL_INSERT_EXPERIMENT;
    /**
     * Query per leggere la lista degli esperimenti eseguiti.
     */
    public String SQL_GET_EXECUTED_EXPERIMENTS;
    /**
     * Query per leggere le statistiche prodotte da un esperimento sulla base
     * del suo ID.
     */
    public String SQL_GET_EXPERIMENT_STATS;
    /**
     * Query per la lettura del grafo stradale.
     */
    public String SQL_GET_ROADGRAPH;
    /**
     * Query per la lettura dei campionamenti FCD georeferenziati riferiti ad un
     * determinato intervallo temporale.
     */
    public String SQL_GET_FCD;
    /**
     * Query per la lettura dei campionamenti FCD georeferenziati riferiti ad un
     * determinato intervallo temporale e ad un determinato elenco di sorgenti
     * dati. Legolas sostituirà la stringa %SOURCES% con le sorgenti
     * selezionate; se le sorgenti sono A, B e C Legolas sostituirà %SOURCES%
     * con 'A', 'B', 'C'.
     */
    public String SQL_GET_FCD_SOURCES_FILTER;
    /**
     * Query per la lettura degli attributi delle strade censite.
     */
    public String SQL_GET_STREETS;
    /**
     * Query per la lettura dei dettagli (arco per arco) delle strade censite.
     */
    public String SQL_GET_STREETS_DETAILS;
    /**
     * Seriale del disco fisso con cui sono state cifrate le strade censite; se
     * non viene specificato legge il seriale del disco su cui Legolas è in
     * esecuzione.
     */
    public String HDD_SERIAL;
}
