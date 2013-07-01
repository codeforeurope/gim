package sistematica.infomobprocessor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
			m_log4j.debug("'" + path + "' exists. Nothing to do.");
		}
		else
		{
			f.mkdir();
			m_log4j.debug("'" + path + "' created.");
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
}
