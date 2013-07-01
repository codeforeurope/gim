package sistematica.gim.legolas.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet richiamata via Ajax dalla pagina del monitoring per avere
 * le metriche in tempo reale.
 */
public class AjaxMetricsServlet extends BaseServlet {

    public AjaxMetricsServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("password") != null) {
            response.setContentType("text/plain"); // it should be application/json but it won't work otherwise...
            response.setStatus(HttpServletResponse.SC_OK);
            String metrics = scheduler.getMetrics().toJSON();
            response.getWriter().print(String.format("{\"metrics\": %s,}", metrics));
        } else {
            response.sendRedirect("/login");
        }
    }
}
