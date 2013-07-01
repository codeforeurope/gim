/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sistematica.sinottici;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 * @author Manuel
 */
public class BaseDAO {


     public int NOT_SET = -1;

    private static final Logger log = Logger.getLogger(BaseDAO.class);

    protected void close(Object o)
    {
        if (o != null)
        {
            try
            {
                if (o instanceof ResultSet)
                    ((ResultSet) o).close();
                else if (o instanceof Statement)
                    ((Statement) o).close();
                else if (o instanceof PreparedStatement)
                    ((PreparedStatement) o).close();
                else if (o instanceof CallableStatement)
                    ((CallableStatement) o).close();
                else if (o instanceof Connection)
                    throw new RuntimeException("Cannot close a " + o.getClass().getName());
                else
                    throw new RuntimeException("Cannot close a " + o.getClass().getName());
            }
            catch (SQLException e)
            {
                log.warn(e.getMessage());
            }
        }
    }

    protected Timestamp getStartDate(int timeRange)
    {
        Calendar c = Calendar.getInstance();
        long now = System.currentTimeMillis();
        int offset = c.getTimeZone().getOffset(now);
        if (log.isDebugEnabled())
            log.debug(String.format("TimeZone offset is : %d", offset / 60000));
        now -= offset;

        Timestamp startDate = new Timestamp(now - timeRange * 60 * 1000);
        if (log.isDebugEnabled())
            log.debug(String.format("Start Date is : %s", startDate));

        return startDate;
    }

    protected String doubleApexes(String s)
    {
        if (s == null)
            return null;

        StringBuilder buff = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            buff.append(c);
            if (c == '\'')
                buff.append('\'');
        }

        return buff.toString();
    }

    protected String isNullReplace(String s)
    {
        return isNullReplace(s, "");
    }

    protected String isNullReplace(String s, String r)
    {
        if (s == null)
            return r;
        return s;
    }

    /**
     * Sostituisce i punti interrogativi delle query per i PreparedStatement con
     * i valori a cui fanno effettivamente riferimento.
     *
     * @param query la query coi punti interrogativi del PreparedStatement
     * @param values i valori dei parametri da sostituire ai punti interrogativi
     * @return la query con i valori effettivi dei parametri
     */
    protected String formatQuery(String query, Object... values)
    {
        String ret = query;

        if (values != null)
        {
            for (Object o : values)
                if (o == null)
                    ret = ret.replaceFirst("\\?", "NULL");
                else if (o.getClass() == String.class)
                    ret = ret.replaceFirst("\\?", "'" + o.toString() + "'");
                else if (o.getClass() == Timestamp.class || o.getClass() == Date.class)
                    ret = ret.replaceFirst("\\?", "to_date('" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(o) + "','dd-mm-yyyy hh24:mi:ss')");
                else
                    ret = ret.replaceFirst("\\?", o.toString());
        }
        return ret;
    }

}
