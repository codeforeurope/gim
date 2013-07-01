package sistematica.gim.legolas.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet che mostra i job in esecuzione e permette di schedulare nuovi
 * job storici.
 */
public class JobsServlet extends VelocityServlet {

    public JobsServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler, "jobs.html");
    }

    @Override
    protected void setContext(VelocityContext context) {
        context.put("configPeriodMinutes", configuration.PERIOD_MINUTES);
    }
}
