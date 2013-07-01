package sistematica.gim.legolas.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.algorithm.DensityModel;
import sistematica.gim.legolas.experiments.Experiment;
import sistematica.gim.legolas.experiments.ExperimentsDAO;
import sistematica.gim.legolas.entity.Source;
import sistematica.gim.legolas.entity.SourcesDAO;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobDescriptorFactory;
import sistematica.gim.legolas.scheduler.JobType;
import sistematica.gim.legolas.scheduler.Scheduler;
import sistematica.pbutils.FormatLogger;

/**
 * Servlet che viene richiamata via Ajax dalla pagina di esecuzione degli
 * esperimenti.
 */
public class AjaxExperimentsServlet extends BaseServlet {

    private static final FormatLogger logger = FormatLogger.getLogger(AjaxExperimentsServlet.class);
    private Connection connection;
    private JobDescriptorFactory jobDescriptorFactory;

    /**
     * Crea una nuova istanza di AjaxExperimentsServlet.
     * 
     * @param configuration la configurazione del programma
     * @param velocity la template engine
     * @param scheduler lo scheduler di esecuzione dei job
     * @param connection la connessione al DB
     * @param jobDescriptorFactory factory per la creazione semplificata dei job descriptor
     */
    public AjaxExperimentsServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler, Connection connection, JobDescriptorFactory jobDescriptorFactory) {
        super(configuration, velocity, scheduler);
        this.connection = connection;
        this.jobDescriptorFactory = jobDescriptorFactory;
    }

    /**
     * @return la lista degli esperimenti presenti su DB in formato JSON
     * @throws IOException se c'è un errore nell'esecuzione della query
     */
    private String getExperimentsList() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        try {
            for (Experiment e : new ExperimentsDAO(configuration, connection).getExperiments()) {
                builder.append(e.toJSON());
                builder.append(",");
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return la lista delle sorgenti di dati raw in formato JSON
     * @throws IOException se c'è un errore nell'esecuzione della query
     */
    private String getSourcesList() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        try {
            for (Source s : new SourcesDAO(configuration, connection).getSources()) {
                builder.append(s.toJSON());
                builder.append(",");
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return la lista dei modelli di calcolo per la densità veicolare in formato JSON
     */
    private String getModelsList() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (DensityModel model : DensityModel.values()) {
            builder.append(String.format("\"%s\"", model.toString()));
            builder.append(",");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * @param id l'ID dell'esperimento da cancellare
     * @return una stringa JSON che descrive l'esito della chiamata
     */
    private String deleteExperiment(long id) {
        String res = "{}";

        try {
            new ExperimentsDAO(configuration, connection).deleteExperiment(id);
        } catch (SQLException ex) {
            res = "{\"error\": \"cannot create experiment\"}";
        }

        return res;
    }

    /**
     * Crea un esperimento su DB a partire dalla codifica JSON.
     * 
     * @param json il JSON che rappresenta l'esperimento da creare
     * @return una stringa JSON che descrive l'esito della chiamata
     */
    private String createExperiment(String json) {
        String res = "{}";
        logger.debug("createExperiment: " + json);

        JsonElement jse = new JsonParser().parse(json);
        JsonObject object = jse.getAsJsonObject();

        try {
            String description = object.get("description").getAsString();
            String sources = object.get("sources").getAsString();
            sources = sources.length() == 0 ? null : sources;
            DensityModel model = DensityModel.fromString(object.get("model").getAsString());
            Date timestamp = WebServer.DATE_TIME_FORMAT.parse(object.get("timestamp").getAsString());

            logger.debug("description: " + description);
            logger.debug("sources: " + sources);
            logger.debug("model: " + model);
            logger.debug("timestamp: " + timestamp);

            if (description.length() == 0) {
                throw new IllegalArgumentException("Experiments cannot have empty descriptions");
            }

            Experiment experiment = new Experiment(0, description, sources, model, timestamp);
            new ExperimentsDAO(configuration, connection).insertExperiment(experiment);
        } catch (ParseException ex) {
            res = "{\"error\": \"cannot create experiment (bad date format)\"}";
            logger.error("Cannot create experiment from JSON '%s'", ex, json);
        } catch (SQLException ex) {
            res = "{\"error\": \"cannot create experiment\"}";
            logger.error("Cannot create experiment from JSON '%s'", ex, json);
        } catch (IllegalArgumentException ex) {
            res = "{\"error\": \"cannot create experiment (empty description)\"}";
            logger.error("Cannot create experiment from JSON '%s'", ex, json);
        }

        return res;
    }

    /**
     * Esegue l'esperimento specificato.
     * 
     * @param id l'ID dell'esperimento sul DB
     * @return una stringa JSON che descrive l'esito della chiamata
     */
    private String runExperiment(String id) {
        String res = "{}";

        try {
            ExperimentsDAO dao = new ExperimentsDAO(configuration, connection);
            Experiment experiment = dao.getExperiment(Integer.parseInt(id));
            JobDescriptor descriptor = jobDescriptorFactory.newJob(JobType.EXPERIMENTAL, experiment);
            scheduler.schedule(descriptor);
        } catch (NumberFormatException ex) {
            res = "{\"error\": \"bad experiment id\"}";
            logger.error("Cannot schedule experiment", ex);
        } catch (Exception ex) {
            res = String.format("{\"error\": \"cannot schedule experiment %s\"}", id);
            logger.error("Cannot schedule experiment", ex);
        }

        return res;
    }

    /**
     * @return la lista degli esperimenti eseguiti in formato JSON
     * @throws IOException se c'è un errore nell'esecuzione della query
     */
    private String getExecutedExperimentsList() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        try {
            for (Experiment e : new ExperimentsDAO(configuration, connection).getExecutedExperiments()) {
                builder.append(e.toJSON());
                builder.append(",");
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("password") != null) {
            response.setContentType("text/plain"); // it should be application/json but it won't work otherwise...
            response.setStatus(HttpServletResponse.SC_OK);
            String res = "";
            String cmd = request.getParameter("cmd");
            String id = request.getParameter("id");
            String json = request.getParameter("json");
            if (cmd != null && cmd.equals("list")) {
                res = getExperimentsList();
            } else if (cmd != null && cmd.equals("delete") && id != null) {
                res = deleteExperiment(new Long(id));
            } else if (cmd != null && cmd.equals("listSources")) {
                res = getSourcesList();
            } else if (cmd != null && cmd.equals("listModels")) {
                res = getModelsList();
            } else if (cmd != null && cmd.equals("create") && json != null) {
                res = createExperiment(json);
            } else if (cmd != null && cmd.equals("run") && id != null) {
                res = runExperiment(id);
            } else if (cmd != null && cmd.equals("listExecuted")) {
                res = getExecutedExperimentsList();
            } else {
                res = "{\"error\": \"bad command\"}";
            }
            response.getWriter().print(res);
        } else {
            response.sendRedirect("/login");
        }
    }
}
