/**
 * 
 */
package test;

import java.util.ArrayList;

import sistematica.infomobprocessor.lib.ParseFileInfoMob;

/**
 * @author gsilvestri
 * 
 */
public class TestIsDate
{
	public static void main(String[] args)
	{

		ArrayList<String> dates = new ArrayList<String>();
		dates.add("05.10.1981"); // swiss date format (dd.MM.yyyy)
		dates.add("05-10-1981");
		dates.add("07-09-2006 23:00:33");
		dates.add("2006-09-07 23:01:24");
		dates.add("2011-07-27 19:33:48");
		dates.add("2003-30-30"); // false
		dates.add("text"); // false
		dates.add("-1");

		for (String d : dates)
			System.out.println("Date " + d + ":" +ParseFileInfoMob.isDate(d));

	}
}
