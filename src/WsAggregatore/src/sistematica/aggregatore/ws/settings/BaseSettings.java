/**
 * 
 */
package sistematica.aggregatore.ws.settings;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author A. Rossini - 23/Marzo/2009
 * 
 */
public class BaseSettings
{
	private static final Logger log = Logger.getLogger(BaseSettings.class);

	static Properties props = null;

	static List<String> msgs;

	
	/*===========================================================================*/
	/*   SISMON 
	/*===========================================================================*/
	public static final String KEY_SISMON_ENABLED  = "sismon.enabled";
	public static Boolean SISMON_ENABLED = false;
	
	public static final String KEY_SISMON_HOST     = "sismon.host";
	public static String SISMON_HOST = "";
	
	public static final String KEY_SISMON_PORT     = "sismon.port";
	public static Integer SISMON_PORT = 5000;
	
	public static final String KEY_SISMON_ALIVE_NOTIFIER_PERIOD = "sismon.alive.notifier.period";
	public static Integer SISMON_ALIVE_NOTIFIER_PERIOD = -1;

	public static Properties getProperties()
	{
		return props;
	}

	public static void print()
	{
		if (msgs == null)
			return;

		for (String msg : msgs)
			System.out.println(msg);
	}
}
