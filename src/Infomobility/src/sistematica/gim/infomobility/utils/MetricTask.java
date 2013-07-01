/**
 * 
 */
package sistematica.gim.infomobility.utils;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import sistematica.gim.infomobility.conf.WebSettings;
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
		long files = 0L;
		
		try
		{
			loader = Loader.getInstance();
			
			positions = loader.getPositions();
			files = loader.getFiles();
			
            VTLoggerThreadPool.init(5,30000);

            VTLoggerMetric vtmlog = new VTLoggerMetric(WebSettings.VTLOGGER_METRIC_URL, true);
            
            log4j.info("---------------------------");
            log4j.info("Sending metrics...");
            vtmlog.sendValue(WebSettings.SENDMGR_TAG_STATUS, "1", System.currentTimeMillis());
            vtmlog.sendValue(WebSettings.SENDMGR_TAG_NUMPOS, Long.toString(positions), System.currentTimeMillis());
            vtmlog.sendValue(WebSettings.SENDMGR_TAG_NUMFILE, Long.toString(files), System.currentTimeMillis());
            log4j.info("---------------------------");
            
			loader.resetFiles();
			loader.resetPositions();
			loader.updateTimestampLastRun();
		
		}
		catch (Exception e)
		{
			log4j.error(e,e);
		}
	
	}
}
