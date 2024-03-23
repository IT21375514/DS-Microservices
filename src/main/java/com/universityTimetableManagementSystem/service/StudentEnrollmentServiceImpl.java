package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.StudentEnrollmentCollectionException;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.repository.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {

    private final CourseRepo courseRepo;
    private final StudentEnrollmentRepo studentEnrollmentRepo;
    private final UserRepository userRepository;
    private final TimetableRepo timetableRepo;
    public StudentEnrollmentServiceImpl(CourseRepo courseRepo, StudentEnrollmentRepo studentEnrollmentRepo, UserRepository userRepository, TimetableRepo timetableRepo) {
        this.courseRepo = courseRepo;
        this.studentEnrollmentRepo = studentEnrollmentRepo;
        this.userRepository = userRepository;
        this.timetableRepo = timetableRepo;
    }

    @Override
    public void createStudentEnrollment(StudentEnrollment studentEnrollment) throws ConstraintViolationException, StudentEnrollmentCollectionException {
        Optional<Course> courseOptional = courseRepo.findById(studentEnrollment.getId().getCode());
        Optional<User> byUsername = userRepository.findByUsername(studentEnrollment.getId().getStudentUserName());
        StudentEnrollmentId id = StudentEnrollmentId.builder().code(studentEnrollment.getId().getCode()).studentUserName(studentEnrollment.getId().getStudentUserName()).studentPeriod(studentEnrollment.getId().getStudentPeriod()).build();
        Optional<StudentEnrollment> studentEnrollmentWithName = studentEnrollmentRepo.findById(id);
        String currentCourse = studentEnrollment.getId().getCode();
        String currentStudent = studentEnrollment.getId().getStudentUserName();
        String currentPeriod = studentEnrollment.getId().getStudentPeriod();
        if(currentCourse!=null && currentStudent!=null && currentPeriod!=null) {
            if (courseOptional.isPresent() && byUsername.isPresent() && isUserStudent(byUsername.get()) && !studentEnrollmentWithName.isPresent()) {
                studentEnrollment.setCreated(new Date(System.currentTimeMillis()));
                studentEnrollmentRepo.save(studentEnrollment);
            } else if (studentEnrollmentWithName.isPresent()) {
                throw new StudentEnrollmentCollectionException(
                        StudentEnrollmentCollectionException.StudentAlreadyExist());
            } else {
                throw new StudentEnrollmentCollectionException(
                        StudentEnrollmentCollectionException.NotFoundException(currentCourse, currentStudent, currentPeriod));
            }
        }else {
            throw new StudentEnrollmentCollectionException(
                    StudentEnrollmentCollectionException.MissingData());
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
    public List<Timetable> getCurrentStudentAllEnrollment(String student) {

        List<Timetable> timetables = new ArrayList<>();

        List <StudentEnrollment> studentEnrollments = studentEnrollmentRepo.findByCurrentUsereId(student);

        for (StudentEnrollment studentEnrollment : studentEnrollments) {
            String code = studentEnrollment.getId().getCode();
            String studentPeriod = studentEnrollment.getId().getStudentPeriod();

            List<Timetable> studentTimetables = timetableRepo.findByCourseBatch(code, studentPeriod);
            timetables.addAll(studentTimetables);
        }
        return !timetables.isEmpty() ? timetables : Collections.emptyList();
    }

    @Override
    public StudentEnrollment getSingleStudentEnrollment(String code, String student, String studentPeriod) throws StudentEnrollmentCollectionException {
        return studentEnrollmentRepo
                .findById(StudentEnrollmentId.builder().code(code).studentUserName(student).studentPeriod(studentPeriod).build())
                .orElseThrow(() -> new StudentEnrollmentCollectionException(
                        StudentEnrollmentCollectionException.NotFoundException(code, student, studentPeriod)));
    }

    @Override
    public void deleteStudentEnrollment(String code, String student, String studentPeriod) throws StudentEnrollmentCollectionException {
        StudentEnrollmentId id = StudentEnrollmentId.builder().code(code).studentUserName(student).studentPeriod(studentPeriod).build();
        Optional<StudentEnrollment> studentEnrollmentWithName = studentEnrollmentRepo.findById(id);

        if (studentEnrollmentWithName.isPresent()) {
            studentEnrollmentRepo.deleteById(id);
        } else {
            throw new StudentEnrollmentCollectionException(
                    StudentEnrollmentCollectionException.NotFoundException(code, student,studentPeriod));
        }
    }


}
