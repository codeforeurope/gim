package sistematica.gim.sisasdiss.conf;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Riempie i campi di un oggetto, discendente da BaseSettings, leggendoli da un
 * file di properties.
 */
public class Configuration
{
    private static final Logger log = Logger.getLogger(Configuration.class);

    public static void init(Class<? extends BaseSettings> c, String cfgFile) throws Exception
    {
        log.info("Loading configuration from file: " + cfgFile);
        Properties props = loadProps(cfgFile);
        load(c, props);
    }

    public static void init(Class<? extends BaseSettings> c, InputStream is) throws Exception
    {
        log.info("Loading configuration from InputStream. ");
        Properties props = new Properties();
        props.load(is);
        load(c, props);
    }

    private static void load(Class<? extends BaseSettings> settingsClass, Properties props) throws Exception
    {
        Field[] fields = settingsClass.getFields();
        List<String> msgs = populateFields(fields, props);

        BaseSettings.props = props;
        BaseSettings.msgs = msgs;
    }

    private static List<String> populateFields(Field[] fields, Properties props) throws Exception
    {
        List<String> msgs = new ArrayList<String>(fields.length / 2);

        for (int k = 0; k < fields.length; k++)
        {
            if (fields[k].getName().startsWith("KEY_"))
            {
                String key = (String) fields[k].get(null);
                if (key == null || key.length() == 0)
                    continue;

                String name = fields[k].getName().substring(4).trim();

                boolean found = false;
                Object oldValue = null;
                for (int i = 0; i < fields.length; i++)
                {
                    if (fields[i].getName().equalsIgnoreCase(name))
                    {
                        oldValue = fields[i].get(null);
                        String sVal = props.getProperty(key);
                        if (sVal != null)
                        {
                            Object val = sVal;
                            @SuppressWarnings("rawtypes")
                            Class type = fields[i].getType();
                            try
                            {
                                if (type.equals(Integer.class))
                                    val = Integer.parseInt(sVal.trim());
                                else if (type.equals(Long.class))
                                    val = Long.parseLong(sVal.trim());
                                else if (type.equals(Float.class))
                                    val = Float.parseFloat(sVal.trim());
                                else if (type.equals(Double.class))
                                    val = Double.parseDouble(sVal.trim());
                                else if (type.equals(Boolean.class))
                                    val = Boolean.parseBoolean(sVal.trim());
                            }
                            catch (Exception e)
                            {
                                throw new ConfigurationPropertyExeception("Parsing property " + key + " of type " + type, e);
                            }

                            fields[i].set(null, val);
                            found = true;
                            msgs.add(StringUtils.rightPad(key, 40, ".") + " = " + val);
                            break;
                        }
                    }

                }
                if (!found)
                {
                    msgs.add(StringUtils.rightPad(key, 40, ".") + " = NOT SET (default value " + oldValue + ")");
                }
            }
        }

        return msgs;
    }

    private static Properties loadProps(String cfgFile) throws Exception
    {
        FileInputStream is = null;
        try
        {
            is = new FileInputStream(cfgFile);
            Properties props = new Properties();
            props.load(is);

            return props;
        }
        finally
        {
            if (is != null)
                try
                {
                    is.close();
                }
                catch (Exception e)
                {
                }
        }
    }

    public Properties getProperties()
    {
        return null;
    }
}
