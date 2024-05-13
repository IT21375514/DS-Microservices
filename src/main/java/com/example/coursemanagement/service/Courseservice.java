package com.example.coursemanagement.service;

import java.util.List;

import com.example.coursemanagement.modelclass.Course;




public interface Courseservice {
	public void createCourse(Course course);
	public List<Course> getAllCourses();
	public void deleteByCourseCode(String courseCode);
	public void updateCourse(String courseID, Course course);

	public Course getCourse(String courseId) ;

	public void updateApproveStatusCourse(String courseId, Course course);

		public Double getCourseAmount(String courseId);
	public List<Course> getAllCoursesByApprove(String approve);

	public List<Course> findByInstructorId(String approve);
	public Course getCourseByApproveAndInstructorId(String approve,String instructorId) ;

	public List<String> getCourseIds();	
}
