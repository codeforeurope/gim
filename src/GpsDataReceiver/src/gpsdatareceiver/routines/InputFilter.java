package gpsdatareceiver.routines;

import java.io.File;
import java.io.FilenameFilter;

public class InputFilter implements FilenameFilter {

	protected String pattern;
	public InputFilter(String str) {
	    pattern = str;
	}
	
	public boolean accept(File dir, String name) {
		return name.toLowerCase().endsWith(pattern.toLowerCase());
	}

}
