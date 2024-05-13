package com.example.coursemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.coursemanagement.modelclass.Coursecontent;

@Repository
public interface CoursecontentRepository extends MongoRepository<Coursecontent, String> {
    Optional<Coursecontent> findByCourseIdAndWeekId(String courseId, String weekId);

	@Query("{'courseContentId': ?0}")
    Optional<Coursecontent> findByCourseContentId(String courseContentId);
//	
	void deleteByCourseContentId(String courseContentId);
	
	void deleteByCourseId(String courseId);

	List<Coursecontent> findByCourseId(String courseid);

}


