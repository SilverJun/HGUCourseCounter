package edu.handong.analysis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Utils {
	
	public static void writeA1File(ArrayList<String> lines, String targetFileName)
	{
		Path path = Paths.get(targetFileName);
		File parentDir = path.toFile().getParentFile();
		if (!parentDir.exists())
		{
			try 
			{
				parentDir.mkdirs();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		File resultFile = new File(targetFileName);
		
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(resultFile));
			resultFile.createNewFile();
			pw.println("StudentID, TotalNumberOfSemestersRegistered, Semester, NumCoursesTakenInTheSemester");
			for (String str : lines)
				pw.println(str);
			pw.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void writeA2File(ArrayList<String> lines, String targetFileName)
	{
		Path path = Paths.get(targetFileName);
		File parentDir = path.toFile().getParentFile();
		if (!parentDir.exists())
		{
			try 
			{
				parentDir.mkdirs();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		File resultFile = new File(targetFileName);
		
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(resultFile));
			resultFile.createNewFile();
			pw.println("Year,Semester,CouseCode, CourseName,TotalStudents,StudentsTaken,Rate");
			for (String str : lines)
				pw.println(str);
			pw.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
