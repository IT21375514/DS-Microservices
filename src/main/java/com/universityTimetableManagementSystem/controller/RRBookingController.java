package com.universityTimetableManagementSystem.controller;

import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.StudentEnrollmentCollectionException;
import com.universityTimetableManagementSystem.model.data.RRBooking;
import com.universityTimetableManagementSystem.model.data.StudentEnrollment;
import com.universityTimetableManagementSystem.security.JwtUtils;
import com.universityTimetableManagementSystem.service.RRBookingService;
import com.universityTimetableManagementSystem.service.StudentEnrollmentService;
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
@RequestMapping("/tms/rrbooking")
public class RRBookingController {
    private final RRBookingService rrBookingService;

    public RRBookingController(RRBookingService rrBookingService) {
        this.rrBookingService = rrBookingService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getAllRRBooking() throws RRBookingCollectionException {
        List<RRBooking> rrBooking = rrBookingService.getAllRRBooking();
        return new ResponseEntity<>(rrBooking,
                !rrBooking.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @PostMapping()
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> rrBooking(@RequestBody RRBooking rrBooking, @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName) throws RRBookingCollectionException{
        try {
            rrBookingService.createRRBooking(rrBooking, userName);
            return new ResponseEntity<>(rrBooking, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (RRBookingCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}/{date}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getSingleDateRRBooking(@PathVariable("id") String id,
                                                  @PathVariable("date") String stringDate)
            throws RRBookingCollectionException, ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        stringDate += "T00:00:00.000+00:00";
        Date date = dateFormat.parse(stringDate);

        return new ResponseEntity<>(rrBookingService.getSingleRRBooking(id, date),
                HttpStatus.OK);
    }

    @PutMapping("/{id}/{date}/{start}/{end}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> updateRRBooking(@PathVariable("id") String id,
                                             @PathVariable("date") String stringDate,
                                             @PathVariable("start") LocalTime start,
                                             @PathVariable("end") LocalTime end,
                                             @RequestBody RRBooking rrBooking,
                                             @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName
                                             )  throws RRBookingCollectionException, ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            stringDate += "T00:00:00.000+00:00";
            Date date = dateFormat.parse(stringDate);

            rrBookingService.updateRRBooking(id, date, start, end, rrBooking, userName);
            return new ResponseEntity<>(rrBooking, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (RRBookingCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}/{date}/{start}/{end}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> deleteRRBooking(@PathVariable("id") String id,
                                             @PathVariable("date") String stringDate,
                                             @PathVariable("start") LocalTime start,
                                             @PathVariable("end") LocalTime end,
                                             @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName)
            throws RRBookingCollectionException, ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        stringDate += "T00:00:00.000+00:00";
        Date date = dateFormat.parse(stringDate);

        rrBookingService.deleteRRBooking(id, date, start, end, userName);
        return ResponseEntity.ok()
                .body("Successfully Deleted Room/Resource " + id + " from " + start + " to " + end);
    }

}
