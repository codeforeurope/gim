package sistematica.webcontext;

/**
 * <p>Title: IS</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sistematica S.p.A.</p>
 * @author RD
 * @version 1.0
 */
import java.util.Properties;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;




public class StartUpAppl
{
	private static final Logger log = Logger.getLogger(StartUpAppl.class);

	private static Properties m_cfg;
	private static boolean m_isInitialized = false;
	private static final Object m_semaforo = new Object();

	/*
	 * NOT Static Methods: can be called directly by JSF pages
	 */
	public StartUpAppl getInstance()
	{
		return this;
	}

	public Properties getProperties()
	{
		if (m_isInitialized == false)
			throw new RuntimeException("Not initialized");

		return m_cfg;
	}

	public static void init(Properties cfg) throws Exception
	{
		if (m_isInitialized == true)
			return;

		synchronized (m_semaforo)
		{
			if (m_isInitialized == false)
			{
				m_cfg = cfg;
				m_isInitialized = true;
                log.info("INITIALIZED");
			}

		}
	}

	/**
	 * 
	 * @param key String
	 * @return String
	 */
	public static String getProperty(String key)
	{
		return m_cfg.getProperty(key);
	}

	public static String getProperty(String name, String defaultValue)
	{
		return m_cfg.getProperty(name, defaultValue);
	}

    public static void setProperty(String name, String value)
    {
        m_cfg.setProperty(name, value);
    }


	

}
