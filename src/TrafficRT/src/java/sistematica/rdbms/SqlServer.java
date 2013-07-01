/**
 * 
 */
package sistematica.rdbms;

import java.sql.Timestamp;

/**
 * @author Alessio
 * 
 */

public class SqlServer implements Rdbms
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see rdbms.Rdbms#getCurrDate()
	 */

	public static final String JDBC_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/**
	 * Restituisce l'istruzione per la data attuale
	 */
	public String getCurrDate()
	{
		// TODO Auto-generated method stub
		return " getDate() ";
	}

	/**
	 * Parametri: 1 - sql: l'istruzione completa su cui deve essere applicata la
	 * condizione LIMIT 2 - upperlimit: il limite superiore
	 * 
	 * 3 - lowerlimit: il limite inferiore
	 */
	public String limitTo(String sql, int upperlimit, int lowerlimit)
	{
		// TODO Auto-generated method stub
		String outcommand = null;
		int index = (sql.toUpperCase()).indexOf("SELECT");
		String restante = sql.substring(index + 6);

		if (index != -1)
		{
			outcommand = "Select top " + upperlimit + restante + " Except select top " + Integer.toString(lowerlimit) + restante;
		}

		return outcommand;
	}

	/**
	 * Parametri: 1 - sql: l'istruzione completa su cui deve essere applicata le
	 * condizioni LIMIT e ORDER BY 2- order: l'istruzione ORDER BY nomeCampo/i
	 * 
	 * 3 - upperlimit: il limite superiore
	 * 
	 * 4 - lowerlimit: il limite inferiore
	 */
	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit)
	{
		String sqlserverSql = "";
		int index = sql.indexOf("FROM");
		if (index == -1)
			index = sql.indexOf("from");

		System.out.println("SEARCH_REPORT index : " + index);
		String head = sql.substring(0, index - 1);
		String back = sql.substring(index + 4);
		if (index != -1)
		{
			sqlserverSql = "with PAGE as ( " + head + ",ROW_NUMBER() OVER ( " + order + " ) as ROWNUMBER " + " FROM " + back + " ) " + head + " FROM PAGE WHERE ROWNUMBER > "
					+ lowerlimit + " and ROWNUMBER <= " + upperlimit + " " + order;
		}

		return sqlserverSql;
	}

	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit, String fields)
	{
		String sqlserverSql = "";
		int index = sql.indexOf("FROM");
		if (index == -1)
			index = sql.indexOf("from");

		System.out.println("SEARCH_REPORT index : " + index);
		String head = sql.substring(0, index - 1);
		String back = sql.substring(index + 4);
		if (index != -1)
		{
			sqlserverSql = "with PAGE as ( " + head + ",ROW_NUMBER() OVER ( " + order + " ) as ROWNUMBER " + " FROM " + back + " ) " + fields + " FROM PAGE WHERE ROWNUMBER > "
					+ lowerlimit + " and ROWNUMBER <= " + upperlimit + " " + order;
		}

		return sqlserverSql;
	}

	/**
	 * Parametri: 1 - date: oggetto Timestamp
	 * 
	 * 2 - javaformat: formato java nel quale il Timestamp deve essere espresso
	 * (YYYY-MM-DD HH-mm-ss)
	 * 
	 */
	public String toSqlDate(Timestamp date, String javaformat)
	{
		String sqlserverDate = null;
		String sqlserverFormat = null;
		sqlserverFormat = javaformat;

		if (javaformat == null || !javaformat.equals("yyyyMMdd HH:mm:ss"))
			;
		{
			javaformat = "yyyyMMdd HH:mm:ss";
		}

		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(javaformat);
		sqlserverDate = dateFormat.format(date);

		sqlserverDate = " CONVERT(datetime, \'" + sqlserverDate + "\') ";

		// TODO Auto-generated method stub
		return sqlserverDate;
	}

	/**
	 * Parametri: 1 - date: Stringa ottenuta applicando il metodo toSqlDate()
	 * 
	 * 2 - value: valore che deve essere sottratto alla data (intero)
	 * 
	 * 3 - intervalType: tipo associato al valore (Es.: minute, day,...)
	 */
	public String subDateFromNow(String date, int value, String intervalType)
	{
		String sqlServerDiff = null;

		sqlServerDiff = " dateadd(" + intervalType + ", -" + value + ", " + date + ")";

		return sqlServerDiff;
	}

	/**
	 * Formatta la data contenuta in un campo del DataBase Paramatri: 1 -
	 * column: colanna contenente date che deve essere opportunamente formattata
	 * 
	 * 2 - format: formato java in cui formattare la colonna(YYYY-MM-DD
	 * HH:mm:ss)
	 * 
	 */
	public String dateFormat(String column, String format)
	{
		String sqlServerFormat = format;
		String result = " DATEPART(" + sqlServerFormat + ", " + column + ") ";

		return null;
	}

	/**
	 * Restituisce l'espressione associata alla funzione count
	 */
	public String countFunc()
	{
		String count = " count_big";
		return count;
	}

	/**
	 * Concatena i risultati di più campi in una sola colanna
	 */
	public String concat(String[] args)
	{
		String result = null;
		if (args != null && args.length > 0)
		{
			result = " ";
			for (int i = 0; i < args.length; i++)
			{
				result += args[i];
				if (i != args.length - 1)
					result += "+";
			}
			result += " ";
		}
		return result;
	}
}
