package com.universityTimetableManagementSystem.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.universityTimetableManagementSystem.model.data.Course;

@Repository
public interface CourseRepo extends MongoRepository<Course, String> {

	Optional <Course> findByCourseName(String courseName);

	
}
