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
 * Servlet che gestisce la pagina del monitoring.
 */
public class MonitorServlet extends VelocityServlet {

    public MonitorServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler, "monitor.html");
    }

    @Override
    protected void setContext(VelocityContext context) {
        context.put("configDbUrl", configuration.DB_URL);
        context.put("configDbDriver", configuration.DB_DRIVER);
        context.put("configDbUser", configuration.DB_USER);

        context.put("configPeriodMinutes", configuration.PERIOD_MINUTES);
        context.put("configMaxInputAgeMinutes", configuration.MAX_INPUT_AGE_MINUTES);
    }
}
