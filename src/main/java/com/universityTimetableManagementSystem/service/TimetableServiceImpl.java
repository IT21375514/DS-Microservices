package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.repository.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TimetableServiceImpl implements TimetableService {

    private final CourseRepo courseRepo;
    private final StudentEnrollmentRepo studentEnrollmentRepo;
    private final RRBookingRepo rrBookingRepo;
    private final UserRepository userRepository;
    private final TimetableRepo timetableRepo;
    private final RRBookingService rrBookingService;
    private final CourseFacultyService courseFacultyService;
    private final  CourseFacultyRepo courseFacultyRepo;
    public TimetableServiceImpl(CourseRepo courseRepo, StudentEnrollmentRepo studentEnrollmentRepo, RRBookingRepo rrBookingRepo, UserRepository userRepository, TimetableRepo timetableRepo, RRBookingService rrBookingService, CourseFacultyService courseFacultyService, CourseFacultyRepo courseFacultyRepo) {
        this.courseRepo = courseRepo;
        this.studentEnrollmentRepo = studentEnrollmentRepo;
        this.rrBookingRepo = rrBookingRepo;
        this.userRepository = userRepository;
        this.timetableRepo = timetableRepo;
        this.rrBookingService = rrBookingService;
        this.courseFacultyService = courseFacultyService;
        this.courseFacultyRepo = courseFacultyRepo;
    }
    @Override
    public void createTable(Timetable timetable, String userName) throws ConstraintViolationException, TimetableCollectionException, RRBookingCollectionException, CourseFacultyCollectionException, CourseCollectionException {

        Optional<Course> courseOptional = courseRepo.findById(timetable.getCode());
        CourseFaculty courseFaculty = courseFacultyService.getSingleCourseFaculty(timetable.getCode(), userName);
        if (courseFaculty != null && courseOptional.isPresent()) {
            timetable.setUpdated(LocalDateTime.now());
            timetable.setFaculty(userName);
            Timetable savedTimetable = timetableRepo.save(timetable);

            String timetableId = savedTimetable.getId();
            String code = timetable.getCode();
            String classRoomResource = timetable.getClassRoomResource();
            Date courseStartDate = timetable.getCourseStartDate();
            int courseDuration = timetable.getCourseDuration();
            LocalTime startTime = timetable.getStartTime();
            LocalTime endTime = timetable.getEndTime();
            String batch = timetable.getBatch();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            boolean error = false;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(courseStartDate);

            for (int i = 0; i < courseDuration; i++) {
                // Calculate the new date by adding i weeks to the start date
                calendar.add(Calendar.WEEK_OF_YEAR, i);
                Date bookingDate = calendar.getTime();

                // Create an instance of RRBooking
                RRBooking rrBooking = new RRBooking();

                // Set RRBookingId
                RRBookingId rrBookingId = new RRBookingId();
                rrBookingId.setRrId(classRoomResource);
                rrBookingId.setDate(bookingDate);
                rrBookingId.setStartTime(startTime);
                rrBookingId.setEndTime(endTime);
                rrBooking.setRrBookingId(rrBookingId);

                // Set other properties of RRBooking
                rrBooking.setFaculty(userName);
                rrBooking.setTimeTableReference(timetableId);

                // Call createRRBooking method for each RRBooking
                try {
                    rrBookingService.createRRBooking(rrBooking, userName);
                } catch (Exception e) {
                    // Handle exceptions
                    error = true;
                }
            }
            if (error) {
                rrBookingService.deleteRRBookingByTimetable(timetableId);
                deleteTimetable(timetableId, userName);
                throw new RRBookingCollectionException("Time slots not available for the classroom");
            }
        } else if (courseFaculty != null) {
            throw new CourseFacultyCollectionException(
                    CourseFacultyCollectionException.NotFoundException(timetable.getCode(), userName));
        } else{
            throw new CourseCollectionException(
                    CourseCollectionException.NotFoundException(timetable.getCode()));
        }
    }

//    @Override
//    public List<RRBooking> getAllRRBooking() throws RRBookingCollectionException {
//        List<RRBooking> rrooking = rrBookingRepo.findAll();
//        return !rrooking.isEmpty() ? rrooking : Collections.emptyList();
//    }
//
//    @Override
//    public List<RRBooking> getSingleRRBooking(String id, Date date) throws RRBookingCollectionException {
//        List<RRBooking> rrooking = rrBookingRepo.findByIdDate(id, date);
//        return !rrooking.isEmpty() ? rrooking : Collections.emptyList();
//    }
//
//    @Override
//    public void updateRRBooking(String id, Date date, LocalTime startTime, LocalTime endTime, RRBooking rrBooking, String userName) throws RRBookingCollectionException {
//
//        RRBookingId rrid1 = RRBookingId.builder().rrId(id).date(date).startTime(startTime).endTime(endTime).build();
//        RRBooking RRBookingWithData = rrBookingRepo.findById(rrid1).orElse(null);
//
//        RRBookingId rrid2 = RRBookingId.builder().rrId(rrBooking.getRrBookingId().getRrId()).date(rrBooking.getRrBookingId().getDate()).startTime(rrBooking.getRrBookingId().getStartTime()).endTime(rrBooking.getRrBookingId().getEndTime()).build();
//        Optional<RRBooking> RRBookingWithNewData = rrBookingRepo.findById(rrid2);
//
//        List<RRBooking> rrookingAll = rrBookingRepo.findByIdDate(rrBooking.getRrBookingId().getRrId(), rrBooking.getRrBookingId().getDate());
//
//        if (RRBookingWithData != null && RRBookingWithNewData.isEmpty() && RRBookingWithData.getFaculty().equalsIgnoreCase(userName)) {
//            boolean isValidAllocation = true;
//
//            for (RRBooking existingBooking : rrookingAll) {
//                // Check if rrBooking overlaps with existingBooking
//                if (!((rrBooking.getRrBookingId().getStartTime().isBefore(existingBooking.getRrBookingId().getStartTime()) && rrBooking.getRrBookingId().getEndTime().isBefore(existingBooking.getRrBookingId().getStartTime())) ||
//                        (rrBooking.getRrBookingId().getStartTime().isAfter(existingBooking.getRrBookingId().getEndTime()) && rrBooking.getRrBookingId().getEndTime().isAfter(existingBooking.getRrBookingId().getEndTime())))
//                ) {
//                    // There is an overlap, set validation to false and break the loop
//                    isValidAllocation = false;
//                    break;
//                }
//            }
//
//            if (isValidAllocation) {
//                // Proceed with allocation
//                deleteRRBooking(id, date, startTime, endTime, userName);
//                createRRBooking(rrBooking, userName);
//            } else {
//                // Cannot allocate resource during this time
//                throw new RRBookingCollectionException(
//                        RRBookingCollectionException.UpdateResourceFailed());
//            }
//        } else if (RRBookingWithData == null) {
//            // No existing bookings for this date, so allocation is valid
//            throw new RRBookingCollectionException(
//                    RRBookingCollectionException.MissingData());
//        } else if (RRBookingWithNewData.isPresent()) {
//            // No existing bookings for this date, so allocation is valid
//            throw new RRBookingCollectionException(
//                    RRBookingCollectionException.ResourceAlreadyExist());
//        } else if (!RRBookingWithData.getFaculty().equalsIgnoreCase(userName)) {
//            // No existing bookings for this date, so allocation is valid
//            throw new RRBookingCollectionException(
//                    RRBookingCollectionException.UpdateFailedFaculty());
//        } else {
//            throw new RRBookingCollectionException(
//                    RRBookingCollectionException.MissingData());
//        }
//    }
//
//    @Override
//    public void deleteRRBooking(String id, Date date, LocalTime startTime, LocalTime endTime, String userName) throws RRBookingCollectionException {
//        RRBookingId rrid = RRBookingId.builder().rrId(id).date(date).startTime(startTime).endTime(endTime).build();
//        Optional<RRBooking> RRBookingWithNewData = rrBookingRepo.findById(rrid);
//
//        if (RRBookingWithNewData.isPresent() && RRBookingWithNewData.get().getFaculty().equalsIgnoreCase(userName)) {
//            rrBookingRepo.deleteById(rrid);
//        } else if (!RRBookingWithNewData.get().getFaculty().equalsIgnoreCase(userName)) {
//            throw new RRBookingCollectionException(
//                    RRBookingCollectionException.UpdateFailedFaculty());
//        } else {
//            throw new RRBookingCollectionException(
//                    RRBookingCollectionException.NotFoundException(id, date, startTime, endTime));
//        }
//    }


    @Override
    public List<Timetable> getAllTimetable() throws TimetableCollectionException {
        List<Timetable> timetable = timetableRepo.findAll();
        return !timetable.isEmpty() ? timetable : Collections.emptyList();
        }

    @Override
    public List<Timetable> getSingleFacultyTimetable(String faculty) throws TimetableCollectionException {
        List<Timetable> timetable = timetableRepo.findByFaculty(faculty);
        return !timetable.isEmpty() ? timetable : Collections.emptyList();
    }


    @Override
    public void updateTimetable(String id, Timetable timetable, String userName) throws TimetableCollectionException, RRBookingCollectionException {

        Optional<Timetable> timetableWithData = timetableRepo.findById(id);
        Date currentDate = new Date();

        if (timetableWithData.isPresent() && timetableWithData.get().getFaculty().equalsIgnoreCase(userName) && timetableWithData.get().getCourseStartDate().before(currentDate)) {

            boolean error = false;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timetable.getCourseStartDate());

            List<RRBooking> rrBookingList1 = rrBookingRepo.findByTimeTableReference(id);
            rrBookingService.deleteRRBookingByTimetable(id);

            for (int i = 0; i < timetable.getCourseDuration(); i++) {
                // Calculate the new date by adding i weeks to the start date
                calendar.add(Calendar.WEEK_OF_YEAR, i);
                Date bookingDate = calendar.getTime();

                // Create an instance of RRBooking
                RRBooking rrBooking = new RRBooking();

                // Set RRBookingId
                RRBookingId rrBookingId = new RRBookingId();
                rrBookingId.setRrId(timetable.getClassRoomResource());
                rrBookingId.setDate(bookingDate);
                rrBookingId.setStartTime(timetable.getStartTime());
                rrBookingId.setEndTime(timetable.getEndTime());
                rrBooking.setRrBookingId(rrBookingId);

                // Set other properties of RRBooking
                rrBooking.setFaculty(userName);
                rrBooking.setTimeTableReference(id);

                // Call createRRBooking method for each RRBooking
                try {
                    rrBookingService.createRRBooking(rrBooking, userName);
                } catch (Exception e) {
                    // Handle exceptions
                    error = true;
                }
            }
            if (error) {
                rrBookingService.deleteRRBookingByTimetable(id);
                for (RRBooking rrBooking1 : rrBookingList1) {
                    rrBookingService.createRRBooking(rrBooking1,rrBooking1.getFaculty());
                }
                throw new RRBookingCollectionException("Time slots not available for the classroom");
            }else{
                Timetable timetableUpdate = timetableWithData.get();
                timetableUpdate.setClassRoomResource(timetable.getClassRoomResource());
                timetableUpdate.setStartTime(timetable.getStartTime());
                timetableUpdate.setEndTime(timetable.getEndTime());
                timetableUpdate.setUpdated(LocalDateTime.now());
                timetableUpdate.setFaculty(userName);
                timetableUpdate.setBatch(timetable.getBatch());
                timetableUpdate.setCode(timetable.getCode());
                timetableUpdate.setCourseDuration(timetable.getCourseDuration());
                timetableUpdate.setCourseStartDate(timetable.getCourseStartDate());
                timetableRepo.save(timetableUpdate);
            }
        } else if (timetableWithData.isEmpty()) {
            throw new TimetableCollectionException(
                    TimetableCollectionException.MissingData());
        } else if(!timetableWithData.get().getFaculty().equalsIgnoreCase(userName)) {
            throw new TimetableCollectionException(
                    TimetableCollectionException.UpdateFailedFaculty());
        } else {
            throw new TimetableCollectionException(
                    TimetableCollectionException.UpdateFailed());
        }
    }



    @Override
    public void deleteTimetable(String id, String faculty) throws TimetableCollectionException, RRBookingCollectionException {

        Optional<Timetable> TimetableWithData = timetableRepo.findById(id);

        if (TimetableWithData.isPresent() && TimetableWithData.get().getFaculty().equalsIgnoreCase(faculty)) {
            timetableRepo.deleteById(id);
            rrBookingService.deleteRRBookingByTimetable(id);
        } else if (!TimetableWithData.get().getFaculty().equalsIgnoreCase(faculty)) {
            throw new TimetableCollectionException(
                    TimetableCollectionException.UpdateFailedFaculty());
        } else {
            throw new TimetableCollectionException(
                    TimetableCollectionException.NotFoundException(id));
        }
    }
}