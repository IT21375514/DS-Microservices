package com.microservice.learnerService.exception;

public class StudentEnrollmentCollectionException  extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StudentEnrollmentCollectionException(String message) {
        super(message);
    }

    public static String NotFoundException(String code, String student) {
        return "Course %s or Student %s not found for %s".formatted(code, student);
    }
    
    
    public static String NotFoundException(String student) {
        return "Student %s not found for %s".formatted(student);
    }

    public static String StudentAlreadyExist() {
        return "Student already enrolled";
    }

    public static String MissingData() {
        return "Provide valid Course Code, Student Username and Period";
    }
}