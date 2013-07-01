package sistematica.gim.legolas.web;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet generica di Legolas, con supporto a Velocity.
 */
public abstract class BaseServlet extends HttpServlet {

    protected Configuration configuration;
    protected VelocityEngine velocity;
    protected Scheduler scheduler;

    /**
     * Crea una nuova istanza della servlet.
     * 
     * @param configuration la configurazione del programma
     * @param velocity la velocity engine per il rendering dei template
     */
    public BaseServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        this.configuration = configuration;
        this.velocity = velocity;
        this.scheduler = scheduler;
    }

    /**
     * Effettua il rendering del template Velocity specificato  e lo invia come response.
     * 
     * @param templateName il nome del template (i template sono nella directory www/tpl)
     * @param context il {@link VelocityContext} coi parametri
     * @param response la response della servlet
     * @throws IOException se c'è un errore nella lettura da disco del file con il template
     */
    protected void renderTemplate(String templateName, VelocityContext context, HttpServletResponse response) throws IOException {
        StringWriter writer = new StringWriter();
        Template template = velocity.getTemplate(templateName);
        template.merge(context, writer);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(writer.toString());
    }
}
