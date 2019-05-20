package edu.handong.analysis.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Utils {
	
	public static ArrayList<String> getLines(String file, boolean removeHeader)
	{
		ArrayList<String> newStrings = new ArrayList<String>();
		
		try {
			// file read
			BufferedReader in = new BufferedReader(new FileReader("path/to/file.csv"));
			
			String s;
			if (removeHeader) in.readLine();		// if removeHeader, readLine() to skip first line.
			
		    while ((s = in.readLine()) != null) {		// append each line
		        newStrings.add(s);
		    }
		    in.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		return newStrings;
	}
	
	public static void writeAFile(ArrayList<String> lines, String targetFileName)
	{
		
	}
}
