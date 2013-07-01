/**
 * 
 */
package sistematica.infomobprocessor.utils;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import sistematica.infomobprocessor.settings.Keys;
import visualtrack.logger.client.VTLoggerMetric;
import visualtrack.logger.client.thread.VTLoggerThreadPool;


/**
 * @author gsilvestri
 *
 */
public class MetricTask extends TimerTask
{

	Logger log4j = Logger.getLogger(MetricTask.class);
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		Loader loader = null;
		
		long positions = 0L;
		long vehicleNumber = 0L;
		
		try
		{
			loader = Loader.getInstance();
			
			positions = loader.getPositions();
			vehicleNumber = loader.getVehicleListSize();
			
            VTLoggerThreadPool.init(5,30000);

//            VTLoggerText vttlog = new VTLoggerText(Keys.VTLOGGER_TEXT_URL, true);
//            vttlog.info(Keys.SENDMGR_TAG_NODE, "Ciao", System.currentTimeMillis());
//            vttlog.warning(Keys.SENDMGR_TAG_NODE, "Ciao", System.currentTimeMillis());

            VTLoggerMetric vtmlog = new VTLoggerMetric(Keys.VTLOGGER_METRIC_URL, true);
            
            log4j.info("---------------------------");
            log4j.info("Sending metrics...");
            vtmlog.sendValue(Keys.SENDMGR_TAG_STATUS, "1", System.currentTimeMillis());
            vtmlog.sendValue(Keys.SENDMGR_TAG_NUMPOS, Long.toString(positions), System.currentTimeMillis());
            vtmlog.sendValue(Keys.SENDMGR_TAG_NUMVEH, Long.toString(vehicleNumber), System.currentTimeMillis());
            log4j.info("---------------------------");
            
//            VTLoggerThreadPool.terminateExecutorService(false);

			loader.clearVehicleList();
			loader.resetPositions();
		
		}
		catch (Exception e)
		{
			log4j.error(e,e);
		}
	
	}
}
