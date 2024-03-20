package com.universityTimetableManagementSystem.service;

import java.util.List;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;

import jakarta.validation.ConstraintViolationException;

public interface CourseService {

	void createCourse(Course course) throws ConstraintViolationException, CourseCollectionException;
	
	List<Course> getAllCourse();
	
	Course getSingleCourse(String id) throws CourseCollectionException;

	void updateCourse(String id, Course course) throws CourseCollectionException;
	
	void deleteCourse(String id) throws CourseCollectionException;


}
