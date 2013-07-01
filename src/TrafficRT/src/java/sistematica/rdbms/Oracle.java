/**
 * 
 */
package sistematica.rdbms;

import java.sql.Timestamp;

/**
 * @author Fabrizio
 * 
 */
public class Oracle implements Rdbms
{
	public static final String JDBC_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

	/**
	 * Restituisce l'istruzione per la data attuale
	 */
	public String getCurrDate()
	{
		return " SYSDATE ";
	}

	/**
	 * Parametri: 1 - sql: l'istruzione completa su cui deve essere applicata la
	 * condizione LIMIT 2 - upperlimit: il limite superiore
	 * 
	 * 3 - lowerlimit: il limite inferiore
	 */
	public String limitTo(String sql, int upperlimit, int lowerlimit)
	{
		String oracleSql = null;

		oracleSql = "SELECT * FROM ( " + " SELECT tab.*, rownum id_row FROM ( " + sql + " ) tab WHERE rownum <= " + upperlimit + " ) where id_row > " + lowerlimit;

		return oracleSql;
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
		return null;
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
		String oracleDate = null;
		String oracleFormat = null;
		oracleFormat = javaformat;

		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(javaformat);
		oracleDate = dateFormat.format(date);

		if (javaformat.indexOf("HH") != -1)
		{
			oracleFormat = oracleFormat.replace("HH", "HH24");
		}
		else if (javaformat.indexOf("hh") != -1)
		{
			oracleFormat = oracleFormat.replace("hh", "HH");
		}

		if (javaformat.indexOf("mm") != -1)
		{
			oracleFormat = oracleFormat.replace("mm", "mi");
		}
		String result = " to_date(\'" + oracleDate + "\',\'" + oracleFormat + "\') ";
		return result;
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
		String oracleDiff = date + " - interval \'" + value + "\' " + intervalType + " ";
		return oracleDiff;
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
		return null;
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

		return result;
	}
}
