package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.StudentEnrollmentCollectionException;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;
import com.universityTimetableManagementSystem.model.data.StudentEnrollment;
import com.universityTimetableManagementSystem.model.data.Timetable;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

public interface StudentEnrollmentService {
    void createStudentEnrollment(StudentEnrollment studentEnrollment) throws ConstraintViolationException, StudentEnrollmentCollectionException;

    List<StudentEnrollment> getAllStudentEnrollment();

    List<Timetable> getCurrentStudentAllEnrollment(String student);

    StudentEnrollment getSingleStudentEnrollment(String code, String student, String studentPeriod) throws StudentEnrollmentCollectionException;


    void deleteStudentEnrollment(String code, String student, String studentPeriod) throws StudentEnrollmentCollectionException;

}

