package com.universityTimetableManagementSystem.exception;

import java.time.LocalTime;
import java.util.Date;

public class RRBookingCollectionException extends Exception  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RRBookingCollectionException(String message) {
        super(message);
    }

    public static String NotFoundException(String rrId, Date date, LocalTime startTime, LocalTime endTime) {
        return "Resource %s Booking on %s not found from %s to %s".formatted(rrId, date, startTime, endTime);
    }

    public static String ResourceAlreadyExist() {
        return "Resource already exist";
    }

    public static String MissingData() {
        return "No data exists";
    }

    public static String UpdateFailed() {
        return "Updated Failed";
    }

    public static String UpdateResourceFailed() {
        return "Updated Failed, Room/Resource not available at the time";
    }

    public static String UpdateFailedFaculty() {
        return "No access to user";
    }

}
