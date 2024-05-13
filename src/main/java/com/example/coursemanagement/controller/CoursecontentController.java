package com.example.coursemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import com.example.coursemanagement.modelclass.Course;
import com.example.coursemanagement.modelclass.CourseContentDTO;
import com.example.coursemanagement.modelclass.Coursecontent;
import com.example.coursemanagement.modelclass.Res;
import com.example.coursemanagement.repository.CoursecontentRepository;
import com.example.coursemanagement.service.Courseservice;
import com.google.api.services.drive.Drive;
import com.example.coursemanagement.service.CoursecontentServiceImpl;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CoursecontentController {

    @Autowired
    private CoursecontentServiceImpl courseContService;
    
    @Autowired
    private CoursecontentRepository coursecontRepo;
    
 // Inject CourseContentService dependency
    @Autowired
    public CoursecontentController(CoursecontentServiceImpl courseContService) {
        this.courseContService = courseContService;
    }

    @GetMapping("/courseContent")
	   // @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<?> getAllCourseContents() {
	        List<Coursecontent> courseCont = courseContService.getAllCourseContents();
	        return new ResponseEntity<>(courseCont, courseCont.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	    }
//    
    
    
//    ***************************************************either lectureNote or Video URL  only
    @DeleteMapping("/courseContent/{courseContentId}")
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteCourseByCid(@PathVariable("courseContentId") String courseContentId) {
		try {
			courseContService.deleteByCourseContentid(courseContentId);
	        return new ResponseEntity<>("Course with ID " + courseContentId + " deleted successfully", HttpStatus.OK);
		} catch (Exception e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}


	 @PostMapping("/course-content")
	    public ResponseEntity<?> createCourseContent(@RequestBody @Validated Coursecontent courseCont, BindingResult result) {
	        // Check for validation errors
	        if (result.hasErrors()) {
	            return ResponseEntity.badRequest().body("Validation errors: " + result.getAllErrors());
	        }

	        // Generate unique courseId
	        String courseContentId = UUID.randomUUID().toString();

	        // Set courseId
	        courseCont.setCourseContentId(courseContentId);

	        // Set createdAt field
	        courseCont.setCreatedAt(new Date());

	        // Call the service method to create the course
	        try {
	            courseContService.createCourseContent(courseCont);
	            // Return success response
	            return ResponseEntity.status(HttpStatus.CREATED)
	                    .body(Map.of("message", "CourseContent added successfully", "success", true));
	        } catch (Exception e) {
	            // Return error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("message", "Error adding Course content: " + e.getMessage(), "success", false));
	        }
	    }
    
	 @PutMapping("/course-content/{courseContentId}")
	 public ResponseEntity<?> updateCourseContent(@PathVariable String courseContentId, @Validated @RequestBody Coursecontent courseCont, BindingResult result) {
	     // Check for validation errors
	     if (result.hasErrors()) {
	         return ResponseEntity.badRequest().body("Validation errors: " + result.getAllErrors());
	     }

	     // Call the service method to update the course content
	     try {
	         courseContService.updateCourseContent(courseCont, courseContentId);
	         // Return success response
	         return ResponseEntity.ok().body(Map.of("message", "CourseContent updated successfully", "success", true));
	     } catch (Exception e) {
	         // Return error response
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                 .body(Map.of("message", "Error updating Course content: " + e.getMessage(), "success", false));
	     }
	 }
	 
	 @GetMapping("/course-content/{courseId}")
	 public ResponseEntity<?> getCountOfPDFsAndVideosByCourse(@PathVariable String courseId) {
	     try {
	         Map<String, Integer> counts = courseContService.getCountOfPDFsAndVideosByCourse(courseId);
	         return ResponseEntity.ok(counts);
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body("Error retrieving count of PDFs and videos: " + e.getMessage());
	     }
	 }

	 
	 
	 @GetMapping("/course/{courseId}/content")
	    public ResponseEntity<?> getCourseContentByCourseId(@PathVariable("courseId") String courseId) {
	        try {
	            // Call service method to get course content
	            Map<String, Object> courseContentMap = courseContService.getCourseContentByCourseId(courseId);
	            return ResponseEntity.ok(courseContentMap);
	        } catch (ResponseStatusException e) {
	            // Return 404 if course not found
	            throw e;
	        } catch (Exception e) {
	            // Return 500 for other exceptions
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("message", e.getMessage()));
	        }
	    }
	 
	 @GetMapping("/course/{courseId}/{weekId}")
	    public ResponseEntity<?> getCourseContentWeekDescription(@PathVariable("courseId") String courseId, @PathVariable("weekId") String weekId) {
	        try {
	            Map<String, List<String>> courseContentMap = courseContService.getWeekDetailsForCourse(courseId, weekId);
	            return ResponseEntity.ok(courseContentMap);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body(Map.of("message", e.getMessage()));
	        }
	    }
	 
	 
	 @DeleteMapping("/course-content/{courseContentId}")
	    public ResponseEntity<String> deleteCourseContent(@PathVariable("courseContentId") String courseContentId) {
	        try {
	            courseContService.deleteCourseContent(courseContentId);
	            return new ResponseEntity<>("Course content with ID " + courseContentId + " deleted successfully", HttpStatus.OK);
	        } catch (IllegalArgumentException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	        } catch (ResponseStatusException e) {
	            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
	        }
	    }
	 @PutMapping("/course-content/single-content/{courseContentId}")
	 public ResponseEntity<?> deleteCoursesingleContent(@PathVariable("courseContentId") String courseContentId,
	                                                    @RequestBody Map<String, Boolean> requestBody) {
	     Boolean dellectureNotesUrls = requestBody.get("dellectureNotesUrls");
	     Boolean delvideoUrls = requestBody.get("delvideoUrls");

	     // Check if the flags are present in the request body
	     if (dellectureNotesUrls == null || delvideoUrls == null) {
	         return ResponseEntity.badRequest().body("Both deleteLectureNotesUrls and deleteVideoUrls are required in the request body");
	     }

	     try {
	         // Check if the courseContentId is valid
	         if (!coursecontRepo.existsById(courseContentId)) {
	             return ResponseEntity.notFound().build();
	         }

	         Coursecontent courseCont = new Coursecontent(); // Creating a dummy Coursecontent object as it's not required here
	         courseContService.deleteSingleCourseContent(courseCont, courseContentId, dellectureNotesUrls, delvideoUrls);
	         return ResponseEntity.ok("Course content with ID " + courseContentId + " updated successfully");
	     } catch (ResponseStatusException e) {
	         return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
	     }
	 }


}
