package sistematica.rdbms;

import java.sql.Timestamp;

public class Postgres implements Rdbms
{

	public static final String JDBC_DRIVER_CLASS = "org.postgresql.Driver";

	public String getCurrDate()
	{
		return " LOCALTIMESTAMP ";
	}

	public String limitTo(String sql, int upperlimit, int lowerlimit)
	{
		String postSql = null;

		int limitPost = upperlimit + lowerlimit;

		postSql = postSql + " LIMIT " + " OFFSET " + lowerlimit;

		return postSql;
	}

	/**
	 * limit più order
	 */
	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit)
	{
		return null;
	}

	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit, String fields)
	{
		return null;
	}

	public String toSqlDate(Timestamp date, String javaformat)
	{
		String postgresDate = null;
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(javaformat);
		postgresDate = dateFormat.format(date);

		String postgresFormat = javaformat;
		if (postgresFormat.indexOf("HH") != -1)
		{
			postgresFormat = postgresFormat.replace("HH", "HH24");
		}
		if (postgresFormat.indexOf("hh") != -1)
		{
			postgresFormat = postgresFormat.replace("hh", "HH");
		}
		if (postgresFormat.indexOf("mm") != -1)
		{
			postgresFormat = postgresDate.replace("mm", "mi");
		}

		postgresDate = " to_timestamp(\'" + postgresDate + "\', \'" + postgresFormat + "\') ";

		return postgresDate;
	}

	public String subDateFromNow(String date, int value, String intervalType)
	{
		return null;
	}

	/**
	 * formatta la data contenuta in una campo del database
	 */
	public String dateFormat(String column, String format)
	{
		return null;
	}

	/**
	 * Funzione count
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

		return result;
	}
}
