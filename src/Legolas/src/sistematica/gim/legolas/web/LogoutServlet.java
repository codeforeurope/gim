package sistematica.gim.legolas.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet che effettua il logout.
 */
public class LogoutServlet extends BaseServlet {

    public LogoutServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession(true).removeAttribute("password");
        response.sendRedirect("/login");
    }
}
