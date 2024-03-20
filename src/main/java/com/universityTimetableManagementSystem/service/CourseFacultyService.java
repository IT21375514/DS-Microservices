package com.universityTimetableManagementSystem.service;

import java.util.List;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;

import jakarta.validation.ConstraintViolationException;

public interface CourseFacultyService {
	void createCourseFaculty(CourseFaculty courseFaculty) throws ConstraintViolationException, CourseFacultyCollectionException;
	
	List<CourseFaculty> getAllCourseFaculty();
	
	CourseFaculty getSingleCourseFaculty(String code, String faculty) throws CourseFacultyCollectionException;

//	public void updateCourseFaculty(String id, CourseFaculty courseFaculty) throws CourseFacultyCollectionException;
	
	void deleteCourseFaculty(String id, String facultyId) throws CourseFacultyCollectionException;

}
