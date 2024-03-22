package com.universityTimetableManagementSystem.exception;

public class StudentEnrollmentCollectionException  extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StudentEnrollmentCollectionException(String message) {
        super(message);
    }

    public static String NotFoundException(String code, String student, String studentPeriod) {
        return "Course %s or Student %s not found for %s".formatted(code, student, studentPeriod);
    }

    public static String StudentAlreadyExist() {
        return "Student already enrolled";
    }

    public static String MissingData() {
        return "Provide valid Course Code, Student Username and Period";
    }
}