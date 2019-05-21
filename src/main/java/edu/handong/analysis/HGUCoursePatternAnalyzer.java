package edu.handong.analysis;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.io.File;

import edu.handong.analysis.datamodel.Course;
import edu.handong.analysis.datamodel.Student;
import edu.handong.analysis.utils.NotEnoughArgumentException;
import edu.handong.analysis.utils.Utils;

public class HGUCoursePatternAnalyzer {

	private HashMap<String,Student> students;
	
	/**
	 * This method runs our analysis logic to save the number courses taken by each student per semester in a result file.
	 * Run method must not be changed!!
	 * @param args
	 */
	public void run(String[] args) {
		
		try {
			// when there are not enough arguments from CLI, it throws the NotEnoughArgmentException which must be defined by you.
			if(args.length<2)
				throw new NotEnoughArgumentException();
			
			// check file exist. 
			File f1 = new File(args[0]);
			if(!f1.exists() || f1.isDirectory())
				throw new Exception("The file path does not exist. Please check your CLI argument!");
		} catch (NotEnoughArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		String dataPath = args[0]; // csv file to be analyzed
		String resultPath = args[1]; // the file path where the results are saved.
		ArrayList<String> lines = Utils.getLines(dataPath, true);
		
		students = loadStudentCourseRecords(lines);
		
		// To sort HashMap entries by key values so that we can save the results by student ids in ascending order.
		Map<String, Student> sortedStudents = new TreeMap<String,Student>(students); 
		
		// Generate result lines to be saved.
		ArrayList<String> linesToBeSaved = countNumberOfCoursesTakenInEachSemester(sortedStudents);
		
		// Write a file (named like the value of resultPath) with linesTobeSaved.
		Utils.writeAFile(linesToBeSaved, resultPath);
	}
	
	/**
	 * This method create HashMap<String,Student> from the data csv file. Key is a student id and the corresponding object is an instance of Student.
	 * The Student instance have all the Course instances taken by the student.
	 * @param lines
	 * @return
	 */
	private HashMap<String,Student> loadStudentCourseRecords(ArrayList<String> lines) {
		
		HashMap<String,Student> newStudents = new HashMap<String,Student>();
		
		for (String item : lines)
		{
			String studentId = item.split(",")[0].trim();	// get student id.
			Student tempStudent;
			
			if (newStudents.containsKey(studentId))			// if contain studentid, get this id's student.
			{
				tempStudent = newStudents.get(studentId);
			}
			else
			{
				tempStudent = new Student(studentId);		// if not exist, create student and put in hashmap.
				newStudents.put(studentId, tempStudent);
			}
			
			tempStudent.addCourse(new Course(item));
		}
		
		return newStudents; // do not forget to return a proper variable.
	}

	/**
	 * This method generate the number of courses taken by a student in each semester. The result file look like this:
	 * StudentID, TotalNumberOfSemestersRegistered, Semester, NumCoursesTakenInTheSemester
	 * 0001,14,1,9
     * 0001,14,2,8
	 * ....
	 * 
	 * 0001,14,1,9 => this means, 0001 student registered 14 semeters in total. In the first semeter (1), the student took 9 courses.
	 * 
	 * 
	 * @param sortedStudents
	 * @return
	 */
	private ArrayList<String> countNumberOfCoursesTakenInEachSemester(Map<String, Student> sortedStudents) {
		ArrayList<String> resultStrings = new ArrayList<String>();
		
		for (Student student : sortedStudents.values())
		{
			Map<String, Integer> sortedSemesters = new TreeMap<String,Integer>(student.getSemestersByYearAndSemester()); 
			
			String totalSemester = Integer.toString(sortedSemesters.values().size());
			
			for (Integer nSemester : sortedSemesters.values())
			{
				String tempString = new String();
				tempString += student.getStudentId();
				tempString += ",";
				tempString += totalSemester;
				tempString += ",";
				tempString += nSemester.toString();
				tempString += ",";
				tempString += student.getNumCourseInNthSementer(nSemester);
				
				resultStrings.add(tempString);
			}
		}
		
		return resultStrings; // do not forget to return a proper variable.
	}
}
