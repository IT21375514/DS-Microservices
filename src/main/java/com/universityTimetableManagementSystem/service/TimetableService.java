package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.data.RRBooking;
import com.universityTimetableManagementSystem.model.data.Timetable;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface TimetableService {

    void createTable(Timetable timetable, String userName) throws ConstraintViolationException, TimetableCollectionException, RRBookingCollectionException, CourseFacultyCollectionException, CourseCollectionException;

    List<Timetable> getAllTimetable() throws TimetableCollectionException;

    List<Timetable> getSingleFacultyTimetable(String faculty) throws TimetableCollectionException;

    void updateTimetable(String id, Timetable timetable, String userName) throws TimetableCollectionException, RRBookingCollectionException;

    void deleteTimetable(String id, String faculty) throws TimetableCollectionException, RRBookingCollectionException;

}
