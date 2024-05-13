package com.microservice.learnerService.controller;

import com.microservice.learnerService.exception.StudentEnrollmentCollectionException;
import com.microservice.learnerService.model.data.StudentEnrollment;
import com.microservice.learnerService.service.StudentEnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.microservice.learnerService.security.JwtUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enrollment")
public class StudentEnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;

    @Autowired
    private JwtUtils jwtUtils;
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

    @GetMapping("getSingleStudentEnrollmentCourse/{code}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getSingleStudentEnrollmentCourse(HttpServletRequest request, HttpServletResponse response, @PathVariable("code") String code) {

        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username" + username);

        // Call the service method to retrieve the single StudentEnrollment
        StudentEnrollment studentEnrollment = studentEnrollmentService.getSingleStudentEnrollmentCourse(code, username);

        // Check if the StudentEnrollment exists
        if (studentEnrollment != null) {
            return new ResponseEntity<>(studentEnrollment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student enrollment not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('FACULTY', 'STUDENT')")
    public ResponseEntity<?> studentEnrollment(HttpServletRequest request, HttpServletResponse response, @RequestBody StudentEnrollment studentEnrollment) {
        try {
            String jwtToken = request.getHeader("Authorization");
            System.out.println(jwtToken);

            String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            System.out.println("Username" + username);

            studentEnrollmentService.createStudentEnrollment(username, studentEnrollment);
            return new ResponseEntity<>(studentEnrollment, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (StudentEnrollmentCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

//    @GetMapping("/{code}/{period}")
//    @PreAuthorize("hasRole('STUDENT')")
//    public ResponseEntity<?> getSignedInStudentEnrollmentCourse(@PathVariable("code") String code,
//                                                                @PathVariable("period") String period,
//                                                                @CookieValue(JwtUtils.USERNAME_COOKIE_NAME) String userName)
//            throws StudentEnrollmentCollectionException {
//        return new ResponseEntity<>(studentEnrollmentService.getSingleStudentEnrollment(code, userName, period),
//                HttpStatus.OK);
//    }
    @GetMapping("/{code}/{student}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getSingleEnrollment(@PathVariable("code") String code,
                                             @PathVariable("student") String student) throws StudentEnrollmentCollectionException {
    	StudentEnrollment enrollment = studentEnrollmentService.getSingleEnrollment(code, student);
        if (enrollment != null) {
            return ResponseEntity.ok("TRUE");
        } else {
            // If no enrollment is found, you may want to return a different response
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/myenrollment")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getSingleStudentEnrollment(HttpServletRequest request, HttpServletResponse response)
            throws StudentEnrollmentCollectionException {

        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username"+username);

        return new ResponseEntity<>(studentEnrollmentService.getSingleStudentEnrollment(username),
                HttpStatus.OK);
    }

    @DeleteMapping("/{code}/{student}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> deleteStudentEnrollment(@PathVariable("code") String code,
                                          @PathVariable("student") String student)
            throws StudentEnrollmentCollectionException {
        studentEnrollmentService.deleteStudentEnrollment(code, student);
        return ResponseEntity.ok()
                .body("Successfully Deleted with code " + code + " & student username " + student);
    }
    
    
    
    @PostMapping("/progress")
    public ResponseEntity<String> createInitialProgress(HttpServletRequest request, HttpServletResponse response, @RequestBody StudentEnrollment progressRequest) {
        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username" + username);

        try {
        	studentEnrollmentService.createInitialProgress(
                    username,
                    progressRequest.getCourseId(),
                    progressRequest.getWeekId(),
                    progressRequest.isNotesRead(),
                    progressRequest.isVideoViewed()
            );
            return ResponseEntity.ok("Initial progress created successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create initial progress: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/progress/{courseId}")
    public ResponseEntity<Integer> getProgressCount(HttpServletRequest request, HttpServletResponse response, @PathVariable String courseId) {
        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username" + username);

        Integer progressCount = studentEnrollmentService.getProgresscount(courseId, username);
        if (progressCount != null) {
            return ResponseEntity.ok(progressCount);
        } else {
            // Handle case where enrollment record is not found
            return ResponseEntity.notFound().build();
        }
    }

}
