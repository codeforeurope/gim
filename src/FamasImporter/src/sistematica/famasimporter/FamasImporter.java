/**
 * 
 */
package sistematica.famasimporter;

import java.io.File;

import org.apache.log4j.Logger;

import sistematica.apptemplate.Template;
import sistematica.apptemplate.cfg.BaseSettings;
import sistematica.famasimporter.lib.WebServiceImporter;
import sistematica.famasimporter.settings.Keys;
import sistematica.famasimporter.utils.FileUtils;

/**
 * @author gsilvestri
 *
 */
public class FamasImporter extends Template
{
	public static Logger log4j = Logger.getLogger(FamasImporter.class);
			
	@Override
	public void cleanup()
	{
		log4j.info("==============================");
		log4j.info(" Famas Importer... TERMINATED ");
		log4j.info("==============================");	
	}

	@Override
	public Class<? extends BaseSettings> getSettingsClass()
	{
		return Keys.class;
	}

	@Override
	public void mainLoop(String... arg0)
	{
		log4j.info("=====================");
		log4j.info(" Famas Importer v0.2 ");
		log4j.info("=====================");
		
		log4j.info("Checking paths...");
		
		FileUtils.makeDir(Keys.WS_ARCHIVE_BASE_DIR);
		FileUtils.makeDir(Keys.WS_ARCHIVE_BASE_DIR + File.separatorChar + Keys.WS_AGGREGATI_ARCHIVE_DIR);
		FileUtils.makeDir(Keys.WS_ARCHIVE_BASE_DIR + File.separatorChar + Keys.WS_VEICOLARI_ARCHIVE_DIR);

		log4j.info("Ok, paths checked.");
		
		WebServiceImporter importer = new WebServiceImporter();
		importer.start();
		
		try
		{
			importer.join();
		}
		catch (InterruptedException e)
		{
			log4j.error(e,e);
		}
	}

}
