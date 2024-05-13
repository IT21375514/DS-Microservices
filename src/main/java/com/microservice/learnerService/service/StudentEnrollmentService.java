package com.microservice.learnerService.service;

import com.microservice.learnerService.exception.StudentEnrollmentCollectionException;
import com.microservice.learnerService.model.data.StudentEnrollment;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

public interface StudentEnrollmentService {
    void createStudentEnrollment(String username, StudentEnrollment studentEnrollment) throws ConstraintViolationException, StudentEnrollmentCollectionException;

    List<StudentEnrollment> getAllStudentEnrollment();

    StudentEnrollment getSingleStudentEnrollmentCourse(String code, String student);

    public List<StudentEnrollment> getSingleStudentEnrollment(String student) throws StudentEnrollmentCollectionException ;

    
   StudentEnrollment getSingleEnrollment(String code, String student) throws StudentEnrollmentCollectionException;
//
//
    void deleteStudentEnrollment(String code, String student) throws StudentEnrollmentCollectionException;
    
    
    public void createInitialProgress(String studentId, String weekId,String courseId, boolean lectureViewed, boolean videoViewed) ;

//	void createInitialProgress(String studentUserName, String courseId, String weekId);
    
    public Integer getProgresscount(String courseId, String studentUsername);


}

