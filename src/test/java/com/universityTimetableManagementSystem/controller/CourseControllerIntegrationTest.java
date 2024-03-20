package com.universityTimetableManagementSystem.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.universityTimetableManagementSystem.UniversityTimetableManagementSystemApplication;
import com.universityTimetableManagementSystem.config.MongoDBTestContainerConfig;
import com.universityTimetableManagementSystem.config.TestWebSecurityConfig;
import com.universityTimetableManagementSystem.model.data.Course;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = { MongoDBTestContainerConfig.class, TestWebSecurityConfig.class, UniversityTimetableManagementSystemApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseControllerIntegrationTest {
  public static final String COURSE_CODE = "CODE";
  public static final String COURSE_NAME = "Name";
  public static final String UPDATED_NAME = "DIFFERENT_NAME";
  @LocalServerPort
  private int port;
  RestClient client;

  ObjectMapper objectMapper = new ObjectMapper();

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

}