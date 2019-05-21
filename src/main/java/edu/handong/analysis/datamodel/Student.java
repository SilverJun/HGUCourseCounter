package edu.handong.analysis.datamodel;
import java.util.ArrayList;
import java.util.HashMap;

public class Student {
	private String studentId;
	private ArrayList<Course> coursesTaken; // 학생이 들은 수업 목록
	private HashMap<String,Integer> semestersByYearAndSemester; 
	                                                         // key: Year-Semester
	                                                         // e.g., 2003-1,
	
	public Student(String studentId) // constructor
	{
		this.setStudentId(studentId);
		coursesTaken = new ArrayList<Course>();
		semestersByYearAndSemester = new HashMap<String, Integer>();
	}
	
	public void addCourse(Course newRecord)
	{
		coursesTaken.add(newRecord);
	}
	
	public HashMap<String,Integer> getSemestersByYearAndSemester()
	{
		for (Course course : coursesTaken)
		{
			if (semestersByYearAndSemester.containsKey(Integer.toString(course.getYearTaken())))
			{
				continue;
			}
			else
			{
				semestersByYearAndSemester.put(Integer.toString(course.getYearTaken()), course.getSemesterCourseTaken());
			}
		}
		
		return semestersByYearAndSemester;
	}
	
	public int getNumCourseInNthSementer(int semester)
	{
		int count = 0;
		
		for (Course course : coursesTaken)
			if (course.getSemesterCourseTaken() == semester)
				count++;
		
		return count;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
}
