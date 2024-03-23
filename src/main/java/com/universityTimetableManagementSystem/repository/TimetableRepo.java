package com.universityTimetableManagementSystem.repository;

import com.universityTimetableManagementSystem.model.data.RRBooking;
import com.universityTimetableManagementSystem.model.data.RRBookingId;
import com.universityTimetableManagementSystem.model.data.Timetable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface TimetableRepo extends MongoRepository<Timetable, String> {

    @Query("{'code':?0}")
    Optional<Timetable> findByCode(String code);

    @Query("{'faculty':?0}")
    List<Timetable> findByFaculty(String faculty);

    @Query("{'code': ?0, 'batch': ?1}")
    List<Timetable> findByCourseBatch(String code, String batch);

}