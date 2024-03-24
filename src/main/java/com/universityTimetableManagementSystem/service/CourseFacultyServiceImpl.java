package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;
import com.universityTimetableManagementSystem.model.data.CourseFacultyId;
import com.universityTimetableManagementSystem.model.data.User;
import com.universityTimetableManagementSystem.repository.CourseFacultyRepo;
import com.universityTimetableManagementSystem.repository.CourseRepo;
import com.universityTimetableManagementSystem.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


@Service
public class CourseFacultyServiceImpl implements CourseFacultyService {

  private final CourseRepo courseRepo;
  private final CourseFacultyRepo courseFacultyRepo;

  private final UserRepository userRepository;

  public CourseFacultyServiceImpl(CourseRepo courseRepo, CourseFacultyRepo courseFacultyRepo, UserRepository userRepository) {
    this.courseRepo = courseRepo;
    this.courseFacultyRepo = courseFacultyRepo;
    this.userRepository = userRepository;
  }

  @Override
  public void createCourseFaculty(CourseFaculty courseFaculty)
          throws ConstraintViolationException, CourseFacultyCollectionException {

    Optional<Course> courseOptional = courseRepo.findById(courseFaculty.getId().getCode());
    Optional<User> byUsername = userRepository.findByUsername(courseFaculty.getId().getFacultyUserName());
    CourseFacultyId id = CourseFacultyId.builder().code(courseFaculty.getId().getCode()).facultyUserName(courseFaculty.getId().getFacultyUserName()).build();
    Optional<CourseFaculty> courseFacultyWithCode = courseFacultyRepo.findById(id);
    if (courseOptional.isPresent() && byUsername.isPresent() && isUserFaculty(byUsername.get()) && !courseFacultyWithCode.isPresent()) {
      courseFaculty.setCreated(new Date(System.currentTimeMillis()));
      courseFacultyRepo.save(courseFaculty);
    } else if(courseFacultyWithCode.isPresent()) {
      throw new CourseFacultyCollectionException(
              CourseFacultyCollectionException.CourseFacultyAlreadyExist());
    } else{
      throw new CourseFacultyCollectionException(
              CourseFacultyCollectionException.NotFoundException(courseFaculty.getId().getCode(),courseFaculty.getId().getFacultyUserName()));
    }

}

  private boolean isUserFaculty(User user) {
    return user
            .getRoles()
            .stream()
            .anyMatch(role -> role.getName().name().equals("ROLE_FACULTY"));
}
  @Override
  public List<CourseFaculty> getAllCourseFaculty() {
    List<CourseFaculty> courseFaculty = courseFacultyRepo.findAll();
    return !courseFaculty.isEmpty() ? courseFaculty : Collections.emptyList();
  }

  @Override
  public CourseFaculty getSingleCourseFaculty(String code, String faculty)
      throws CourseFacultyCollectionException {
    return courseFacultyRepo
        .findById(CourseFacultyId.builder().code(code).facultyUserName(faculty).build())
        .orElseThrow(() -> new CourseFacultyCollectionException(
            CourseFacultyCollectionException.NotFoundException(code, faculty)));
  }

  @Override
  public void deleteCourseFaculty(String code, String facultyId)
      throws CourseFacultyCollectionException {
    CourseFacultyId id = CourseFacultyId.builder().code(code).facultyUserName(facultyId).build();
    Optional<CourseFaculty> courseFacultyWithCode = courseFacultyRepo.findById(id);

    if (courseFacultyWithCode.isPresent()) {
      courseFacultyRepo.deleteById(id);
    } else {
      throw new CourseFacultyCollectionException(
          CourseFacultyCollectionException.NotFoundException(code, facultyId));
    }

  }

}
