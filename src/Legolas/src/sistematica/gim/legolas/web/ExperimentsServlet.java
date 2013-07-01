package sistematica.gim.legolas.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet per la gestione degli esperimenti.
 */
public class ExperimentsServlet extends VelocityServlet {

    public ExperimentsServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler, "experiments.html");
    }

    @Override
    protected void setContext(VelocityContext context) {
        context.put("configPeriodMinutes", configuration.PERIOD_MINUTES);
    }
}
