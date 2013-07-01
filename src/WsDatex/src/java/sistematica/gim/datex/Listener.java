package sistematica.gim.datex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.PropertyConfigurator;

public class Listener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        InputStream input = null;
        Properties properties = new Properties();

        try {
            String path = "/WEB-INF/datex.properties";
            input = context.getResourceAsStream(path);
            properties.load(input);
            PropertyConfigurator.configure(properties);
            Configuration.getInstance().load(properties);
        } catch (Exception ex) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
