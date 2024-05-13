package com.example.coursemanagement.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;


import com.example.coursemanagement.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.coursemanagement.modelclass.Course;
import com.example.coursemanagement.service.Courseservice;


@RestController
@RequestMapping("/api")
public class CourseController {

    public CourseController() {
        // TODO Auto-generated constructor stub

    }


    @Autowired
    private Courseservice courseService;


    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/course")
    public ResponseEntity<?> createCourse(HttpServletRequest request, HttpServletResponse response, @Validated @RequestBody Course course, BindingResult result) {
        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username" + username);

        // Check for validation errors
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation errors: " + result.getAllErrors());
        }
        // Generate unique courseId
        String courseId = UUID.randomUUID().toString();
        // Set courseId
        course.setCourseId(courseId);
        course.setInstructorId(username);
        // Set createdAt field
        course.setCreatedAt(new Date());
        // Call the service method to create the course
        try {
            courseService.createCourse(course);
            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Course registered successfully", "success", true, "courseId", courseId));
        } catch (Exception e) {
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating Course: " + e.getMessage(), "success", false));
        }
    }


    @GetMapping("/course")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, courses.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<?> deleteCourseByCid(@PathVariable("courseId") String courseId) {
        try {
            courseService.deleteByCourseCode(courseId);
            return new ResponseEntity<>("Course with ID " + courseId + " and related course content deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/course/{courseId}")
//			@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCourseByCid(@PathVariable("courseId") String courseId, @RequestBody Course course) throws SQLIntegrityConstraintViolationException {
        try {
            courseService.updateCourse(courseId, course);
            //return new ResponseEntity<>("Update User with ID : "+id, HttpStatus.OK);
            return ResponseEntity.ok().body(Map.of("message", "Course updated successfully", "success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Course registration unsuccessful: " + e.getMessage(), "success", false));
        }
    }

    @PatchMapping("/course/approve/{courseId}")
//			@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateApproveCourseByCid(@PathVariable("courseId") String courseId, @RequestBody Course course) throws SQLIntegrityConstraintViolationException {
        try {
            courseService.updateApproveStatusCourse(courseId, course);
            //return new ResponseEntity<>("Update User with ID : "+id, HttpStatus.OK);
            return ResponseEntity.ok().body(Map.of("message", "Course updated successfully", "success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Course registration unsuccessful: " + e.getMessage(), "success", false));
        }
    }


    @GetMapping("/course/{courseId}")
//			@PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public ResponseEntity<?> getSingleCourse(@PathVariable("courseId") String courseId) {
        try {
            return new ResponseEntity<>(courseService.getCourse(courseId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage(), "success", false));
        }
    }

    @GetMapping("/course/payment/{courseId}")
    // @PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public ResponseEntity<?> getSingleCourseAmount(@PathVariable("courseId") String courseId) {
        try {
            Double amount = courseService.getCourseAmount(courseId);
            if (amount != null) {
                return ResponseEntity.ok(amount);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage(), "success", false));
        }
    }


    @GetMapping("/course/courseId")
    public ResponseEntity<List<String>> getAllCourseIds() {
        List<String> courseIds = courseService.getCourseIds();
        if (!courseIds.isEmpty()) {
            return new ResponseEntity<>(courseIds, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/course/approve/{approve}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCoursesByApprove(@Validated @PathVariable("approve") String approve) {
        List<Course> approvedCourses = courseService.getAllCoursesByApprove(approve);
        return new ResponseEntity<>(approvedCourses, approvedCourses.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/course/approve/instructorId")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFacultyAllCoursesApprove(HttpServletRequest request, HttpServletResponse response) {

        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username"+username);

        List<Course> approvedCourses = courseService.findByInstructorId(username);
        return new ResponseEntity<>(approvedCourses, approvedCourses.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/course/approve/{approve}/instructorId")
//			@PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public ResponseEntity<?> getCourseByApproveAndInstructorId(HttpServletRequest request, HttpServletResponse response, @PathVariable("approve") String approve) {

        String jwtToken = request.getHeader("Authorization");
        System.out.println(jwtToken);

        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        System.out.println("Username"+username);

        try {
            return new ResponseEntity<>(courseService.getCourseByApproveAndInstructorId(approve, username), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage(), "success", false));
        }
    }


}
