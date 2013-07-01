package sistematica.gim.legolas.production;

import java.sql.SQLException;
import java.util.Date;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.algorithm.DensityModel;
import sistematica.gim.legolas.entity.Streets;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobDescriptorFactory;
import sistematica.gim.legolas.scheduler.JobType;

/**
 * Facilita la creazione dei descrittori dei job di tipo storico e real-time.
 */
public class ProductionJobDescriptorFactory extends JobDescriptorFactory {

    private Streets streets;
    
    /**
     * Factory per la creazione semplificata dei job descriptor per job
     * real-time o storici (vedi {@link JobType}).
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param streets le strade censite
     * @param metrics le metriche del programma
     */
    public ProductionJobDescriptorFactory(Configuration configuration, RoadGraph roadGraph, Streets streets, RuntimeMetrics metrics) {
        super(configuration, roadGraph, metrics);
        this.streets = streets;
    }

    /**
     * Crea un nuovo {@link JobDescriptor}.
     * @param type il tipo di job (può essere {@link JobType.HISTORIC} o {@link JobType.REAL_TIME})
     * @param args in questo caso deve essere soltanto un argomento di tipo {@link Date}.
     * @return il job descriptor
     * @throws SQLException se c'è stato un errore nell'interazione col DB
     * @throws IllegalArgumentException se è stato specificato un tipo di job non supportato
     */
    @Override
    public JobDescriptor newJob(JobType type, Object... args) throws SQLException {
        switch (type) {
            case HISTORIC:
            case REAL_TIME:
                Date timestamp = (Date) args[0];
                return new JobDescriptor(
                        configuration, 
                        roadGraph, 
                        streets,
                        metrics, 
                        type, 
                        timestamp, 
                        null, 
                        DensityModel.fromString(configuration.TRAFFIC_MODEL), 
                        ProductionOutputTask.class);
            default:
                throw new IllegalArgumentException("This factory cannot create jobs of type " + type);
        }
    }
}
