package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;
import com.universityTimetableManagementSystem.repository.CourseRepo;
import jakarta.validation.ConstraintViolationException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {

  private final CourseRepo courseRepo;

  public CourseServiceImpl(CourseRepo courseRepo) {
    this.courseRepo = courseRepo;
  }

  @Override
  public void createCourse(Course course)
      throws ConstraintViolationException, CourseCollectionException {
    Optional<Course> courseOptional = courseRepo.findByCode(course.getCode());
    if (courseOptional.isPresent()) {
      throw new CourseCollectionException(CourseCollectionException.CourseAlreadyExist());
    } else {
      course.setCreated(new Date(System.currentTimeMillis()));
      courseRepo.save(course);
    }
  }

  @Override
  public List<Course> getAllCourse() {
    return courseRepo.findAll();
  }

  @Override
  public Course getSingleCourse(String code) throws CourseCollectionException {
    Optional<Course> optionalCourse = courseRepo.findById(code);
    if (optionalCourse.isEmpty()) {
      throw new CourseCollectionException(CourseCollectionException.NotFoundException(code));
    } else {
      return optionalCourse.get();
    }
  }

  @Override
  public void updateCourse(String code, Course course) throws CourseCollectionException {
    Optional<Course> courseWithCode = courseRepo.findById(code);
    Optional<Course> courseWithSameName = courseRepo.findByCourse(course.getCourseName());
    if (courseWithCode.isPresent()) {

      if (courseWithSameName.isPresent() && !courseWithSameName.get().getCode().equals(code)) {
        throw new CourseCollectionException(CourseCollectionException.CourseAlreadyExist());
      }

      Course courseToUpdate = courseWithCode.get();
      courseToUpdate.setCourseName(course.getCourseName());
      courseToUpdate.setDescription(course.getDescription());
      courseToUpdate.setCredit(course.getCredit());
      courseToUpdate.setUpdated(new Date(System.currentTimeMillis()));
      courseRepo.save(courseToUpdate);
    } else {
      throw new CourseCollectionException(CourseCollectionException.NotFoundException(code));
    }
  }

  @Override
  public void deleteCourse(String id) throws CourseCollectionException {

    Optional<Course> courseWithCode = courseRepo.findById(id);

    if (courseWithCode.isEmpty()) {
      throw new CourseCollectionException(CourseCollectionException.NotFoundException(id));
    } else {
      courseRepo.deleteById(id);
    }


  }

}
