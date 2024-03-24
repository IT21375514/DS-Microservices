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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class TimetableServiceImpl implements TimetableService {

    private final CourseRepo courseRepo;
    private final StudentEnrollmentRepo studentEnrollmentRepo;
    private final RRBookingRepo rrBookingRepo;
    private final TimetableRepo timetableRepo;
    private final RRBookingService rrBookingService;
    private final EmailService emailService;
    private final CourseFacultyService courseFacultyService;
    private final UserRepository userRepository;


    public TimetableServiceImpl(CourseRepo courseRepo, StudentEnrollmentRepo studentEnrollmentRepo, RRBookingRepo rrBookingRepo, TimetableRepo timetableRepo, RRBookingService rrBookingService, EmailService emailService, CourseFacultyService courseFacultyService, UserRepository userRepository) {
        this.courseRepo = courseRepo;
        this.studentEnrollmentRepo = studentEnrollmentRepo;
        this.rrBookingRepo = rrBookingRepo;
        this.timetableRepo = timetableRepo;
        this.rrBookingService = rrBookingService;
        this.emailService = emailService;
        this.courseFacultyService = courseFacultyService;
        this.userRepository = userRepository;
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
            }else{
                List<StudentEnrollment> studentEnrollment = studentEnrollmentRepo.findByCodeStudents(code,batch);
                if (studentEnrollment.isEmpty()) {
                    System.out.println("No student enrollment data found for the provided code and batch.");
                } else {
                    for (StudentEnrollment enrollment : studentEnrollment) {
                        Optional<User> userIn = userRepository.findByUsername(enrollment.getId().getStudentUserName());
                        String email = userIn.map(User::getEmail).orElse(null);
                        EmailData emailData = new EmailData();
                        emailData.setSendTo(email);
                        emailData.setSubject("Timetable Update");
                        emailData.setBody("Hi Student, There is a timetable update for " + code);
                        emailService.sendTextEmail(emailData);
                    }
                }


            }
        } else if (courseFaculty != null) {
            throw new CourseFacultyCollectionException(
                    CourseFacultyCollectionException.NotFoundException(timetable.getCode(), userName));
        } else{
            throw new CourseCollectionException(
                    CourseCollectionException.NotFoundException(timetable.getCode()));
        }
    }

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