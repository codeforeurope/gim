/**
 * 
 */
package sistematica.rdbms;

import java.sql.Timestamp;

/**
 * @author
 * 
 */
public interface Rdbms
{

	/**
	 * Restituisce la data corrente
	 * 
	 * @return
	 */
	public String getCurrDate();

	/**
	 * Restituisce istruzione sql limitata sul numero di colonne richieste
	 * 
	 * @return
	 */
	public String limitTo(String sql, int upperlimit, int lowerlimit);

	/**
	 * Restituisce istruzione sql relativa alla gestione delle date
	 * 
	 * @return
	 */
	/**
	 * limit with order
	 */
	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit);

	/**
	 * limit with order plus fields list
	 * 
	 * @param sql
	 * @param order
	 * @param upperlimit
	 * @param lowerlimit
	 * @param fields
	 * @return
	 */
	public String limitToOrder(String sql, String order, int upperlimit, int lowerlimit, String fields);

	/**
	 * Restituisce istruzione sql relativa alla gestione delle date
	 * 
	 * @return
	 */
	public String toSqlDate(Timestamp date, String javaformat);

	/**
	 * Restituisce la differenza tra la data attuale e un intervallo temporale
	 * 
	 * @return
	 */
	public String subDateFromNow(String date, int value, String intervalType);

	/**
	 * Formatta la data contenuta in un campo del database
	 */
	public String dateFormat(String column, String format);

	/**
	 * Funzione count
	 */
	public String countFunc();

	/**
	 * Funzione di concatenazione
	 */
	public String concat(String[] args);
}
