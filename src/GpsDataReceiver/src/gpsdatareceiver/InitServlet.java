package gpsdatareceiver;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet
{
    org.apache.log4j.Logger m_log4j;

    public InitServlet()
    {
        m_log4j = org.apache.log4j.Logger.getLogger(this.getClass());
    }

    public void init(ServletConfig config)
    {
        m_log4j.info("INIT");
    }
}
