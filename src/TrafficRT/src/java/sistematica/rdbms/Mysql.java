package sistematica.rdbms;

import java.sql.Timestamp;

public class Mysql implements Rdbms
{
	public static final String JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";

	/**
	 * Restituisce l'istruzione per la data attuale
	 */
	public String getCurrDate()
	{
		return " NOW() ";
	}

	/**
	 * Parametri: 1 - sql: l'istruzione completa su cui deve essere applicata la
	 * condizione LIMIT 2 - upperlimit: il limite superiore
	 * 
	 * 3 - lowerlimit: il limite inferiore
	 */
	public String limitTo(String sql, int upperlimit, int lowerlimit)
	{
		String mysqlSql = null;

		int numberOfRow = (upperlimit - lowerlimit);

		mysqlSql = sql + " LIMIT " + lowerlimit + ", " + numberOfRow;

		return mysqlSql;
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
		int numberOfRow = (upperlimit - lowerlimit);

		String mysqlSql = sql + " " + order + " LIMIT " + lowerlimit + ", " + numberOfRow;

		return mysqlSql;
	}

	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit, String fields)
	{
		return null;
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
		if (javaformat == null)
			javaformat = "yyyy-MM-dd HH:mm:ss";
		String mysqlDate = null;
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(javaformat);
		mysqlDate = dateFormat.format(date);

		String mysqlFormat = javaformat;

		if (mysqlFormat.indexOf("yyyy") != -1)
		{
			mysqlFormat = mysqlFormat.replace("yyyy", "%Y");
		}
		if (mysqlFormat.indexOf("MM") != -1)
		{
			mysqlFormat = mysqlFormat.replace("MM", "%m");
		}
		if (mysqlFormat.indexOf("dd") != -1)
		{
			mysqlFormat = mysqlFormat.replace("dd", "%d");
		}
		if (mysqlFormat.indexOf("HH") != -1)
		{
			mysqlFormat = mysqlFormat.replace("HH", "%H");
		}
		if (mysqlFormat.indexOf("mm") != -1)
		{
			mysqlFormat = mysqlFormat.replace("mm", "%i");
		}
		if (mysqlFormat.indexOf("ss") != -1)
		{
			mysqlFormat = mysqlFormat.replace("ss", "%s");
		}

		mysqlDate = " str_to_date(\'" + mysqlDate + "\', \'" + mysqlFormat + "\') ";
		// mysqlDate = "\'"+mysqlDate+"\'";

		return mysqlDate;
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
		String sqlDiff = " SUBDATE(" + date + ", INTERVAL " + value + " " + intervalType + ") ";
		return sqlDiff;
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
		String mysqlFormat = format;
		if (mysqlFormat.indexOf("YYYY") != -1)
		{
			mysqlFormat = mysqlFormat.replace("YYYY", "%Y");
		}
		if (mysqlFormat.indexOf("MM") != -1)
		{
			mysqlFormat = mysqlFormat.replace("MM", "%m");
		}
		if (mysqlFormat.indexOf("dd") != -1)
		{
			mysqlFormat.replace("dd", "%d");
		}
		if (mysqlFormat.indexOf("HH") != -1)
		{
			mysqlFormat = mysqlFormat.replace("HH", "%H");
		}
		if (mysqlFormat.indexOf("mm") != -1)
		{
			mysqlFormat = mysqlFormat.replace("mm", "%i");
		}
		if (mysqlFormat.indexOf("ss") != -1)
		{
			mysqlFormat = mysqlFormat.replace("ss", "%s");
		}

		String result = " DATE_FORMAT(" + column + ", " + mysqlFormat + ") ";
		return result;
	}

	/**
	 * Restituisce l'espressione associata alla funzione count
	 */
	public String countFunc()
	{
		String count = " count";
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
			result = " CONCAT(";
			for (int i = 0; i < args.length; i++)
			{
				result += args[i];
				if (i != args.length - 1)
					result += ",";
			}
			result += ") ";
		}
		return result;
	}
}
