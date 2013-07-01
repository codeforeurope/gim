/**
 * 
 */
package sistematica.rdbms;

/**
 * @author Alessio
 * 
 */
public class RdbmsFactory
{

	private static RdbmsFactory instance;

	public static final int MY_SQL = 0;
	public static final int SQL_SERVER = 1;
	public static final int ORACLE = 2;

	private Rdbms rdbms = null;

	static
	{
		instance = new RdbmsFactory();
	}

	private RdbmsFactory()
	{
	}

	public static RdbmsFactory getInstance()
	{
		return instance;
	}

	public Rdbms getRdbms(int rdbms) throws RdbmsException
	{
		/*
		 * switch(rdbms) { case MY_SQL: return new MySql(); }
		 */
		return null;
	}

	public Rdbms getRdbms(String jdbcDriverClass) throws RdbmsException
	{
		if (jdbcDriverClass.equals(SqlServer.JDBC_DRIVER_CLASS))
			return new SqlServer();

		if (jdbcDriverClass.equals(Oracle.JDBC_DRIVER_CLASS))
			return new Oracle();

		if (jdbcDriverClass.equals(Mysql.JDBC_DRIVER_CLASS))
			return new Mysql();

		if (jdbcDriverClass.equals(Postgres.JDBC_DRIVER_CLASS))
			return new Postgres();
		else
			throw new RdbmsException("Unknown RDBMS by driver class (" + jdbcDriverClass + ")");

	}

	public Rdbms getRdbmsForDs(String nomeClasseRdbms) throws Exception
	{
		Rdbms r = null;

		if (nomeClasseRdbms != null && nomeClasseRdbms.length() > 0)
			return (Rdbms) Class.forName(nomeClasseRdbms).getConstructor().newInstance();
		else
			throw new Exception("error in RDBMS getRdbmsProp (\"" + nomeClasseRdbms + ") .....");
	}
	// (Rdbms) Class.forName("rdbms.Oracle").getConstructor().newInstance()

}
