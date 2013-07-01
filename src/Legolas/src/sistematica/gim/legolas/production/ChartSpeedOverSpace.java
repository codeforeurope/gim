package sistematica.gim.legolas.production;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.*;
import sistematica.pbutils.FormatLogger;

/**
 * Gestisce la generazione delle immagini per il grafico delle velocità rispetto
 * allo spazio sulle strade censite.
 */
public class ChartSpeedOverSpace {

    private static final FormatLogger logger = FormatLogger.getLogger(ChartSpeedOverSpace.class);
    private String[] colors;
    private Configuration configuration;
    private Streets streets;
    private Map<StreetKey, List<StreetStat>> streetStats = new HashMap<StreetKey, List<StreetStat>>();
    // Visto che il nome originale è decisamente troppo lungo creo questi alias
    private double width;
    private double height;
    private double marginLeft;
    private double marginTop;
    private double marginRight;
    private double marginBottom;
    private Timestamp timestamp;

    /**
     * Crea una nuova istanza di ChartSpeedOverSpace.
     *
     * @param configuration la configurazione del programma
     * @param streets le strade censite
     */
    public ChartSpeedOverSpace(Configuration configuration, Streets streets, Timestamp timestamp) {
        if (configuration.CHART_SPEED_OVER_SPACE_SERIES == null) {
            logger.error("You must configure the property chart.speed.over.space.series.");
            logger.error("Using default values: #440000, #880000, #CC0000");
            colors = new String[]{"#440000", "#880000", "#CC0000"};
        } else {
            colors = configuration.CHART_SPEED_OVER_SPACE_SERIES.split(",");
            for (int i = 0, size = colors.length; i < size; i++) {
                colors[i] = colors[i].trim();
            }
        }

        this.configuration = configuration;
        this.streets = streets;

        width = configuration.CHART_SPEED_OVER_SPACE_WIDTH;
        height = configuration.CHART_SPEED_OVER_SPACE_HEIGHT;
        marginLeft = configuration.CHART_SPEED_OVER_SPACE_MARGIN_LEFT;
        marginTop = configuration.CHART_SPEED_OVER_SPACE_MARGIN_TOP;
        marginRight = configuration.CHART_SPEED_OVER_SPACE_MARGIN_RIGHT;
        marginBottom = configuration.CHART_SPEED_OVER_SPACE_MARGIN_BOTTOM;

        this.timestamp = timestamp;

        // Inizializzo le liste delle statistiche di TUTTE le streetKeys, così
        // verranno creati grafici vuoti per le strade che non hanno statistiche
        for (StreetKey key : streets.getStreetKeys()) {
            streetStats.put(key, new LinkedList<StreetStat>());
        }
    }

    /**
     * Aggiunge una statistica a quelle usate per creare i grafici.
     *
     * @param stat la statistica da aggiungere
     */
    public void addStat(TrafficStat stat) {
        StreetDetail streetDetail = streets.getStreetDetail(stat.getIdEdge(), stat.getEdgeDirection());
        if (streetDetail != null) {
            List<StreetStat> list = streetStats.get(streetDetail.getKey());
            double percSpeed = 100 * Math.min(streetDetail.getFreeFlowSpeed(), stat.getAvgSpeed()) / streetDetail.getFreeFlowSpeed();
            list.add(new StreetStat(streetDetail, percSpeed));
        }
    }

    /**
     * Crea i file SVG con i grafici velocità/spazio (va richiamato dopo tutti
     * gli addStat).
     */
    public void flush() {
        createSvgFragements();
        mergeSvgFragments();
        createTimestamp();
    }

    /**
     * Imposta il proprietario ed il gruppo, se configurati, per il file
     * specificato.
     *
     * @param file il file di cui bisogna cambiare proprietario e gruppo
     */
    private void fixPermissions(File file) {
        file.setReadable(true, false);
        if (file.isDirectory()) {
            file.setExecutable(true, false);
        }
    }

    private List<PercShape> statsToShapes(List<StreetStat> stats, long minId, long maxId) {
        List<PercShape> shapes = new ArrayList<PercShape>(stats.size());

        if (!stats.isEmpty()) {
            double kx = (double) (width - marginLeft - marginRight) / (maxId - minId);
            double ky = (double) (height - marginTop - marginBottom) / 100;

            // Prima le linee...
            StreetStat previous = stats.get(0);
            long previousId = previous.getStreetDetail().getId();
            for (int i = 1, size = stats.size(); i < size; i++) {
                StreetStat current = stats.get(i);
                long currentId = current.getStreetDetail().getId();

                if (currentId == previousId + 1) {
                    shapes.add(new PercLine(kx, ky, marginLeft, marginTop, minId, previous, current));
                }

                previous = current;
                previousId = currentId;
            }

            // ... poi i punti
            for (StreetStat stat : stats) {
                shapes.add(new PercPoint(kx, ky, marginLeft, marginTop, minId, stat));
            }
        }

        return shapes;
    }

    private void rotateSvgFiles(File dir, final String prefix) {
        FilenameFilter prefixFilter = new FilenameFilter() {

            @Override
            public boolean accept(File directory, String name) {
                return name.startsWith(prefix);
            }
        };
        List<File> files = Arrays.asList(dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File directory, String name) {
                return name.startsWith(prefix);
            }
        }));
        Collections.sort(files, Collections.reverseOrder());
        logger.debug("Files to rotate: %s (%d)", files, files.size());

        // Innanzitutto vengono rimossi i file in eccesso, se ve ne sono
        int maxFiles = colors.length;
        int filesToDelete = files.size() - maxFiles + 1;
        logger.debug("Files to delete: %d", filesToDelete);
        if (filesToDelete > 0) {
            for (int i = 0; i < filesToDelete; i++) {
                files.get(i).delete();
            }
            // Leggo la nuova lista dei file
            files = Arrays.asList(dir.listFiles(prefixFilter));
            Collections.sort(files, Collections.reverseOrder());
        }

        // Cambio nome ai file, aumentandone la cifra di 1 (salvo l'ultimo)
        for (int i = 0, size = files.size(); i < size; i++) {
            File file = files.get(i);
            file.renameTo(new File(file.getParentFile(), String.format("%s%d.svg", prefix, size - i)));
        }
    }

    private void createSvgFragment(File file, List<PercShape> shapes) {
        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            for (PercShape shape : shapes) {
                out.println(shape.toSVG());
            }
        } catch (IOException e) {
            logger.error("Error while generating %s", file, e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void createSvgFragements() {
        logger.debug("Creating SVG fragments");

        File baseDir = new File(configuration.CHART_SPEED_OVER_SPACE_DIR);
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            logger.error("Speed/space chart directory creation error (%s)", baseDir);
            return;
        }
        fixPermissions(baseDir);
        logger.debug("Using %s as baseDir", baseDir);

        for (Map.Entry<StreetKey, List<StreetStat>> entry : streetStats.entrySet()) {
            StreetKey key = entry.getKey();
            logger.debug("Creating SVG fragment for street %s", key);

            List<StreetStat> stats = entry.getValue();

            File dir = new File(baseDir, Long.toString(key.getIdStreet()));
            if (!dir.exists() && !dir.mkdirs()) {
                logger.error("Speed/space chart directory creation error (%s)", dir);
            } else {
                fixPermissions(dir);

                Collections.sort(stats);

                String prefix = String.format("dir%d-", key.getDirection().getValue());
                rotateSvgFiles(dir, prefix);

                Street street = streets.getStreet(key);
                File fragment = new File(dir, String.format("%s0.svg", prefix));

                // Ora ho idStreet, minId, maxId, la lista delle statistiche (id, percSpeed), le directory... devo disegnare l'SVG
                List<PercShape> shapes = statsToShapes(stats, street.getMinId(), street.getMaxId());
                createSvgFragment(fragment, shapes);
                fixPermissions(fragment);
            }
        }
    }

    private void mergeSvgFragments() {
        logger.debug("Merging SVG fragments");

        File baseDir = new File(configuration.CHART_SPEED_OVER_SPACE_DIR);

        for (StreetKey key : streetStats.keySet()) { // Scorre su tutte combinazioni idStreet + dir
            File dir = new File(baseDir, Long.toString(key.getIdStreet())); // Directory coi frammenti SVG della coppia idStreet + dir corrent
            if (dir.exists()) {
                File svg = new File(dir, String.format("chart%d.svg", key.getDirection().getValue()));

                PrintWriter out = null;
                BufferedReader in = null;
                String line;

                try {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(svg)));

                    // Scrivo l'intestazione del file
                    printHeader(out);
                    printLabels(out);

                    // Aggrego i frammenti, sostituendo i colori coi valori appropriati
                    final String prefix = String.format("dir%d-", key.getDirection().getValue());
                    List<File> fragments = Arrays.asList(dir.listFiles(new FilenameFilter() {

                        @Override
                        public boolean accept(File directory, String name) {
                            return name.startsWith(prefix);
                        }
                    }));
                    Collections.sort(fragments, Collections.reverseOrder());

                    for (int i = 0, size = fragments.size(); i < size; i++) {
                        File fragment = fragments.get(i);
                        String color = colors[i];

                        try {
                            in = new BufferedReader(new InputStreamReader(new FileInputStream(fragment)));
                            while ((line = in.readLine()) != null) {
                                out.println(line.replaceAll("\\$COLOR\\$", color));
                            }
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }

                    // Scrivo la coda del file
                    printAxis(out);
                    printFooter(out);
                } catch (IOException e) {
                    logger.error("Error while generating %s", svg, e);
                } finally {
                    if (out != null) {
                        out.close();
                        fixPermissions(svg);
                    }
                }
            }
        }
    }

    private void printHeader(PrintWriter out) {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("<?xml version=\"1.0\" standalone=\"no\"?>");
        strBuild.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"");
        strBuild.append("\t\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">");
        strBuild.append(String.format("\n<!-- statistics for %s -->\n", timestamp));
        strBuild.append(String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"%f\" height=\"%f\">", width, height));
        String header = strBuild.toString();
        out.print(header);
    }

    private void printAxis(PrintWriter out) {
        String axisTemplate = "<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\"/>";
        out.println("<g fill=\"none\" stroke=\"black\" stroke-width=\"1\" >");
        out.println(String.format(axisTemplate, marginLeft - marginBottom, height - marginTop, width, height - marginTop)); // Asse X
        out.println(String.format(axisTemplate, marginLeft, 0, marginLeft, height)); // Asse Y
        out.println("</g>");
    }

    private void printLabels(PrintWriter out) {
        String tickTemplate = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" stroke-width=\"1\"/>";
        String gridTemplate = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"#888888\" stroke-width=\"0.5\" stroke-dasharray=\"4 4\"/>";
        String labelTemplate = "<text x=\"%f\" y=\"%f\">%d%%</text>";

        out.println("<g font-size=\"12\" font-family=\"Verdana\">");
        double tickWidth = 10;

        double chartHeight = height - 2 * marginTop;
        double factors[] = new double[]{0, 0.25, 0.5, 0.75};
        for (double factor : factors) {
            double y = marginTop + chartHeight * factor;
            out.println(String.format(tickTemplate, marginLeft - tickWidth, y, marginLeft, y));
            out.println(String.format(gridTemplate, marginLeft, y, width - marginRight, y));

            int label = (int) ((1 - factor) * 100);
            out.println(String.format(labelTemplate, 2.0, y, label));

        }
        out.println("</g>");
    }

    private void printFooter(PrintWriter out) {
        out.println("</svg>");
    }

    private void createTimestamp() {
        File baseDir = new File(configuration.CHART_SPEED_OVER_SPACE_DIR);
        File timestampFile = new File(baseDir, "timestamp.txt");
        logger.debug("Saving timestamp to %s", timestampFile);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(timestampFile)));
            out.println(timestamp.getTime());
        } catch (IOException e) {
            logger.error("Error while generating %s", timestampFile, e);
        } finally {
            if (out != null) {
                out.close();
                fixPermissions(timestampFile);
            }
        }
    }
}
