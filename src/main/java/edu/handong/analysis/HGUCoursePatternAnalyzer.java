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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		String courseCode = null;
		
		int analysisType = 0;	//1: Count courses per semester, 2: Count per course name and year
		
		Options options = createOptions();
		try {
			CommandLine cmd = parser.parse(options, args);

			dataPath = cmd.getOptionValue("i");
			resultPath = cmd.getOptionValue("o");
			analysisType = Integer.parseInt(cmd.getOptionValue("a"));
			startYear = cmd.getOptionValue("s");
			endYear = cmd.getOptionValue("e");
			if (analysisType == 2 && (courseCode = cmd.getOptionValue("c")) == null) throw new NotEnoughArgumentException();
			if (analysisType != 1 && analysisType != 2) throw new NotEnoughArgumentException();
			// check file exist. 
			File f1 = new File(dataPath);
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
		
		CSVParser csvParser = null;
		try {
		Reader reader = Files.newBufferedReader(Paths.get(dataPath));
        csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withTrim());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		students = loadStudentCourseRecords(csvParser);
		
		// To sort HashMap entries by key values so that we can save the results by student ids in ascending order.
		Map<String, Student> sortedStudents = new TreeMap<String,Student>(students); 
		
		if (analysisType == 1)
		{
			// Generate result lines to be saved.
			ArrayList<String> linesToBeSaved = countNumberOfCoursesTakenInEachSemester(sortedStudents, startYear, endYear);
			
			// Write a file (named like the value of resultPath) with linesTobeSaved.
			Utils.writeA1File(linesToBeSaved, resultPath);
		}
		else
		{
			// Generate result lines to be saved.
			ArrayList<String> linesToBeSaved = courseTakenAnalyze(sortedStudents, courseCode, startYear, endYear);
						
			Utils.writeA2File(linesToBeSaved, resultPath);
		}
	}
	
	/**
	 * This method create HashMap<String,Student> from the data csv file. Key is a student id and the corresponding object is an instance of Student.
	 * The Student instance have all the Course instances taken by the student.
	 */
	private HashMap<String,Student> loadStudentCourseRecords(CSVParser csvParser) {
		
		HashMap<String,Student> newStudents = new HashMap<String,Student>();
		
		for (CSVRecord item : csvParser)
		{
			String studentId = item.get("StudentID");	// get student id.
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
	private ArrayList<String> countNumberOfCoursesTakenInEachSemester(Map<String, Student> sortedStudents, String startYear, String endYear) {
		ArrayList<String> resultStrings = new ArrayList<String>();
		
		int startyear = Integer.parseInt(startYear);
		int endyear = Integer.parseInt(endYear);
		
		for (Student student : sortedStudents.values())
		{
			Map<String, Integer> sortedSemesters = new TreeMap<String,Integer>(student.getSemestersByYearAndSemester()); 
			
			String totalSemester = Integer.toString(sortedSemesters.values().size());
			
			for (Integer nSemester : sortedSemesters.values())
			{
				String year = null;
				for (String key : sortedSemesters.keySet()) {		// 해당년도를 받아오기 위한 탐색.
					if (sortedSemesters.get(key) == nSemester)
					{
						year = key;
						break;
					}
				}
				
				int intyear = Integer.parseInt(year.split("-")[0]);		// 해당 년도 받아옴.
				System.out.println(intyear);
				if (intyear < startyear || intyear > endyear) {			// 주어진 범위가 아니면 스킵.
					continue;
				}
				
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
	
	private ArrayList<String> courseTakenAnalyze(Map<String, Student> students, String courseCode, String startYear, String endYear)
	{
		ArrayList<String> resultStrings = new ArrayList<String>();
		HashMap<Integer, Course> courses = new HashMap<Integer, Course>();
		HashMap<Integer, Integer> yearTotalPerson = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> yearTakenPerson = new HashMap<Integer, Integer>();
		
		int startyear = Integer.parseInt(startYear);
		int endyear = Integer.parseInt(endYear);
		
		for (Student item : students.values())
		{
			for (Course course : item.getCoursesTaken())
			{
				int courseYear = course.getYearTaken();
				if (courseYear < startyear || courseYear > endyear) continue;
				
				if (yearTotalPerson.containsKey(courseYear))
				{
					yearTotalPerson.put(courseYear, yearTotalPerson.get(courseYear)+1);
				}
				else
				{
					yearTotalPerson.put(courseYear, 1);
				}
				
				if (course.getCourseCode().equals(courseCode))
				{
					courses.put(courseYear, course);
					if (yearTakenPerson.containsKey(courseYear))
						yearTakenPerson.put(courseYear, yearTakenPerson.get(courseYear)+1);
					else
						yearTakenPerson.put(courseYear, 1);
					break;
				}
			}
		}
		
		//Map<Integer, Course> sortCourses = new TreeMap<Integer,Course>(courses); 
		//Map<Integer, Integer> sortYearTotalPerson = new TreeMap<Integer,Integer>(yearTotalPerson); 
		Map<Integer, Integer> sortYearTakenPerson = new TreeMap<Integer,Integer>(yearTakenPerson); 
		
		
		for (Integer i : sortYearTakenPerson.keySet())
		{
			//Year,Semester,CouseCode, CourseName,TotalStudents,StudentsTaken,Rate
			resultStrings.add(i.toString() + ", "
					+ courses.get(i).getSemesterCourseTaken() + ", "
					+ courseCode + ", "
					+ courses.get(i).getCourseName() + ", "
					+ yearTotalPerson.get(i) + ", "
					+ yearTakenPerson.get(i) + ", "
					+ String.format("%.1f", (yearTakenPerson.get(i).floatValue() / yearTotalPerson.get(i).floatValue())*100.0f ));
		}
		
		
		return resultStrings;
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
