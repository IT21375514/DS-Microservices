package com.microservice.learnerService.service;


import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.learnerService.exception.StudentEnrollmentCollectionException;
import com.microservice.learnerService.model.data.StudentEnrollment;
import com.microservice.learnerService.model.data.StudentEnrollmentId;
import com.microservice.learnerService.repository.StudentEnrollmentRepo;

import ch.qos.logback.classic.Logger;
import jakarta.validation.ConstraintViolationException;

@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {

	
	
    private final WebClient webClient;
    
    @Autowired
	public StudentEnrollmentRepo studentEnrollmentRepo;
    
    @Autowired
    public StudentEnrollmentServiceImpl(WebClient webClient, StudentEnrollmentRepo studentEnrollmentRepo) {
        this.webClient = webClient;
        this.studentEnrollmentRepo = studentEnrollmentRepo;
    }
   
  
    @Override
    public void createStudentEnrollment(String username, StudentEnrollment studentEnrollment) throws ConstraintViolationException, StudentEnrollmentCollectionException {
    	  String courseIdJson = webClient.get()
    	            .uri("http://host.docker.internal:8082/api/course/{courseId}/content", studentEnrollment.getCourseId())
    	            .retrieve()
    	            .bodyToMono(String.class)
    	            .block();

    	        // Parse JSON response to extract courseEnrollKey
    	        ObjectMapper objectMapper = new ObjectMapper();
    	        String currentCourse = null;

    	        try {
    	        	JsonNode courseDetailsNode = objectMapper.readTree(courseIdJson).get("courseDetails");
    	            if (courseDetailsNode != null) {
    	                currentCourse = courseDetailsNode.get("courseId").asText();
    	            }
    	        } catch (IOException e) {
    	            // Handle JSON parsing exception
    	            e.printStackTrace();
    	        }


    	                String currentStudent1 = studentEnrollment.getId().getStudentUserName();
                        String currentStudent = username;
                        String myCourse = studentEnrollment.getCourseId();
                        studentEnrollment.setId(new StudentEnrollmentId(myCourse,username));

    	                if (currentCourse != null && currentStudent != null) {
    	                    StudentEnrollmentId id = new StudentEnrollmentId();
    	                    id.setCode(currentCourse); // Set courseEnrollKey
    	                    id.setStudentUserName(currentStudent);

    	                    Optional<StudentEnrollment> studentEnrollmentIdName = studentEnrollmentRepo.findById(id);
    	                    if (studentEnrollmentIdName.isPresent()) {
    	                        throw new StudentEnrollmentCollectionException(StudentEnrollmentCollectionException.StudentAlreadyExist());
    	                    } else {
    	                    	 // Proceed with creating the student enrollment
    	                        studentEnrollment.getId().setCode(currentCourse); // Set courseEnrollKey in id object
    	                        studentEnrollment.setCreated(new Date(System.currentTimeMillis()));
    	                        studentEnrollmentRepo.save(studentEnrollment);
    	                    }

    	                    throw new StudentEnrollmentCollectionException("Course ID: " + currentCourse + ", Student User Name: " + currentStudent);
    	                } else {
    	                    throw new StudentEnrollmentCollectionException(StudentEnrollmentCollectionException.MissingData());
    	                }
    	                
    }
    	    
    

    @Override
    public List<StudentEnrollment> getAllStudentEnrollment() {
        List<StudentEnrollment> studentEnrollment = studentEnrollmentRepo.findAll();
        return !studentEnrollment.isEmpty() ? studentEnrollment : Collections.emptyList();
    }

    @Override
    public StudentEnrollment getSingleStudentEnrollmentCourse(String code, String studentUserName) {
        Optional<StudentEnrollment> studentEnrollmentOptional = studentEnrollmentRepo.findByCodeAndStudentUserName(code, studentUserName);
        return studentEnrollmentOptional.orElse(null);
    }

    @Override
    public StudentEnrollment getSingleEnrollment(String code, String student) throws StudentEnrollmentCollectionException {
        StudentEnrollmentId studentEnrollmentId = StudentEnrollmentId.builder()
                                            .code(code)
                                            .studentUserName(student)
                                            .build();
        return studentEnrollmentRepo
                .findById(studentEnrollmentId)
                .orElseThrow(() -> new StudentEnrollmentCollectionException(
                        StudentEnrollmentCollectionException.NotFoundException(code, student)));
    }

    
    @Override
    public List<StudentEnrollment> getSingleStudentEnrollment(String student) throws StudentEnrollmentCollectionException {
        List<StudentEnrollment> enrollments = studentEnrollmentRepo.findByCurrentUsereId(student);
        if (!enrollments.isEmpty()) {
            return enrollments; // Return all enrollments for the student
        } else {
            throw new StudentEnrollmentCollectionException(
                    StudentEnrollmentCollectionException.NotFoundException(student));
        }
    }


    @Override
    public void deleteStudentEnrollment(String code, String student) throws StudentEnrollmentCollectionException {
        StudentEnrollmentId id = StudentEnrollmentId.builder().code(code).studentUserName(student).build();
        Optional<StudentEnrollment> studentEnrollmentWithName = studentEnrollmentRepo.findById(id);

        if (studentEnrollmentWithName.isPresent()) {
            studentEnrollmentRepo.deleteById(id);
        } else {
            throw new StudentEnrollmentCollectionException(
                    StudentEnrollmentCollectionException.NotFoundException(code, student));
        }
    }
    
    
    public void createInitialProgress(String studentUserName, String courseId, String weekId, boolean lectureViewed, boolean videoViewed) {
        org.slf4j.Logger logger = LoggerFactory.getLogger("InitialProgressLogger");

        // Fetch the course data from the external API
        String courseIdJson = webClient.get()
                .uri("http://host.docker.internal:8082/api/course/{courseId}/{weekId}", courseId, weekId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode courseNode = objectMapper.readTree(courseIdJson);

            // Extract the courseId from the JSON response
            String fetchedCourseId = courseId; // Assuming courseId is already provided in the request
            logger.info("Fetched courseId: {}", fetchedCourseId);

            // Log the fetched weekId
            logger.info("Fetched weekId: {}", weekId);

            // Check if the progress already exists for the student and course
            Optional<StudentEnrollment> existingProgress = studentEnrollmentRepo.findByStudentIdAndCourseId(fetchedCourseId, studentUserName);
            logger.info("Fetched studentUserName: {}", studentUserName);

            if (!existingProgress.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student or course not found");
            }

            // Update the progress with the viewed status for the specified week
            StudentEnrollment progress = existingProgress.get();
            Map<String, Map<String, Boolean>> courseContent = progress.getCourseContent();
            if (courseContent == null) {
                courseContent = new HashMap<>();
            }
            Map<String, Boolean> weekContent = new HashMap<>();
            weekContent.put("lectureUrlViewed", lectureViewed);
            weekContent.put("videoUrlViewed", videoViewed);
            courseContent.put(weekId, weekContent);
            progress.setCourseContent(courseContent);
            
         // Count the total number of true values in the courseContent map
            int progressCount = countTrueValues(courseContent);
            logger.info("Total count of true values in courseContent: {}", progressCount);
            
         // Set the progressCount in the progress object
            progress.setProgressCount(progressCount);

            // Save the updated progress
            studentEnrollmentRepo.save(progress);
        } catch (IOException e) {
            logger.error("Failed to parse course ID JSON: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse course ID JSON", e);
        }
    }
    private int countTrueValues(Map<String, Map<String, Boolean>> courseContent) {
        int trueCount = 0;
        if (courseContent != null) {
            for (Map<String, Boolean> weekContent : courseContent.values()) {
                for (Boolean value : weekContent.values()) {
                    if (value) {
                        trueCount++;
                    }
                }
            }
        }
        return trueCount;
    }
    
    
    public Integer getProgresscount(String courseId, String studentUsername) {
        Optional<StudentEnrollment> existingProgress = studentEnrollmentRepo.findByStudentIdCourse(courseId, studentUsername);
	    if (!existingProgress.isPresent()) {
	    	 Logger logger = (Logger) LoggerFactory.getLogger("ProgressLogger");
	         logger.warn("CourseId '{}' for student '{}' is not available in the DB", courseId, studentUsername);
	        return null;
	    } else {
	    	StudentEnrollment studEnroll = existingProgress.get();
	        return studEnroll.getProgressCount();
	    }
	}







}
