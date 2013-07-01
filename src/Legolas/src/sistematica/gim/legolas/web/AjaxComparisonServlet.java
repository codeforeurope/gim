package sistematica.gim.legolas.web;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.experiments.ExperimentsComparison;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet per le comunicazioni Ajax della pagina di confronto degli
 * esperimenti. A differenza delle altre servlet Ajax che restituiscono JSON
 * questa restituisce HTML.
 */
public class AjaxComparisonServlet extends BaseServlet {

    private Connection connection;
    private Template template = velocity.getTemplate("comparison-box.html");

    /**
     * Crea una nuova istanza di AjaxComparisonServlet.
     * 
     * @param configuration la configurazione del programma
     * @param velocity la template engine
     * @param scheduler lo scheduler di esecuzione dei job
     * @param connection la connessione al DB
     */
    public AjaxComparisonServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler, Connection connection) {
        super(configuration, velocity, scheduler);
        this.connection = connection;
    }

    /**
     * Restituisce la rappresentazione HTML di un {@link ExperimentComparison}.
     * 
     * @param comparison la comparazione da trasformare in HTML
     * @return l'HTML che rappresenta la comparazione
     */
    private String renderComparison(ExperimentsComparison comparison) {
        VelocityContext context = new VelocityContext();
        context.put("comparison", comparison);
        StringWriter writer = new StringWriter();
        template = velocity.getTemplate("comparison-box.html"); // TODO: per debugging veloce... poi va tolta!
        template.merge(context, writer);
        return writer.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("password") != null) {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            String res = "";
            String cmd = request.getParameter("cmd");
            String a = request.getParameter("a");
            String b = request.getParameter("b");
            if (cmd != null && cmd.equals("compare")) {
                long idA = Long.parseLong(a);
                long idB = Long.parseLong(b);
                try {
                    ExperimentsComparison comparison = new ExperimentsComparison(configuration, connection, idA, idB);
                    res = renderComparison(comparison);
                } catch (SQLException ex) {
                    res = "<strong>Error:</strong> database error";
                }
            } else {
                res = "<strong>Error:</strong> bad command";
            }
            response.getWriter().print(res);
        } else {
            response.sendRedirect("/login");
        }
    }
}
