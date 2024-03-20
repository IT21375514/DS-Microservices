package com.universityTimetableManagementSystem.controller;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;
import com.universityTimetableManagementSystem.service.CourseService;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tms/courses")
public class CourseController {

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping()
	public ResponseEntity<?> getAllCourses(){
		List<Course> courses = courseService.getAllCourse();
		return new ResponseEntity<>(courses,
            courses.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
	}
	
	
	@PostMapping()
	@PreAuthorize("hasRole('FACULTY')")
	public ResponseEntity<?> createCourse(@RequestBody Course course) {
		try {
			courseService.createCourse(course);
	        return new ResponseEntity<>(course, HttpStatus.OK);
	    } catch (ConstraintViolationException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	    } catch (CourseCollectionException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	    }
	}

	
	@GetMapping("/{code}")
	public ResponseEntity<?> getSingleCourse(@PathVariable("code") String code) {
		
		try {
			return new ResponseEntity<>(courseService.getSingleCourse(code), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/{code}")
	@PreAuthorize("hasRole('FACULTY')")
	public ResponseEntity<?> updateByCode(@PathVariable("code") String code, @RequestBody Course course) {
		
		try {
			courseService.updateCourse(code, course);
			return new ResponseEntity<>("Update Course with code "+code, HttpStatus.OK);
		}catch(ConstraintViolationException e){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}catch(CourseCollectionException e){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	
		}
	}
		

	@DeleteMapping("/{code}")
	@PreAuthorize("hasRole('FACULTY')")
	public ResponseEntity<?> deleteByCode(@PathVariable("code") String code) {
		
		try {
			courseService.deleteCourse(code);
			return new ResponseEntity<>("Successfully Deleted with code "+code, HttpStatus.OK);
		}catch(CourseCollectionException e){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
