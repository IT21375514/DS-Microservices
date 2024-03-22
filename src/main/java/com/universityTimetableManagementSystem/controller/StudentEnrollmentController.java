package com.universityTimetableManagementSystem.controller;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.StudentEnrollmentCollectionException;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;
import com.universityTimetableManagementSystem.model.data.StudentEnrollment;
import com.universityTimetableManagementSystem.security.JwtUtils;
import com.universityTimetableManagementSystem.service.CourseFacultyService;
import com.universityTimetableManagementSystem.service.StudentEnrollmentService;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tms/studentEnrollment")
public class StudentEnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;

    public StudentEnrollmentController(StudentEnrollmentService studentEnrollmentService) {
        this.studentEnrollmentService = studentEnrollmentService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getAllStudentEnrollment() {
        List<StudentEnrollment> studentEnrollment = studentEnrollmentService.getAllStudentEnrollment();
        return new ResponseEntity<>(studentEnrollment,
                !studentEnrollment.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @PostMapping()
    @PreAuthorize("hasAnyRole('FACULTY', 'STUDENT')")
    public ResponseEntity<?> studentEnrollment(@RequestBody StudentEnrollment studentEnrollment) {
        try {
            studentEnrollmentService.createStudentEnrollment(studentEnrollment);
            return new ResponseEntity<>(studentEnrollment, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (StudentEnrollmentCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{code}/{period}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getSignedInStudentEnrollmentCourse(@PathVariable("code") String code,
                                                                @PathVariable("period") String period,
                                                                @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName)
            throws StudentEnrollmentCollectionException {
        return new ResponseEntity<>(studentEnrollmentService.getSingleStudentEnrollment(code, userName, period),
                HttpStatus.OK);
    }
    @GetMapping("/{code}/{student}/{period}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getStudentEnrollment(@PathVariable("code") String code,
                                             @PathVariable("student") String student,@PathVariable("period") String period)
            throws StudentEnrollmentCollectionException {
        return new ResponseEntity<>(studentEnrollmentService.getSingleStudentEnrollment(code, student, period),
                HttpStatus.OK);
    }

    @DeleteMapping("/{code}/{student}/{period}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> deleteStudentEnrollment(@PathVariable("code") String code,
                                          @PathVariable("student") String student, @PathVariable("period") String period)
            throws StudentEnrollmentCollectionException {
        studentEnrollmentService.deleteStudentEnrollment(code, student, period);
        return ResponseEntity.ok()
                .body("Successfully Deleted with code " + code + " & student username " + student+ " of " + period);
    }

}
