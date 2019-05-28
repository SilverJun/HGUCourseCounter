package edu.handong.analysis;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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
		
		CommandLineParser parser = new DefaultParser();

		String dataPath = null; // csv file to be analyzed
		String resultPath = null; // the file path where the results are saved.
		String startYear = null;
		String endYear = null;
		
		int analysisType = 0;	//1: Count courses per semester, 2: Count per course name and year
		
		Options options = createOptions();
		try {
			CommandLine cmd = parser.parse(options, args);

			dataPath = cmd.getOptionValue("i");
			resultPath = cmd.getOptionValue("o");
			analysisType = Integer.parseInt(cmd.getOptionValue("a"));
			startYear = cmd.getOptionValue("s");
			endYear = cmd.getOptionValue("e");
			if (analysisType == 2 && cmd.getOptionValue("c") == null) throw new NotEnoughArgumentException();

			// check file exist. 
			File f1 = new File(args[0]);
			if(!f1.exists() || f1.isDirectory())
				throw new Exception("The file path does not exist. Please check your CLI argument!");
		} catch (NotEnoughArgumentException e) {
			printHelp(options);
			System.exit(0);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
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
	
	private Options createOptions() {
		Options options = new Options();

		options.addOption(Option.builder("i").longOpt("input")
				.desc("Set an input file path")
				.hasArg()
				.argName("Input path")
				.required()
				.build());
		
		options.addOption(Option.builder("o").longOpt("output")
				.desc("Set an input file path")
				.hasArg()
				.argName("Output path")
				.required()
				.build());
		
		options.addOption(Option.builder("a").longOpt("analysis")
				.desc("1: Count courses per semester, 2: Count per course name and year")
				.hasArg()
				.argName("Analysis option")
				.required()
				.build());
		
		options.addOption(Option.builder("c").longOpt("coursecode")
				.desc("Course code for '-a 2' option")
				.hasArg()
				.argName("course code")
				.build());
		
		options.addOption(Option.builder("s").longOpt("startyear")
				.desc("CSet the start year for analysis e.g., -s 2002")
				.hasArg()
				.argName("Start year for analysis")
				.required()
				.build());
		
		options.addOption(Option.builder("e").longOpt("endyear")
				.desc("Set the end year for analysis e.g., -e 2005")
				.hasArg()
				.argName("End year for analysis")
				.required()
				.build());

		// add options by using OptionBuilder
		options.addOption(Option.builder("h").longOpt("help")
		        .desc("Help")
		        .build());
		
		return options;
	}
	

	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "HGU Course Analyzer";
		String footer = "";
		formatter.printHelp("HGUCourseCounter", header, options, footer, true);
	}
}
