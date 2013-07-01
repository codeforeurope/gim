package sistematica.gim.legolas.experiments;

import java.sql.SQLException;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.entity.RoadGraph;
import sistematica.gim.legolas.metrics.RuntimeMetrics;
import sistematica.gim.legolas.scheduler.JobDescriptor;
import sistematica.gim.legolas.scheduler.JobDescriptorFactory;
import sistematica.gim.legolas.scheduler.JobType;

/**
 * Facilita la creazione dei descrittori dei job di tipo sperimentale.
 */
public class ExperimentalJobDescriptorFactory extends JobDescriptorFactory {

    /**
     * Factory per la creazione semplificata dei job descriptor per job
     * sperimentali (vedi {@link JobType}).
     * 
     * @param configuration la configurazione del programma
     * @param roadGraph il grafo stradale di riferimento
     * @param metrics le metriche del programma
     */
    public ExperimentalJobDescriptorFactory(Configuration configuration, RoadGraph roadGraph, RuntimeMetrics metrics) {
        super(configuration, roadGraph, metrics);
    }

    /**
     * Crea un nuovo {@link JobDescriptor}.
     * @param type il tipo di job (può essere solo {@link JobType.EXPERIMENTAL})
     * @param args in questo caso deve essere soltanto un argomento di tipo {@link Experiment}.
     * @return il job descriptor
     * @throws SQLException se c'è stato un errore nell'interazione col DB
     * @throws IllegalArgumentException se è stato specificato un tipo di job non supportato
     */
    @Override
    public JobDescriptor newJob(JobType type, Object... args) throws SQLException {
        switch (type) {
            case EXPERIMENTAL:
                Experiment experiment = (Experiment) args[0];
                return new ExperimentalJobDescriptor(
                        configuration,
                        roadGraph,
                        metrics,
                        JobType.EXPERIMENTAL,
                        experiment.getTimestamp(),
                        experiment.getSources(),
                        experiment.getDensityModel(),
                        ExperimentalOutputTask.class,
                        experiment);
            default:
                throw new IllegalArgumentException("This factory cannot create jobs of type " + type);
        }
    }
}
