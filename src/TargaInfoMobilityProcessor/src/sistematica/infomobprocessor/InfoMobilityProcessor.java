/**
 * 
 */
package sistematica.infomobprocessor;

import java.io.File;
import java.util.Timer;

import org.apache.log4j.Logger;

import sistematica.apptemplate.Template;
import sistematica.apptemplate.cfg.BaseSettings;
import sistematica.infomobprocessor.lib.FtpDataExporter;
import sistematica.infomobprocessor.lib.RawDataProcessor;
import sistematica.infomobprocessor.settings.Keys;
import sistematica.infomobprocessor.utils.FileUtils;
import sistematica.infomobprocessor.utils.Loader;
import sistematica.infomobprocessor.utils.MetricTask;
import visualtrack.logger.client.thread.VTLoggerThreadPool;

/**
 * @author gsilvestri
 * 
 */
public class InfoMobilityProcessor extends Template
{
	public static Logger m_log4j = Logger.getLogger(InfoMobilityProcessor.class);
	 
	@Override
	public void cleanup()
	{
		if(!Keys.FTP_EXPORTER_DEL_SERVER_FILE_ENABLE  && Keys.FTP_EXPORTER_ENABLE)
		{
			m_log4j.info("=======================================");
			m_log4j.info(" InfoMobility Processor... CLEAN UP... ");
			m_log4j.info("=======================================");
			
			Loader loader = Loader.getInstance();
//			loader.flushFilesExportedList();
			loader.flushLastFileExported();
		}
		
		if (Keys.SENDMGR_ENABLE)
		{
			VTLoggerThreadPool.terminateExecutorService(false);
		}
		
		m_log4j.info("=======================================");
		m_log4j.info(" InfoMobility Processor... TERMINATED. ");
		m_log4j.info("=======================================");		
	}

	@Override
	public Class<? extends BaseSettings> getSettingsClass()
	{
		return Keys.class;
	}

	@Override
	public void mainLoop(String... arg0)
	{
		m_log4j.info("===================================");
		m_log4j.info(" Targa InfoMobility Processor v0.3 ");
		m_log4j.info("===================================");
		
		try
		{	
			m_log4j.info("Checking paths...");
			
			FileUtils.makeDir(Keys.FTP_EXPORTER_ARCHIVE_DIR);
			FileUtils.makeDir(Keys.FTP_EXPORTER_ARCHIVE_DIR + File.separatorChar + Keys.RAWDATA_PROCESSOR_OK_DIR);
			FileUtils.makeDir(Keys.FTP_EXPORTER_ARCHIVE_DIR + File.separatorChar + Keys.RAWDATA_PROCESSOR_ERROR_DIR);
			FileUtils.makeDir(Keys.FTP_EXPORTER_ARCHIVE_DIR + File.separatorChar + Keys.RAWDATA_PROCESSOR_EMPTY_DIR);

			m_log4j.info("Ok, paths checked.");
			
			if(!Keys.FTP_EXPORTER_DEL_SERVER_FILE_ENABLE && Keys.FTP_EXPORTER_ENABLE)
			{
				Loader loader = Loader.getInstance();
//				loader.loadFileExportedFromFile();
				loader.getLastFileExportedFromFile();
			}
			
			FtpDataExporter ftpExp = null;
			if (Keys.FTP_EXPORTER_ENABLE)
			{
				ftpExp = new FtpDataExporter();
				ftpExp.start();
			}
			
			RawDataProcessor processor = null;
			if (Keys.RAWDATA_PROCESSOR_ENABLE)
			{
				processor = new RawDataProcessor();
				processor.start();
			}
			
			Timer timer = null;
			MetricTask metricTask = null;
			if (Keys.SENDMGR_ENABLE)
			{
				timer = new Timer();
	        	metricTask = new MetricTask();
	        	timer.schedule(metricTask, 60000 , Keys.SENDMGR_TIME_MIN*60000);
	        	m_log4j.info("Start timer MetricTask ...");
			}
			
			if (Keys.FTP_EXPORTER_ENABLE)
			{
				ftpExp.join();
			}
			
			if (Keys.RAWDATA_PROCESSOR_ENABLE)
			{
				processor.join();
			}
			
			
		}
		catch (Exception e)
		{
			m_log4j.error(e,e);
		}
		
	}
	
}
