package com.universityTimetableManagementSystem.exception;

public class CourseCollectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CourseCollectionException(String message) {
		super(message);
	}
	
	public static String NotFoundException(String code) {
		return "Course with " + code+" not found";
	}
	
	public static String CourseAlreadyExist() {
		return "Course with given name already exist";
	}

}
