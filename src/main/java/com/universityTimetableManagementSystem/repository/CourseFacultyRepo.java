package com.universityTimetableManagementSystem.repository;

import com.universityTimetableManagementSystem.model.data.CourseFacultyId;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.universityTimetableManagementSystem.model.data.CourseFaculty;

@Repository
public interface CourseFacultyRepo extends MongoRepository<CourseFaculty, CourseFacultyId> {

	@Query("{'_id.code':?0}")
	Optional <CourseFaculty> findByCode(String code);

	@Query("{'_id.facultyId':?0}")
	Optional <CourseFaculty> findByFacultyId(String facultyId);

	@Query("{'_id.code':?0,'_id.facultyId':?0}")
	Optional <CourseFaculty> findByFacultyIdCode(String facultyId);
}
