/**
 * 
 */
package test;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sistematica.infomobprocessor.lib.ParseFileInfoMob;

/**
 * @author gsilvestri
 *
 */
public class TestParseFileInfoMob
{
	public static Logger m_log4j = Logger.getLogger(TestParseFileInfoMob.class);
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		PropertyConfigurator.configure("../cfg/application.properties");
		
		m_log4j.info("======================");
		m_log4j.info(" TestParseFileInfoMob ");
		m_log4j.info("======================");
		
		File file = new File("C:\\TrafficData2011-07-28_01-29-57.txt");
		
		ParseFileInfoMob.parseFile(file);
		
	}

}
