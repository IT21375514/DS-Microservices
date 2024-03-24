package com.universityTimetableManagementSystem.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universityTimetableManagementSystem.UniversityTimetableManagementSystemApplication;
import com.universityTimetableManagementSystem.config.MongoDBTestContainerConfig;
import com.universityTimetableManagementSystem.config.TestWebSecurityConfig;
import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.ERole;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.repository.RRBookingRepo;
import com.universityTimetableManagementSystem.repository.TimetableRepo;
import com.universityTimetableManagementSystem.repository.UserRepository;
import com.universityTimetableManagementSystem.service.CourseFacultyService;
import com.universityTimetableManagementSystem.service.EmailService;
import com.universityTimetableManagementSystem.service.RRBookingService;
import com.universityTimetableManagementSystem.service.TimetableServiceImpl;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.universityTimetableManagementSystem.service.CourseFacultyServiceImplTest.CODE;
import static com.universityTimetableManagementSystem.service.CourseFacultyServiceImplTest.getTestRole;
import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = { MongoDBTestContainerConfig.class, TestWebSecurityConfig.class, UniversityTimetableManagementSystemApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {
  public static final String COURSE_CODE = "CODE";
  public static final String COURSE_NAME = "Name";
  public static final String UPDATED_NAME = "DIFFERENT_NAME";
  public static final String USER_NAME = "Test User";

    User facultyUser = getTestUser(USER_NAME, "rgqwrkjkhjhsfaduihulyuweuqwl","asrgewyjrkdsfasdfasjau");

    @LocalServerPort
  private int port;
  RestClient client;
    @Autowired
    private UserRepository userRepository;
  ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RRBookingRepo rrBookingRepo;

    @Autowired
    private TimetableRepo timetableRepo;

    @Autowired
    private RRBookingService rrBookingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CourseFacultyService courseFacultyService;

    @Autowired
    private TimetableServiceImpl timetableService;
  @BeforeEach
  void clear() {
    client = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @SneakyThrows
  @Test
  @Order(1)
  void testCreateCourse() {
    client.post()
        .uri("/tms/courses")
        .contentType(MediaType.APPLICATION_JSON)
        .body(getTestCourse(COURSE_CODE, COURSE_NAME))
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
          return clientResponse;
        });

  }

  @Test
  @Order(2)
  void testGetAllCourses() {
    List<Course> course = client.get()
        .uri("/tms/courses")
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
          return objectMapper.readValue(clientResponse.getBody(), new TypeReference<>() {});
        });
    Assertions.assertEquals(1, course.size());
    Assertions.assertEquals(COURSE_CODE, course.getFirst().getCode());
    Assertions.assertEquals(COURSE_NAME, course.getFirst().getCourseName());

  }

  @Test
  @Order(3)
  void testGetSingleCourse() {
    Course course = client.get()
        .uri("/tms/courses/"+COURSE_CODE)
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
          return objectMapper.readValue(clientResponse.getBody(), Course.class);
        });
    Assertions.assertNotNull(course);
    Assertions.assertEquals(COURSE_CODE, course.getCode());
    Assertions.assertEquals(COURSE_NAME, course.getCourseName());
  }

  @Test
  @Order(4)
  void updateByCode() {
    client.put()
        .uri("/tms/courses/"+COURSE_CODE)
        .contentType(MediaType.APPLICATION_JSON)
        .body(getTestCourse(COURSE_CODE, UPDATED_NAME))
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
          return clientResponse;
        });
  }

  @Test
  @Order(5)
  void testGetSingleCourseAfterUpdate() {
    Course course = client.get()
        .uri("/tms/courses/"+COURSE_CODE)
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
          return objectMapper.readValue(clientResponse.getBody(), Course.class);
        });
    Assertions.assertNotNull(course);
    Assertions.assertEquals(COURSE_CODE, course.getCode());
    Assertions.assertEquals(UPDATED_NAME, course.getCourseName());
  }

  @Test
  @Order(6)
  void testDeleteCourse() {
    client.delete()
        .uri("/tms/courses/"+COURSE_CODE)
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
          return clientResponse;
        });
  }

  @Test
  @Order(7)
  void testGetSingleCourseAfterDelete() {
   client.get()
        .uri("/tms/courses/"+COURSE_CODE)
        .exchange((clientRequest, clientResponse) -> {
          Assertions.assertNotNull(clientResponse);
          Assertions.assertEquals(HttpStatusCode.valueOf(404), clientResponse.getStatusCode());
          return clientResponse;
        });
  }

  private static Course getTestCourse(String code, String name) {
    Course course = new Course();
    course.setCode(code);
    course.setCourseName(name);
    course.setDescription("description");
    course.setCredit(3);
    return course;
  }

    @SneakyThrows
    @Test
    @Order(8)
    void testCreateCourse2() {
        client.post()
                .uri("/tms/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .body(getTestCourse(COURSE_CODE, COURSE_NAME))
                .exchange((clientRequest, clientResponse) -> {
                    Assertions.assertNotNull(clientResponse);
                    Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
                    return clientResponse;
                });

    }


    @Test
    @Order(9)
    void testCreateCourseFaculty() {

        CourseFaculty courseFaculty = new CourseFaculty();

        courseFaculty.setId(new CourseFacultyId(CODE, USER_NAME));


        client.post()
                .uri("/tms/course-faculty")
                .contentType(MediaType.APPLICATION_JSON)
                .body(getTestCourseFaculty(CODE,USER_NAME))
                .exchange((clientRequest, clientResponse) -> {
                    Assertions.assertNotNull(clientResponse);
                    Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
                    return clientResponse;
                });
    }

    @Test
    @Order(10)
    void testGetAllCourseFaculty() {
        List<CourseFaculty> courseFacultyList = client.get()
                .uri("/tms/course-faculty")
                .exchange((clientRequest, clientResponse) -> {
                    Assertions.assertNotNull(clientResponse);
                    Assertions.assertEquals(HttpStatusCode.valueOf(200), clientResponse.getStatusCode());
                    return objectMapper.readValue(clientResponse.getBody(), new TypeReference<>() {});
                });
        // Assertions for the returned course faculty list
        Assertions.assertNotNull(courseFacultyList);
        // Add more assertions as needed
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
        roles.add(getTestRole(ERole.ROLE_ADMIN));
        roles.add(getTestRole(ERole.ROLE_FACULTY));
        roles.add(getTestRole(ERole.ROLE_STUDENT));
        return roles;
    }
    private static CourseFaculty getTestCourseFaculty() {
        return getTestCourseFaculty(CODE, USER_NAME);
    }

    private static CourseFaculty getTestCourseFaculty(String code, String name) {
        CourseFaculty courseFaculty = new CourseFaculty();
        CourseFacultyId courseFacultyId = new CourseFacultyId();
        courseFacultyId.setCode(code);
        courseFacultyId.setFacultyUserName(name);
        courseFaculty.setId(courseFacultyId);
        return courseFaculty;
    }

    @Test
    void testCreateTable() throws ConstraintViolationException, TimetableCollectionException, RRBookingCollectionException, CourseFacultyCollectionException, CourseCollectionException {
        // Create a sample timetable
        Timetable timetable = new Timetable();
        timetable.setCode(COURSE_CODE);
        timetable.setClassRoomResource("A101");
        timetable.setCourseStartDate(new Date());
        timetable.setCourseDuration(10);
        timetable.setStartTime(LocalTime.of(9, 0));
        timetable.setEndTime(LocalTime.of(11, 0));
        timetable.setBatch("2024");
        timetable.setFaculty(USER_NAME);
        timetable.setUpdated(LocalDateTime.now());

        // Save the sample timetable
        timetableRepo.save(timetable);

        // Call the createTable method of TimetableServiceImpl
        timetableService.createTable(timetable, "facultyUser");

        // Retrieve the saved timetable from the database
        Optional<Timetable> savedTimetableOptional = timetableRepo.findById(timetable.getId());

        // Verify that the timetable was saved and created successfully
        assertNotNull(savedTimetableOptional);
        assertTrue(savedTimetableOptional.isPresent());
        Timetable savedTimetable = savedTimetableOptional.get();
        assertEquals("facultyUser", savedTimetable.getFaculty());
        assertEquals("CSE101", savedTimetable.getCode());
        // Add more assertions as needed
    }
}