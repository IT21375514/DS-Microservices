package com.example.coursemanagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.coursemanagement.modelclass.Course;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.CoursecontentRepository;

@Service
public class CourseserviceImpl implements Courseservice {
	
    private static final Logger logger = LoggerFactory.getLogger(CourseserviceImpl.class);
	
	@Autowired
	public CourseRepository courseRepo;
	
	@Autowired
	public CoursecontentRepository courseContRepo;

	public CourseserviceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createCourse(Course course){
	    try {
	        // Check if the course already exists by courseId
	        Optional<Course> existingCourseOptional = courseRepo.findByCourseId(course.getCourseId());
	        if (existingCourseOptional.isPresent()) {
	            return;
	        }
	        if (course.getApprove() == null) {
	        	course.setApprove("Pending");
	        }
	        // Check if a course with the same courseName and instructorId already exists
	        Optional<Course> courseWithSameNameAndInstructor = courseRepo.findByCourseNameAndInstructorId(course.getCourseName(), course.getInstructorId());
	        if (courseWithSameNameAndInstructor.isPresent()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course with the same name and instructor already exists.");
	        }
	        course.setCreatedAt(new Date(System.currentTimeMillis()));

	        courseRepo.save(course);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public List<Course> getAllCourses() {
		List<Course> courses= courseRepo.findAll();
		if(courses.size()>0) {
			return courses;
		}else {
			return new ArrayList<Course>();
		}
	}
	
	@Override
	public void deleteByCourseCode(String courseId) {
	    // Check if course exists
	    Optional<Course> courseOptional = courseRepo.findByCourseId(courseId);
	    if (courseOptional.isEmpty()) {
	        // Course not found
            throw new NoSuchElementException("Course with ID " + courseId + " not found.");
	    }

	    Course course = courseOptional.get();
	    String approve = course.getApprove();
	    if ("Approved".equals(approve)) {
	        // Can't delete approved courses or courses with null approval status
	        System.out.println("Can't delete approved courses or courses with null approval status.");
	        return;
	    }

	    // Delete the course
	    courseRepo.delete(course);
	    System.out.println("Course with ID " + courseId + " deleted successfully.");

	 // Delete related courseContent entries
	    courseContRepo.deleteByCourseId(courseId);
        System.out.println("Related course content for course with ID " + courseId + " deleted successfully.");
	}
	
	@Override
	public void updateCourse(String courseId, Course course){
		
		Optional<Course> courseOptId = courseRepo.findById(courseId);
		Optional<Course> courseOptCode = courseRepo.findByCourseId(course.getCourseId());
		if (courseOptId.isPresent()) {
        	if(courseOptCode.isPresent()&& !courseOptCode.get().getCourseId().equals(courseId)) {
        		 logger.warn("Mentioned CourseId '{}' is not available in the DB", courseId);
               

        	}
        	
            Course courseUpdate= courseOptId.get();
            
            courseUpdate.setCourseName(course.getCourseName());           
            courseUpdate.setDescription(course.getDescription());
            courseUpdate.setCourseAmount(course.getCourseAmount());
            courseUpdate.setApprove(course.getApprove());
            courseUpdate.setCourseEnrollKey(course.getCourseEnrollKey());
            courseUpdate.setWeekCount(course.getWeekCount());
            courseUpdate.setUpdatedAt  (new Date(System.currentTimeMillis()));
            courseRepo.save(courseUpdate);
        } else {
   		 logger.warn("Mentioned CourseId '{}' is not available in the DB", courseId);

           
        }  
        }


	public void updateApproveStatusCourse(String courseId, Course course){

		Optional<Course> courseOptId = courseRepo.findById(courseId);
		Optional<Course> courseOptCode = courseRepo.findByCourseId(course.getCourseId());
		if (courseOptId.isPresent()) {
			if(courseOptCode.isPresent()&& !courseOptCode.get().getCourseId().equals(courseId)) {
				logger.warn("Mentioned CourseId '{}' is not available in the DB", courseId);
			}

			Course courseUpdate= courseOptId.get();
			courseUpdate.setApprove(course.getApprove());

			courseUpdate.setUpdatedAt  (new Date(System.currentTimeMillis()));
			courseRepo.save(courseUpdate);
		} else {
			logger.warn("Mentioned CourseId '{}' is not available in the DB", courseId);


		}
	}


	public Course getCourse(String courseId) {
		Optional<Course> courseOpt = courseRepo.findByCourseId(courseId);
		if(!courseOpt.isPresent()) {
			logger.warn("Mentioned CourseId '{}' is not available in the DB", courseId);
			return null;
		}else {
			return courseOpt.get();
		}
	}
	
	public Double getCourseAmount(String courseId) {
	    Optional<Course> courseOpt = courseRepo.findByCourseId(courseId);
	    if (!courseOpt.isPresent()) {
	        logger.warn("Mentioned CourseId '{}' is not available in the DB", courseId);
	        return null;
	    } else {
	        Course course = courseOpt.get();
	        return course.getCourseAmount();
	    }
	}
	
	
//	public List<Course> getAllApprovedCourses() {
//	    return courseRepo.findByApproveTrue();
//	}


@Override
	public List<Course> getAllCoursesByApprove(String approve) {
		List<Course> courses= courseRepo.findByApprove(approve);
		if(courses.size()>0) {
			return courses;
		}else {
			return new ArrayList<Course>();
		}
	}

	@Override
	public List<Course> findByInstructorId(String user) {
		List<Course> courses= courseRepo.findByInstructorId(user);
		if(courses.size()>0) {
			return courses;
		}else {
			return new ArrayList<Course>();
		}
	}


@Override
public Course getCourseByApproveAndInstructorId(String approve, String instructorId) {
    Optional<Course> courseOpt = courseRepo.findByApproveAndInstructorId(approve, instructorId);
    if (courseOpt.isPresent()) {
        return courseOpt.get();
    } else {
        logger.warn("Course with approval status '{}' and instructor ID '{}' not found in the DB", approve, instructorId);
        return null;
    }
}


@Override
public List<String> getCourseIds() {
    List<Course> courses = courseRepo.findAll();
    List<String> courseIds = new ArrayList<>();
    
    for (Course course : courses) {
        courseIds.add(course.getCourseId());
    }
    
    return courseIds;
}
	
	

}
