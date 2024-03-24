package com.universityTimetableManagementSystem.controller;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;
import com.universityTimetableManagementSystem.service.CourseFacultyService;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tms/courseFaculty")
public class CourseFacultyController {

  private final CourseFacultyService courseFacultyService;

  public CourseFacultyController(CourseFacultyService courseFacultyService) {
    this.courseFacultyService = courseFacultyService;
  }

  @GetMapping()
  @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
  public ResponseEntity<?> getAllTodos() {
    List<CourseFaculty> courseFaculty = courseFacultyService.getAllCourseFaculty();
    return new ResponseEntity<>(courseFaculty,
        !courseFaculty.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }


  @PostMapping()
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createCourse(@RequestBody CourseFaculty courseFaculty) {
    try {
      courseFacultyService.createCourseFaculty(courseFaculty);
      return new ResponseEntity<>(courseFaculty, HttpStatus.OK);
    } catch (ConstraintViolationException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    } catch (CourseFacultyCollectionException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
  }

  @GetMapping("/{code}/{faculty}")
  @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
  public ResponseEntity<?> getSingleCourse(@PathVariable("code") String code,
                                           @PathVariable("faculty") String faculty)
      throws CourseFacultyCollectionException {
    return new ResponseEntity<>(courseFacultyService.getSingleCourseFaculty(code, faculty),
        HttpStatus.OK);
  }

//	@PutMapping("/courseFaculty/{code}")
//	public ResponseEntity<?> updateByCode(@PathVariable("code") String code, @RequestBody CourseFaculty courseFaculty) {
//		
//		try {
//			courseFacultyService.updateCourseFaculty(code, courseFaculty);
//			return new ResponseEntity<>("Update Course with Faculty "+code, HttpStatus.OK);
//		}catch(ConstraintViolationException e){
//			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
//		}catch(CourseFacultyCollectionException e){
//			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//	
//		}
//	}

  @DeleteMapping("/{code}/{facultyCode}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteByCode(@PathVariable("code") String code,
                                        @PathVariable("facultyCode") String facultyCode)
      throws CourseFacultyCollectionException {
    courseFacultyService.deleteCourseFaculty(code, facultyCode);
    return ResponseEntity.ok()
        .body("Successfully Deleted with code " + code + " & Faculty " + facultyCode);
  }

}
