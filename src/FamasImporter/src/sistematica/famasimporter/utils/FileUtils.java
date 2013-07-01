package sistematica.famasimporter.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

public class FileUtils
{
	private static Logger m_log4j = Logger.getLogger(FileUtils.class);
	
	public static void makeDir(String path)
	{
		File f = new File(path);
		if (f.exists())
		{
			m_log4j.info("'" + path + "' exists. Nothing to do.");
		}
		else
		{
			f.mkdir();
			m_log4j.info("'" + path + "' created.");
		}
	}
	
	public static void moveFile(File sourceFile, File destDirectory) throws Exception
	{
		try
		{
			copyFile(sourceFile, destDirectory);
			sourceFile.delete();
		}
		finally
		{

		}
	}
	
	public static void moveFile(String sourceFile, String destDirectory) throws Exception
	{
		File source = null;
		File dest = null;
		try
		{
			source = new File(sourceFile);
			dest = new File(destDirectory);  
			copyFile(source, dest);
			source.delete();
		}
		finally
		{

		}
	}
	
	public static void copyFile(File sourceFile, File destDirectory) throws Exception
	{
		File destination = new File(destDirectory, sourceFile.getName());
		FileChannel in = null, out = null;
		try
		{
			in = new FileInputStream(sourceFile).getChannel();
			out = new FileOutputStream(destination).getChannel();
			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
			out.write(buf);
		}
		finally
		{
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
		}
	}
	
	public static boolean isPath(String a)
	{
		return (a.contains("\\") || a.contains("/"));
	}
	
	public static int getLastIdFromFile(String filePath)
	{
		m_log4j.info("Loading last id from file " + filePath + " ... ");
		int res = 0;
		
		BufferedReader bufRdr = null;
		try
		{
			File file = new File(filePath);
			
			if(file.exists())
			{
				int index = 0;
				String line = null;
				bufRdr = new BufferedReader(new FileReader(file));
				
				if( (line = bufRdr.readLine()) != null )
				{
					index++;
					res = Integer.parseInt(line.trim());
				}
				
				if(index > 0)
				{
					m_log4j.info("Ok, loaded " + res + " last raw data file exported.");
				}
				else
				{
					m_log4j.info("Ok, file " + filePath + " exists, but is empty... Nothing to do. Return ID = 0! ");
					res = 0;
				}
			}
			else
			{
				m_log4j.info("File " + filePath + " doesn't exist... Nothing to do. Return ID = 0!");
				res = 0;
			}
		}
		catch (Exception e)
		{
			m_log4j.error(e,e);
			res = 0;
		}
		finally
		{
			try
			{
				if(bufRdr != null)
				bufRdr.close();
			}
			catch (IOException e)
			{
				m_log4j.error(e,e);
			}
		}
		
		return res;
	}
	
	public static void flushLastIdOnFile(String filePath, String lastId)
	{
		File file = null;
		FileOutputStream file_os = null;
		PrintStream output = null;
		
		m_log4j.info("Flushing last ID " + lastId + " data exported into the file " + filePath + " ... ");
		
		try
		{
			file = new File(filePath);
			
			file_os = new FileOutputStream(file, false);
			output = new PrintStream(file_os);
			
			output.println(lastId);
			
			output.flush();
			output.close();
			file_os.close();
			
			m_log4j.info("Ok, flushing terminated");

		}
		catch(Exception e)
		{
			m_log4j.error(e, e);
			file = null;
		}
	}
}
