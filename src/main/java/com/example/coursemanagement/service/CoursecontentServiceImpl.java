package com.example.coursemanagement.service;

import com.example.coursemanagement.modelclass.Course;
import com.example.coursemanagement.modelclass.CourseContentDTO;
import com.example.coursemanagement.modelclass.Coursecontent;
import com.example.coursemanagement.modelclass.Res;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.CoursecontentRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import ch.qos.logback.classic.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CoursecontentServiceImpl {

    @Autowired
    public CoursecontentRepository coursecontentRepo;

    @Autowired
    public CourseRepository courseRepo;


    public void createCourseContent(Coursecontent courseCont) {
        try {

            // Check if a course with the same courseName and instructorId already exists
            Optional<Coursecontent> courseWithCourseIdAndWeekId = coursecontentRepo.findByCourseIdAndWeekId(courseCont.getCourseId(), courseCont.getWeekId());
            if (courseWithCourseIdAndWeekId.isPresent()) {
                return;
            }

            courseCont.setCreatedAt(new Date(System.currentTimeMillis()));

            coursecontentRepo.save(courseCont);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //		public void updateCourseContent(Coursecontent courseCont, String coursecontId) {
//			 try {
//			        Optional<Coursecontent> existingCourse = coursecontentRepo.findById(coursecontId);
//
//			        if (existingCourse.isEmpty()) {
//			            // Course content not found
//			            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course content with ID " + coursecontId + " not found.");
//			        }
//
//			        Coursecontent courseContent = existingCourse.get();
//			        String courseId = courseContent.getCourseId();
//
//			        // Check if course exists
//			        Optional<Course> courseOptional = courseRepo.findById(courseId);
//			        if (courseOptional.isEmpty()) {
//			            // Course not found
//			            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with ID " + courseId + " not found.");
//			        }
//
//			        Course course = courseOptional.get();
//			        String approve = course.getApprove();
//
//			        if ("Approve".equals(approve)) {
//			            // Course is already approved, disallow updating
//			            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update course content for an approved course.");
//			        }
//			        else {
//			        	  // Check if a course with the same courseId and weekId exists
//			            if (existingCourse.isPresent()) {
//			                Coursecontent existingCourseCont = existingCourse.get();
//			                // Update the existing course content
//		                	if ((existingCourseCont.getLectureNotesUrls() == null || existingCourseCont.getLectureNotesUrls().isEmpty()) && courseCont.getLectureNotesUrls() != null && !courseCont.getLectureNotesUrls().isEmpty()) {
//
//			                	existingCourseCont.setLectureNotesUrls(courseCont.getLectureNotesUrls());
//			                }
//			                if (existingCourseCont.getVideoUrls() == null && courseCont.getVideoUrls() != null && !courseCont.getVideoUrls().isEmpty()) {
//			                    existingCourseCont.setVideoUrls(courseCont.getVideoUrls());
//			                }
//			                // Save the updated course content
//			                coursecontentRepo.save(existingCourseCont);
//			            } else {
//			                // If the course content does not exist, create a new one
//			                courseCont.setCreatedAt(new Date(System.currentTimeMillis()));
//			                coursecontentRepo.save(courseCont);
//			            }
//			        }
//			 } catch (Exception e) {
//			        e.printStackTrace();
//			    }
//			}
//
//
    public void updateCourseContent(Coursecontent courseCont, String coursecontId) {
        try {
            Optional<Coursecontent> existingCourse = coursecontentRepo.findById(coursecontId);

            if (existingCourse.isEmpty()) {
                // Course content not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course content with ID " + coursecontId + " not found.");
            }

            Coursecontent courseContent = existingCourse.get();
            String courseId = courseContent.getCourseId();

            // Check if course exists
            Optional<Course> courseOptional = courseRepo.findById(courseId);
            if (courseOptional.isEmpty()) {
                // Course not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with ID " + courseId + " not found.");
            }

            Course course = courseOptional.get();
            String approve = course.getApprove();

            if ("Approved".equals(approve)) {
                // Course is already approved, disallow updating
                if (existingCourse.isPresent()) {
                    Coursecontent existingCourseCont = existingCourse.get();
                    // Update the existing course content
                    if ((existingCourseCont.getLectureNotesUrls() == null || existingCourseCont.getLectureNotesUrls().isEmpty()) && courseCont.getLectureNotesUrls() != null && !courseCont.getLectureNotesUrls().isEmpty()) {

                        existingCourseCont.setLectureNotesUrls(courseCont.getLectureNotesUrls());
                    }
                    if ((existingCourseCont.getVideoUrls() == null || existingCourseCont.getVideoUrls().isEmpty()) && courseCont.getVideoUrls() != null && !courseCont.getVideoUrls().isEmpty()) {
                        existingCourseCont.setVideoUrls(courseCont.getVideoUrls());
                    }
                    // Save the updated course content
                    coursecontentRepo.save(existingCourseCont);
                } else {
                    // If the course content does not exist, create a new one
                    courseCont.setUpdatedAt(new Date(System.currentTimeMillis()));
                    coursecontentRepo.save(courseCont);
                }
//			            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update course content for an approved course.");
            } else {
                Coursecontent existingCourseCont = existingCourse.get();
                existingCourseCont.setLectureNotesUrls(courseCont.getLectureNotesUrls());
                existingCourseCont.setVideoUrls(courseCont.getVideoUrls());
                coursecontentRepo.save(existingCourseCont);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Coursecontent> getAllCourseContents() {
        List<Coursecontent> coursesContent = coursecontentRepo.findAll();
        if (coursesContent.size() > 0) {
            return coursesContent;
        } else {
            return new ArrayList<Coursecontent>();
        }
    }


    public void deleteByCourseContentid(String courseContentId) {
        Optional<Coursecontent> courseContOpt = coursecontentRepo.findByCourseContentId(courseContentId);


        if (!courseContOpt.isPresent()) {
            log.warn("Mentioned CourseId '{}' is not available in the DB", courseContentId);

        } else {
            coursecontentRepo.deleteByCourseContentId(courseContentId);
        }

    }

    //Count of PDF & video & total sum
    public Map<String, Integer> getCountOfPDFsAndVideosByCourse(String courseId) {
        Map<String, Integer> counts = new HashMap<>();

        // Fetch course contents for the specified courseId
        List<Coursecontent> courseContents = coursecontentRepo.findByCourseId(courseId);

        int pdfCount = 0;
        int videoCount = 0;

        for (Coursecontent content : courseContents) {
            String pdfUrl = content.getLectureNotesUrls();
            String videoUrl = content.getVideoUrls();

            // Increment counts based on the presence of PDF and video URLs
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                pdfCount++;
            }
            if (videoUrl != null && !videoUrl.isEmpty()) {
                videoCount++;
            }
        }

        // Put counts into the map
        counts.put("pdfCount", pdfCount);
        counts.put("videoCount", videoCount);
        counts.put("totalContent", pdfCount + videoCount);

        return counts;
    }


    // Sum Count only

    public Map<String, Integer> getSumOfContentByCourse() {
        Map<String, Integer> courseContentSum = new HashMap<>();

        List<Coursecontent> courseContents = coursecontentRepo.findAll();

        for (Coursecontent content : courseContents) {
            String courseId = content.getCourseId();
            String pdfUrl = content.getLectureNotesUrls();
            String videoUrl = content.getVideoUrls();

            // Calculate total content for this course
            int totalContent = 0;
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                totalContent++;
            }
            if (videoUrl != null && !videoUrl.isEmpty()) {
                totalContent++;
            }

            // Update total content count for this course
            courseContentSum.put(courseId, courseContentSum.getOrDefault(courseId, 0) + totalContent);
        }

        return courseContentSum;
    }

    public Map<String, Object> getCourseContentByCourseId(String courseId) {
        Optional<Course> courseOptional = courseRepo.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with ID " + courseId + " not found.");
        }
        Course course = courseOptional.get();
        List<Coursecontent> allCourseContent = coursecontentRepo.findByCourseId(courseId);
        Map<String, List<CourseContentDTO>> courseContentMap = new HashMap<>();

        // Group course content by week
        for (Coursecontent content : allCourseContent) {
            String weekId = content.getWeekId();
            CourseContentDTO contentDTO = new CourseContentDTO();
            contentDTO.setCourseContentId(content.getCourseContentId());
            contentDTO.setWeekTitle(content.getWeekTitle());
            contentDTO.setWeekDescription(content.getWeekDescription());
            contentDTO.setVideoUrls(content.getVideoUrls());
            contentDTO.setLectureNotesUrls(content.getLectureNotesUrls());

            if (!courseContentMap.containsKey(weekId)) {
                List<CourseContentDTO> contentList = new ArrayList<>();
                contentList.add(contentDTO);
                courseContentMap.put(weekId, contentList);
            } else {
                courseContentMap.get(weekId).add(contentDTO);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("courseDetails", course);
        result.put("courseContent", courseContentMap);

        return result;
    }


    public Map<String, List<String>> getWeekDetailsForCourse(String courseId, String weekId) {
        Optional<Coursecontent> courseContentForWeek = coursecontentRepo.findByCourseIdAndWeekId(courseId, weekId);
        Map<String, List<String>> weekDetailsMap = new HashMap<>();

        // Ensure that course content is found for the given weekId
        if (!courseContentForWeek.isEmpty()) {
            Coursecontent content = courseContentForWeek.get(); // Assuming only one entry per weekId
            String courseContentId = content.getCourseContentId();
            String lectureURL = content.getLectureNotesUrls();
            String videoURL = content.getVideoUrls();


            List<String> weekDetails = new ArrayList<>();
            weekDetails.add(courseId);
            weekDetails.add(lectureURL);
            weekDetails.add(videoURL);

            weekDetailsMap.put(weekId, weekDetails);
        }

        return weekDetailsMap;
    }


    public void deleteCourseContent(String courseContentId) {
        // Check if course content exists
        Optional<Coursecontent> courseContentOptional = coursecontentRepo.findById(courseContentId);
        if (courseContentOptional.isEmpty()) {
            // Course content not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course content with ID " + courseContentId + " not found.");
        }

        Coursecontent courseContent = courseContentOptional.get();
        String courseId = courseContent.getCourseId();

        // Check if course exists
        Optional<Course> courseOptional = courseRepo.findById(courseId);
        if (courseOptional.isEmpty()) {
            // Course not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with ID " + courseId + " not found.");
        }

        Course course = courseOptional.get();
        String approve = course.getApprove();

        if ("Approved".equals(approve)) {
            // Can't delete course content if course is approved or approval status is null
            throw new IllegalArgumentException("Can't delete course content for an approved course or a course with null approval status.");
        } else {
            // Delete the course content
            coursecontentRepo.delete(courseContent);
            System.out.println("Course content with ID " + courseContentId + " deleted successfully.");
        }
    }


    public void deleteSingleCourseContent(Coursecontent courseCont, String coursecontId, Boolean dellectureNotesUrls, Boolean delvideoUrls) {
        try {
            Optional<Coursecontent> existingCourse = coursecontentRepo.findById(coursecontId);

            if (existingCourse.isEmpty()) {
                // Course content not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course content with ID " + coursecontId + " not found.");
            }

            Coursecontent courseContent = existingCourse.get();
            String courseId = courseContent.getCourseId();

            // Check if course exists
            Optional<Course> courseOptional = courseRepo.findById(courseId);
            if (courseOptional.isEmpty()) {
                // Course not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with ID " + courseId + " not found.");
            } else {

                Course course = courseOptional.get();
                String approve = course.getApprove();

                if ("Approve".equals(approve)) {
                    // Course is already approved, disallow updating
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update course content for an approved course.");
                } else {
                    Coursecontent existingCourseCont = existingCourse.get();
                    // Check if a course with the same courseId and weekId exists
                    if (Boolean.TRUE.equals(dellectureNotesUrls)) {
                        existingCourseCont.setLectureNotesUrls(null);
                    }
                    if (Boolean.TRUE.equals(delvideoUrls)) {
                        existingCourseCont.setVideoUrls(null);
                    }
                    existingCourseCont.setUpdatedAt(new Date(System.currentTimeMillis())); // Set updatedAt field
                    coursecontentRepo.save(existingCourseCont); // Save the updated course content
                    // Log the updated values
                    System.out.println("Updated LectureNotesUrls: " + existingCourseCont.getLectureNotesUrls());
                    System.out.println("Updated VideoUrls: " + existingCourseCont.getVideoUrls());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
