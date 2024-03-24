package com.universityTimetableManagementSystem.service;

import static com.universityTimetableManagementSystem.model.ERole.ROLE_ADMIN;
import static com.universityTimetableManagementSystem.service.CourseServiceImplTest.CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.universityTimetableManagementSystem.controller.AuthController;
import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.model.ERole;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.model.security.SignupRequest;
import com.universityTimetableManagementSystem.repository.CourseFacultyRepo;
import com.universityTimetableManagementSystem.repository.CourseRepo;

import java.util.*;

import com.universityTimetableManagementSystem.repository.RoleRepository;
import com.universityTimetableManagementSystem.repository.UserRepository;
import com.universityTimetableManagementSystem.security.JwtUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

class CourseFacultyServiceImplTest {

    private CourseRepo courseRepo;
    public static final String CODE = "code";
    public static final String FACULTY = "faculty";
    private CourseFacultyRepo courseFacultyRepo;
    private UserRepository userRepository;

    private CourseFacultyServiceImpl onTest;
    private CourseServiceImpl onTest1;
    private UserDetailsImpl onTest2;

    private SignupRequest signupRequest;

    private UserDetailsServiceImpl onTest3;


    private AuthenticationManager authenticationManager;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        courseFacultyRepo = Mockito.mock(CourseFacultyRepo.class);
        userRepository = Mockito.mock(UserRepository.class);
        courseRepo = Mockito.mock(CourseRepo.class);
        onTest1 = new CourseServiceImpl(courseRepo);
        User facultyUser = getTestUser(FACULTY, "facultyTestUser@gmail.com", "rgqwrkjkhjhsfaduihulyuweuqwl");
        Set<Role> roles = new HashSet<>();
        roles.add(getTestRole(ERole.ROLE_FACULTY));
        facultyUser.setRoles(roles);

        // Define the behavior of userRepository mock
        when(userRepository.findByUsername(eq(FACULTY))).thenReturn(Optional.of(facultyUser));

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_FACULTY"));
        onTest2 = new UserDetailsImpl("65fd97ea50429b0b459b08b213", FACULTY, "facultyTestUser@gmail.com", "rgqwrkjkhjhsfaduihulyuweuqwl", updatedAuthorities);
        onTest3 = new UserDetailsServiceImpl(userRepository);
        onTest = new CourseFacultyServiceImpl(courseRepo, courseFacultyRepo, userRepository);
    }

    @SneakyThrows
    @Test
    void testCreateCourseValid() {
        Course course = getTestCourse();
        User user = getTestUser();
        CourseFaculty courseFaculty = getTestCourseFaculty();
        onTest1.createCourse(course);
        Mockito.verify(courseRepo).save(any());
        onTest.createCourseFaculty(courseFaculty);
        Mockito.verify(courseFacultyRepo).save(any());
    }

    @Test
    void testGetAllCourseFaculty() {
        Mockito.when(courseFacultyRepo.findAll()).thenReturn(List.of(getTestCourseFaculty()));
        List<CourseFaculty> allCourseFaculty = onTest.getAllCourseFaculty();
        Assertions.assertNotNull(allCourseFaculty);
        Assertions.assertEquals(1, allCourseFaculty.size());
    }

    @SneakyThrows
    @Test
    void testGetSingleCourseFacultyValid() {
        Mockito.when(courseFacultyRepo.findById(any())).thenReturn(Optional.of(getTestCourseFaculty()));
        CourseFaculty courseFaculty = onTest.getSingleCourseFaculty(CODE,FACULTY);
        Assertions.assertNotNull(courseFaculty);
        Assertions.assertEquals(CODE, courseFaculty.getId().getCode());
        Assertions.assertEquals(FACULTY, courseFaculty.getId().getFacultyUserName());
    }

    @SneakyThrows
    @Test
    void testGetSingleCourseFacultyNotExists() {
        Assertions.assertThrows(CourseFacultyCollectionException.class, () -> onTest.getSingleCourseFaculty(CODE,"faculty2"));
    }

    @SneakyThrows
    @Test
    void testDeleteCourseFacultyValid() {
        Mockito.when(courseFacultyRepo.findById(any())).thenReturn(Optional.of(getTestCourseFaculty()));
        onTest.deleteCourseFaculty(CODE,FACULTY);
        CourseFacultyId courseId = new CourseFacultyId(CODE,FACULTY);
        Mockito.verify(courseFacultyRepo).deleteById(courseId);
    }

    @SneakyThrows
    @Test
    void testDeleteCourseFacultInvalid() {
        Assertions.assertThrows(CourseFacultyCollectionException.class, () -> onTest.deleteCourseFaculty(CODE,"faculty2"));
    }

    private static CourseFaculty getTestCourseFaculty() {
        return getTestCourseFaculty(CODE, FACULTY);
    }

    private static CourseFaculty getTestCourseFaculty(String code, String name) {
        CourseFaculty courseFaculty = new CourseFaculty();
        CourseFacultyId courseFacultyId = new CourseFacultyId();
        courseFacultyId.setCode(code);
        courseFacultyId.setFacultyUserName(name);
        courseFaculty.setId(courseFacultyId);
        return courseFaculty;
    }

    private static Course getTestCourse() {
        return getTestCourse(CODE, "name");
    }

    private static Course getTestCourse(String code, String name) {
        Course course = new Course();
        course.setCode(code);
        course.setCourseName(name);
        course.setDescription("description");
        course.setCredit(20);
        return course;
    }

    public static User getTestUser() {
        return getTestUser(FACULTY, "facultyTestUser@gmail.com", "rgqwrkjkhjhsfaduihulyuweuqwl");
    }

    public static User getTestUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(getTestRoles()); // Set test roles
        return user;
    }

    private static Set<Role> getTestRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(getTestRole(ERole.ROLE_FACULTY));
        return roles;
    }

    private static Role getTestRole(ERole role) {
        Role testRole = new Role();
        testRole.setName(role);
        return testRole;
    }

}