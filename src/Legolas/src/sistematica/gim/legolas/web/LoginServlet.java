package sistematica.gim.legolas.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet che gestisce il login. Se chiamata via GET restituisce la pagina
 * di login; se chiamata via POST effettua il controllo della password
 * ed eventualmente autentica la sessione.
 */
public class LoginServlet extends BaseServlet {

    public LoginServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VelocityContext context = new VelocityContext();
        String message = request.getParameter("again") != null ? "<strong>Wrong password.</strong> Don't you dare trying again!" : null;
        context.put("message", message);
        context.put("experimental", configuration.MODE_EXPERIMENTAL.toString());
        renderTemplate("login.html", context, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String password = request.getParameter("password");
        if (password != null && password.equals(configuration.WEB_PASSWORD)) {
            session.setAttribute("password", password);
            session.setAttribute("experimental", configuration.MODE_EXPERIMENTAL.toString());
            response.sendRedirect("/monitor");
        } else {
            response.sendRedirect("/login?again=y");
        }
    }
}
