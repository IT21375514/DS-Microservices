package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.model.data.RRBooking;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface RRBookingService {
    void createRRBooking(RRBooking rrBooking, String userName) throws ConstraintViolationException, RRBookingCollectionException;

    List<RRBooking> getAllRRBooking() throws RRBookingCollectionException;

    List<RRBooking> getSingleRRBooking(String id, Date date) throws RRBookingCollectionException;

    void updateRRBooking(String id, Date date, LocalTime startTime, LocalTime endTime, RRBooking rrBooking, String userName) throws RRBookingCollectionException;

    void deleteRRBooking(String id, Date date, LocalTime startTime, LocalTime endTime, String userName) throws RRBookingCollectionException;

    void deleteRRBookingByTimetable(String timeTableReference) throws RRBookingCollectionException;

}