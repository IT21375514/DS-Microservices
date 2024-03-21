package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.StudentEnrollmentCollectionException;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.repository.CourseFacultyRepo;
import com.universityTimetableManagementSystem.repository.CourseRepo;
import com.universityTimetableManagementSystem.repository.StudentEnrollmentRepo;
import com.universityTimetableManagementSystem.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {

    private final CourseRepo courseRepo;
    private final StudentEnrollmentRepo studentEnrollmentRepo;

    private final UserRepository userRepository;

    public StudentEnrollmentServiceImpl(CourseRepo courseRepo, StudentEnrollmentRepo studentEnrollmentRepo, UserRepository userRepository) {
        this.courseRepo = courseRepo;
        this.studentEnrollmentRepo = studentEnrollmentRepo;
        this.userRepository = userRepository;
    }

    @Override
    public void createStudentEnrollment(StudentEnrollment studentEnrollment) throws ConstraintViolationException, StudentEnrollmentCollectionException {
        Optional<Course> courseOptional = courseRepo.findById(studentEnrollment.getId().getCode());
        Optional<User> byUsername = userRepository.findByUsername(studentEnrollment.getId().getStudentUserName());
        StudentEnrollmentId id = StudentEnrollmentId.builder().code(studentEnrollment.getId().getCode()).studentUserName(studentEnrollment.getId().getStudentUserName()).build();
        Optional<StudentEnrollment> studentEnrollmentWithName = studentEnrollmentRepo.findById(id);
        if (courseOptional.isPresent() && byUsername.isPresent() && isUserStudent(byUsername.get()) && !studentEnrollmentWithName.isPresent()) {
            studentEnrollment.setCreated(new Date(System.currentTimeMillis()));
            studentEnrollmentRepo.save(studentEnrollment);
        } else if(studentEnrollmentWithName.isPresent()) {
            throw new StudentEnrollmentCollectionException(
                    StudentEnrollmentCollectionException.StudentAlreadyExist());
        } else{
            throw new StudentEnrollmentCollectionException(
                    StudentEnrollmentCollectionException.NotFoundException(studentEnrollment.getId().getCode(),studentEnrollment.getId().getStudentUserName()));
        }

    }
    private boolean isUserStudent(User user) {
        return user
                .getRoles()
                .stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_STUDENT"));
    }

    @Override
    public List<StudentEnrollment> getAllStudentEnrollment() {
        List<StudentEnrollment> studentEnrollment = studentEnrollmentRepo.findAll();
        return !studentEnrollment.isEmpty() ? studentEnrollment : Collections.emptyList();
    }

    @Override
    public StudentEnrollment getSingleStudentEnrollment(String code, String student) throws StudentEnrollmentCollectionException {
        return studentEnrollmentRepo
                .findById(StudentEnrollmentId.builder().code(code).studentUserName(student).build())
                .orElseThrow(() -> new StudentEnrollmentCollectionException(
                        StudentEnrollmentCollectionException.NotFoundException(code, student)));
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
}
