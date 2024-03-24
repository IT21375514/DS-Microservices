package com.universityTimetableManagementSystem.controller;


import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.data.RRBooking;
import com.universityTimetableManagementSystem.model.data.Timetable;
import com.universityTimetableManagementSystem.security.JwtUtils;
import com.universityTimetableManagementSystem.service.RRBookingService;
import com.universityTimetableManagementSystem.service.TimetableService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/tms/timetable")
public class TimetableController {
    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getAllTimetable() throws TimetableCollectionException {
        List<Timetable> timetable = timetableService.getAllTimetable();
        return new ResponseEntity<>(timetable,
                !timetable.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @PostMapping()
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> rrBooking(@RequestBody Timetable timetable, @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName) throws RRBookingCollectionException{
        try {
            timetableService.createTable(timetable, userName);
            return new ResponseEntity<>(timetable, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (RRBookingCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (TimetableCollectionException | CourseCollectionException | CourseFacultyCollectionException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/myself")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getSingleFacultyTimetable(@CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName)
            throws TimetableCollectionException, ParseException {

        return new ResponseEntity<>(timetableService.getSingleFacultyTimetable(userName),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> updateTimetable(@PathVariable("id") String id,
                                             @RequestBody Timetable timetable,
                                             @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName
    )  throws RRBookingCollectionException, ParseException, TimetableCollectionException {
        try {
            timetableService.updateTimetable(id, timetable, userName);
            return new ResponseEntity<>("Update Timetable with code " + id, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (RRBookingCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (TimetableCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> deleteRRBooking(@PathVariable("id") String id,
                                             @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName)
            throws TimetableCollectionException, ParseException, RRBookingCollectionException {

        timetableService.deleteTimetable(id,userName);
        return ResponseEntity.ok()
                .body("Successfully Deleted Timetable " + id);
    }

}
