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
 * Servlet da utilizzarsi per il rendering delle pagine con Velocity.
 * Garantisce che la pagina web gestita dalla servlet è accessibile soltanto se
 * nella sessione c'è l'attributo <b>password</b> ed inserisce nel
 * VelocityContext l'attributo booleano <b>experimental</b> che consente di
 * determinare la modalità con cui Legolas è in esecuzione.
 * 
 * Questa classe può essere usata direttamente o, se i template hanno bisogno
 * di ulteriori informazioni nel VelocityContext, ereditandola e ridefinendo
 * il metodo {@link VelocityServlet#setContext(org.apache.velocity.VelocityContext)}.
 */
public class VelocityServlet extends BaseServlet {

    private String template;

    public VelocityServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler, String template) {
        super(configuration, velocity, scheduler);
        this.template = template;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("password") != null) {
            VelocityContext context = new VelocityContext();
            context.put("password", session.getAttribute("password"));
            context.put("experimental", session.getAttribute("experimental"));
            setContext(context);
            renderTemplate(template, context, response);
        } else {
            response.sendRedirect("/login");
        }
    }

    protected void setContext(VelocityContext context) {
        // This is meant for subclasses that must expand the velocity context
    }
}
