package sistematica.gim.legolas.scheduler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Un {@link Job} è composto di più task.
 */
public abstract class Task extends sistematica.pbutils.DAO {
    
    protected JobDescriptor descriptor;

    /**
     * Crea una nuova istanza di Task.
     * 
     * @param descriptor i parametri del job di cui fa parte il task
     * @param connection la connessione al DB
     * @throws SQLException se c'è un errore nelle query o nella connessione al DB
     */
    public Task(JobDescriptor descriptor, Connection connection) throws SQLException {
        super(connection);
        this.descriptor = descriptor;
    }
}
