package com.universityTimetableManagementSystem.repository;

import com.universityTimetableManagementSystem.model.data.RRBooking;
import com.universityTimetableManagementSystem.model.data.RRBookingId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RRBookingRepo extends MongoRepository<RRBooking, RRBookingId> {

    @Query("{'_id.rrId':?0}")
    Optional<RRBooking> findByResouceId(String code);

    @Query("{'_id.date':?0}")
    Optional <RRBooking> findByDate(Date date);

    @Query("{'_id.rrId': ?0, '_id.date': ?1}")
    List<RRBooking> findByIdDate(String rrId, Date date);

    @Query("{'_id.startTime':?0}")
    Optional <RRBooking> findByStartTime(LocalTime startTime);

    @Query("{'timeTableReference':?0}")
    List<RRBooking> findByTimeTableReference(String timeTableReference);

    @Query("{'_id.endTime':?0}")
    Optional <RRBooking> findByEndTime(LocalTime endTime);
}
