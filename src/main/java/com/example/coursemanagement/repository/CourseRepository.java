package com.example.coursemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.coursemanagement.modelclass.Course;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
	@Query("{'courseId': ?0}")
    Optional<Course> findByCourseId(String courseId);

	void deleteByCourseId(String courseId);
    Optional<Course> findByCourseNameAndInstructorId(String courseName, String instructorId);
    
    Optional<Course> findByApproveAndInstructorId(String approve, String instructorId);

    List<Course> findByInstructorId(String instructorId);
    
    
    List<Course> findByApprove(String approve);
 


}
