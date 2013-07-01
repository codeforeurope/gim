/**
 * 
 */
package sistematica.gim.sisasdiss.conf;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BaseSettings
{
	public static Logger log4j = Logger.getLogger(BaseSettings.class);

	static Properties props = null;

	static List<String> msgs;

	public static Properties getProperties()
	{
		return props;
	}

	public static void print()
	{
		if (msgs == null)
			return;

		for (String msg : msgs)
			log4j.info(msg);
	}
}
