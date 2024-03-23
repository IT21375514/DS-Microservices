package com.universityTimetableManagementSystem.exception;

public class CourseFacultyCollectionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CourseFacultyCollectionException(String message) {
		super(message);
	}
	
	public static String NotFoundException(String code, String faculty) {
		return "Course %s or Faculty %s not found".formatted(code, faculty);
	}
	
	public static String CourseFacultyAlreadyExist() {
		return "Course Faculty already exist";
	}
}
