/**
 * 
 */
package sistematica.sintelimporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import sistematica.apptemplate.Template;
import sistematica.apptemplate.cfg.BaseSettings;
import sistematica.sintelimporter.data.Message;
import sistematica.sintelimporter.data.MoviTraff;
import sistematica.sintelimporter.lib.ConnectionManager;
import sistematica.sintelimporter.settings.Keys;
import sistematica.sintelimporter.utils.FileUtils;

/**
 * @author gsilvestri
 *
 */
public class SintelImporter extends Template
{
	Logger log4j = Logger.getLogger(SintelImporter.class);
	
	private ConnectionManager mgr = null;
	@Override
	public void cleanup()
	{
		if(this.mgr != null)
		{
			if(this.mgr.socketChannel.socket != null)
			{
				try
				{
					log4j.info("CLEAN UP...");
					this.mgr.socketChannel.socket.close();
					log4j.info("Socket closed..");
				}
				catch (IOException e)
				{
					log4j.error(e,e);
				}
			}
		}
		log4j.info("===============================");
		log4j.info(" Sintel Importer... TERMINATED ");
		log4j.info("===============================");	
	}

	@Override
	public Class<? extends BaseSettings> getSettingsClass()
	{
		return Keys.class;
	}

	@Override
	public void mainLoop(String... arg0)
	{
		log4j.info("======================");
		log4j.info(" Sintel Importer v0.1 ");
		log4j.info("======================");
		
		log4j.info("Checking paths...");
		
		FileUtils.makeDir(Keys.ARCHIVE_XML_DIR);

		log4j.info("Ok, paths checked.");
		
		Long start = 0L;
		boolean stop = false;
		ConnectionManager mgr = null;
		String confFile = Keys.CONFIGURATION_FILE;
		
		try
		{
			ArrayList<MoviTraff> movitraffList = getMovitraffConfigured(confFile);
			
			if(movitraffList != null && movitraffList.size() > 0)
			{
				while(!stop)
				{
					if(System.currentTimeMillis() - start > (Keys.POLLING_TIME_MIN * 60000) )
					{
						for(MoviTraff moviTraff:movitraffList)
						{
							try
							{
								mgr = new ConnectionManager(moviTraff.ip, moviTraff.port, moviTraff.tag);
								this.mgr = mgr;
//								mgr.sendMessage(Message.TYPE_CLEAR_DATA);
								mgr.sendMessage(Message.TYPE_GET_DATA);
								Thread.sleep(100);
							}
							catch(Exception e)
							{
								log4j.error(e,e);
							}

						}
						
						start = System.currentTimeMillis();
					}
					else
					{
						Thread.sleep(100);
					}
				}
			}
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		
		
//		SocketChannel sk = new SocketChannel();
//		sk.encodeMsg(Message.getMessage(Message.TYPE_GET_PRODUCT_INFORMATION).getBytes());
		
	}

	private ArrayList<MoviTraff> getMovitraffConfigured(String confFile)
	{
		ArrayList<MoviTraff> list = null;
		Integer movitraffNum = Keys.MOVITRAFF_NUMBER;
		File file = new File(confFile);
		FileInputStream fis = null;
		Properties p = null;
		
//		sintel.movitraff.ip.1 = 94.88.107.135
//		sintel.movitraff.port.1 = 950
//		sintel.movitraff.tag.1 = TEST
		
		String conf_key_ip = "sintel.movitraff.ip.";
		String conf_key_port = "sintel.movitraff.port.";
		String conf_key_tag = "sintel.movitraff.tag.";
		
		String ipTemp = "";
		String portTemp = "";
		String tagTemp = "";
		
		try
		{

			if(movitraffNum > 0)
			{
				fis = new FileInputStream(file);
				p = new Properties(); 
				p.load(fis);
				
				list = new ArrayList<MoviTraff>();
				
				for(int i = 1; i <= movitraffNum; i++)
				{
					ipTemp = p.getProperty(conf_key_ip + i);
					portTemp = p.getProperty(conf_key_port + i);
					tagTemp = p.getProperty(conf_key_tag + i);
					
					if(ipTemp != null && ipTemp.length() > 0 && portTemp != null && portTemp.length() > 0 && tagTemp != null && tagTemp.length() > 0)
					{
						list.add(new MoviTraff(ipTemp, Integer.parseInt(portTemp), tagTemp));
						log4j.info("Properties loaded correctly for the MoviTraff " + tagTemp + ": IP = " + ipTemp + " PORT = " + portTemp);
					}
					
				}
			}
		}
		catch(Exception e)
		{
			log4j.error(e,e);
			return null;
		}
		finally
		{
			try
			{
				if(fis != null)
					fis.close();
			}
			catch (IOException e)
			{
				log4j.error(e,e);
			}
		}

		return list;
	}
	
}
