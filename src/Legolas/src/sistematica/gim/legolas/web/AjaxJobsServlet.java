package sistematica.gim.legolas.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobDescriptorFactory;
import sistematica.gim.legolas.scheduler.JobType;
import sistematica.gim.legolas.scheduler.Scheduler;
import sistematica.pbutils.DateTime;
import sistematica.pbutils.FormatLogger;

/**
 * Servlet richiamata via Ajax dalla pagina dei job che restituisce la
 * lista dei job in esecuzione.
 */
public class AjaxJobsServlet extends BaseServlet {

    private static final FormatLogger logger = FormatLogger.getLogger(AjaxExperimentsServlet.class);
    private JobDescriptorFactory jobDescriptorFactory;

    public AjaxJobsServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler, JobDescriptorFactory jobDescriptorFactory) {
        super(configuration, velocity, scheduler);
        this.jobDescriptorFactory = jobDescriptorFactory;
    }

    /**
     * Esegue una serie di job storici nell'intervallo indicato.
     * 
     * @param fromParam data d'inizio (inclusa)
     * @param toParam data di fine (inclusa)
     * @return un feedback sul risultato dell'elaborazione in formato JSON
     */
    private String runJob(String fromParam, String toParam) {
        String res = "{}";

        try {
            Date from = WebServer.DATE_TIME_FORMAT.parse(fromParam);
            Date to = WebServer.DATE_TIME_FORMAT.parse(toParam);

            while (from.getTime() <= to.getTime()) {
                try {
                    JobDescriptor descriptor = jobDescriptorFactory.newJob(JobType.HISTORIC, from);
                    scheduler.schedule(descriptor);
                } catch (Exception ex) {
                    res = String.format("{\"error\": \"unable to schedule job %s\"}", WebServer.DATE_TIME_FORMAT.format(from));
                    logger.error("Cannot schedule job", ex);
                    break;
                }

                // Se non aggiungessi un minuto andrei in loop (vedi la funzione di DateTime)
                from = new Date(from.getTime() + 60 * 1000);
                from = DateTime.nextDateWithMinutesDivisibleBy(from, configuration.PERIOD_MINUTES);
            }
        } catch (ParseException ex) {
            res = "{\"error\": \"bad date format\"}";
            logger.error("Cannot schedule job", ex);
        } catch (NullPointerException ex) {
            res = "{\"error\": \"bad date format\"}";
            logger.error("Cannot schedule job", ex);
        }

        return res;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("password") != null) {
            response.setContentType("text/plain"); // it should be application/json but it won't work otherwise...
            response.setStatus(HttpServletResponse.SC_OK);
            String res = "";
            String cmd = request.getParameter("cmd");
            if (cmd != null && cmd.equals("run")) {
                res = runJob(request.getParameter("from"), request.getParameter("to"));
            } else if (cmd != null && cmd.equals("list")) {
                res = scheduler.toJSON();
            } else {
                res = "{\"error\": \"bad command\"}";
            }
            response.getWriter().print(res);
        } else {
            response.sendRedirect("/login");
        }
    }
}
