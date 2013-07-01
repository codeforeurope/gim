package sistematica.gim.datex;

public class Configuration extends sistematica.pbutils.Configuration {

    public String DATASOURCE = "";
    public String TMP_DIR = "";
    public Long MIN_GRAPH_ID;
    public Long MAX_GRAPH_ID;
    public Boolean RANDOM_RESULTS = false;
    private static Configuration singleton = new Configuration();

    /**
     * @return l'istanza del singleton
     */
    public static Configuration getInstance() {
        return singleton;
    }
}
