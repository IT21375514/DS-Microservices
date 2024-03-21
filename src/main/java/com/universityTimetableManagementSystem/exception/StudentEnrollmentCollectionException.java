package com.universityTimetableManagementSystem.exception;

public class StudentEnrollmentCollectionException  extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StudentEnrollmentCollectionException(String message) {
        super(message);
    }

    public static String NotFoundException(String code, String student) {
        return "Course %s or Student %s not found".formatted(code, student);
    }

    public static String StudentAlreadyExist() {
        return "Student already enrolled";
    }
}