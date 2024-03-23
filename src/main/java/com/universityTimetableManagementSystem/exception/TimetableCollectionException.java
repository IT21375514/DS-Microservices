package com.universityTimetableManagementSystem.exception;

public class TimetableCollectionException  extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TimetableCollectionException(String message) {
        super(message);
    }

    public static String NotFoundException(String id) {
        return "Timetable %s not found".formatted(id);
    }

    public static String ResourceAlreadyExist() {
        return "Timetable already exist";
    }

    public static String MissingData() {
        return "No data exists";
    }

    public static String UpdateFailed() {
        return "Updated Failed";
    }

    public static String UpdateFailedFaculty() {
        return "No access to user";
    }
}
