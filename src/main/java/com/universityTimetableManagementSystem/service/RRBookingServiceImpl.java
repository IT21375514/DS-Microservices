package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.StudentEnrollmentCollectionException;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.repository.CourseRepo;
import com.universityTimetableManagementSystem.repository.RRBookingRepo;
import com.universityTimetableManagementSystem.repository.StudentEnrollmentRepo;
import com.universityTimetableManagementSystem.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RRBookingServiceImpl implements RRBookingService{

    private final CourseRepo courseRepo;
    private final StudentEnrollmentRepo studentEnrollmentRepo;
    private final RRBookingRepo rrBookingRepo;
    private final UserRepository userRepository;

    public RRBookingServiceImpl(CourseRepo courseRepo, StudentEnrollmentRepo studentEnrollmentRepo, RRBookingRepo rrBookingRepo, UserRepository userRepository) {
        this.courseRepo = courseRepo;
        this.studentEnrollmentRepo = studentEnrollmentRepo;
        this.rrBookingRepo = rrBookingRepo;
        this.userRepository = userRepository;
    }

    @Override
    public void createRRBooking(RRBooking rrBooking, String userName) throws ConstraintViolationException, RRBookingCollectionException {

        RRBookingId id = RRBookingId.builder().rrId(rrBooking.getRrBookingId().getRrId()).date(rrBooking.getRrBookingId().getDate()).startTime(rrBooking.getRrBookingId().getStartTime()).endTime(rrBooking.getRrBookingId().getEndTime()).build();
        Optional<RRBooking> RRBookingWithData = rrBookingRepo.findById(id);

        String currentRRId = rrBooking.getRrBookingId().getRrId();
        Date currentDate = rrBooking.getRrBookingId().getDate();
        LocalTime currentStartTime = rrBooking.getRrBookingId().getStartTime();
        LocalTime currentEndTime = rrBooking.getRrBookingId().getEndTime();


        if(currentRRId!=null && currentDate!=null && currentStartTime!=null && currentEndTime!=null) {

            List<RRBooking> rrookingAll = rrBookingRepo.findByIdDate(rrBooking.getRrBookingId().getRrId(),rrBooking.getRrBookingId().getDate());

            if (RRBookingWithData.isEmpty()) {
                boolean isValidAllocation = true;

                for (RRBooking existingBooking : rrookingAll) {
                    // Check if rrBooking overlaps with existingBooking
                    if (!((rrBooking.getRrBookingId().getStartTime().isBefore(existingBooking.getRrBookingId().getStartTime()) && rrBooking.getRrBookingId().getEndTime().isBefore(existingBooking.getRrBookingId().getStartTime())) ||
                            (rrBooking.getRrBookingId().getStartTime().isAfter(existingBooking.getRrBookingId().getEndTime()) && rrBooking.getRrBookingId().getEndTime().isAfter(existingBooking.getRrBookingId().getEndTime())))
                    ) {
                        // There is an overlap, set validation to false and break the loop
                        isValidAllocation = false;
                        break;
                    }
                }

                if (isValidAllocation) {
                    // Proceed with allocation
                    rrBooking.setUpdated(LocalDateTime.now());
                    rrBooking.setFaculty(userName);
                    rrBookingRepo.save(rrBooking);
                } else {
                    // Cannot allocate resource during this time
                    throw new RRBookingCollectionException(
                            RRBookingCollectionException.UpdateResourceFailed());
                }


            } else {
                throw new RRBookingCollectionException(
                        RRBookingCollectionException.ResourceAlreadyExist());
            }
        }else {
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.MissingData());
        }
    }

    @Override
    public List<RRBooking> getAllRRBooking() throws RRBookingCollectionException {
        List<RRBooking> rrooking = rrBookingRepo.findAll();
        return !rrooking.isEmpty() ? rrooking : Collections.emptyList();
    }

    @Override
    public List<RRBooking> getSingleRRBooking(String id, Date date) throws RRBookingCollectionException {
        List<RRBooking> rrooking = rrBookingRepo.findByIdDate(id,date);
        return !rrooking.isEmpty() ? rrooking : Collections.emptyList();
    }

    @Override
    public void updateRRBooking(String id, Date date, LocalTime startTime, LocalTime endTime, RRBooking rrBooking, String userName) throws RRBookingCollectionException {

        RRBookingId rrid1 = RRBookingId.builder().rrId(id).date(date).startTime(startTime).endTime(endTime).build();
        RRBooking RRBookingWithData = rrBookingRepo.findById(rrid1).orElse(null);

        RRBookingId rrid2 = RRBookingId.builder().rrId(rrBooking.getRrBookingId().getRrId()).date(rrBooking.getRrBookingId().getDate()).startTime(rrBooking.getRrBookingId().getStartTime()).endTime(rrBooking.getRrBookingId().getEndTime()).build();
        Optional<RRBooking> RRBookingWithNewData = rrBookingRepo.findById(rrid2);

        List<RRBooking> rrookingAll = rrBookingRepo.findByIdDate(rrBooking.getRrBookingId().getRrId(),rrBooking.getRrBookingId().getDate());

        if (RRBookingWithData != null && RRBookingWithNewData.isEmpty() && RRBookingWithData.getFaculty().equalsIgnoreCase(userName)) {
            boolean isValidAllocation = true;

            for (RRBooking existingBooking : rrookingAll) {
                // Check if rrBooking overlaps with existingBooking
                if (!((rrBooking.getRrBookingId().getStartTime().isBefore(existingBooking.getRrBookingId().getStartTime()) && rrBooking.getRrBookingId().getEndTime().isBefore(existingBooking.getRrBookingId().getStartTime())) ||
                        (rrBooking.getRrBookingId().getStartTime().isAfter(existingBooking.getRrBookingId().getEndTime()) && rrBooking.getRrBookingId().getEndTime().isAfter(existingBooking.getRrBookingId().getEndTime())))
                ) {
                    // There is an overlap, set validation to false and break the loop
                    isValidAllocation = false;
                    break;
                }
            }

            if (isValidAllocation) {
                // Proceed with allocation
                deleteRRBooking(id, date, startTime, endTime, userName);
                createRRBooking(rrBooking,userName);
            } else {
                // Cannot allocate resource during this time
                throw new RRBookingCollectionException(
                        RRBookingCollectionException.UpdateResourceFailed());
            }
        } else if (RRBookingWithData == null) {
            // No existing bookings for this date, so allocation is valid
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.MissingData());
        } else if (RRBookingWithNewData.isPresent()) {
            // No existing bookings for this date, so allocation is valid
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.ResourceAlreadyExist());
        } else if (!RRBookingWithData.getFaculty().equalsIgnoreCase(userName)) {
            // No existing bookings for this date, so allocation is valid
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.UpdateFailedFaculty());
        } else {
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.MissingData());
        }
    }

    @Override
    public void deleteRRBooking(String id, Date date, LocalTime startTime, LocalTime endTime, String userName) throws RRBookingCollectionException {
        RRBookingId rrid = RRBookingId.builder().rrId(id).date(date).startTime(startTime).endTime(endTime).build();
        Optional<RRBooking> RRBookingWithNewData = rrBookingRepo.findById(rrid);

        if (RRBookingWithNewData.isPresent() && RRBookingWithNewData.get().getFaculty().equalsIgnoreCase(userName)) {
            rrBookingRepo.deleteById(rrid);
        } else if(!RRBookingWithNewData.get().getFaculty().equalsIgnoreCase(userName)) {
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.UpdateFailedFaculty());
        }else {
            throw new RRBookingCollectionException(
                    RRBookingCollectionException.NotFoundException(id, date, startTime, endTime));
        }
    }

    @Override
    public void deleteRRBookingByTimetable(String timeTableReference) throws RRBookingCollectionException {

        List<RRBooking> rrBookingList = rrBookingRepo.findByTimeTableReference(timeTableReference);

        for (RRBooking rrBooking : rrBookingList) {
            // Delete each RRBooking object
            try {
                rrBookingRepo.deleteById(rrBooking.getRrBookingId());
            } catch (Exception e) {
                // Handle deletion failure if needed
                e.printStackTrace();
            }
        }
    }
}
