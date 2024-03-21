package com.universityTimetableManagementSystem.service;

import static org.mockito.ArgumentMatchers.any;

import com.universityTimetableManagementSystem.exception.CourseCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;
import com.universityTimetableManagementSystem.repository.CourseRepo;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class CourseServiceImplTest {

  public static final String CODE = "code";
  private  CourseRepo courseRepoMock;
  private CourseServiceImpl onTest;

  @BeforeEach
  void setUp() {
    courseRepoMock = Mockito.mock(CourseRepo.class);
    onTest = new CourseServiceImpl(courseRepoMock);
  }

  @SneakyThrows
  @Test
  void testCreateCourseValid() {
    Course course = getTestCourse();
    onTest.createCourse(course);
    Mockito.verify(courseRepoMock).save(any());
  }

  @SneakyThrows
  @Test
  void testCreateCourseExists() {
    Course course = getTestCourse();
    Mockito.when(courseRepoMock.findById(CODE)).thenReturn(Optional.of(getTestCourse()));
    Assertions.assertThrows(CourseCollectionException.class, () -> onTest.createCourse(course));
  }

  @Test
  void testGetAllCourse() {
    Mockito.when(courseRepoMock.findAll()).thenReturn(List.of(getTestCourse()));
    List<Course> allCourse = onTest.getAllCourse();
    Assertions.assertNotNull(allCourse);
    Assertions.assertEquals(1, allCourse.size());
  }

  @SneakyThrows
  @Test
  void testGetSingleCourseValid() {
    Mockito.when(courseRepoMock.findById(any())).thenReturn(Optional.of(getTestCourse()));
    Course course = onTest.getSingleCourse(CODE);
    Assertions.assertNotNull(course);
    Assertions.assertEquals(CODE, course.getCode());
  }

  @SneakyThrows
  @Test
  void testGetSingleCourseNotExists() {
    Assertions.assertThrows(CourseCollectionException.class, () -> onTest.getSingleCourse(CODE));
  }

  @SneakyThrows
  @Test
  void testUpdateCourseValid() {
    Mockito.when(courseRepoMock.findById(any())).thenReturn(Optional.of(getTestCourse()));
    Course courseToUpdate = new Course();
    String newName = "new_name";
    String newDescription = "new_description";
    int newCredit = 30;
    courseToUpdate.setCourseName(newName);
    courseToUpdate.setDescription(newDescription);
    courseToUpdate.setCredit(newCredit);

    ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);

    onTest.updateCourse(CODE, courseToUpdate);
    Mockito.verify(courseRepoMock).save(captor.capture());

    Course updatedCourse = captor.getValue();
    Assertions.assertEquals(newName, updatedCourse.getCourseName());
    Assertions.assertEquals(newDescription, updatedCourse.getDescription());
    Assertions.assertEquals(newCredit, updatedCourse.getCredit());
  }

  @SneakyThrows
  @Test
  void testUpdateCourseNameConflict() {
    String newName = "new_name";
    String newDescription = "new_description";
    int newCredit = 30;

    Mockito.when(courseRepoMock.findById(any())).thenReturn(Optional.of(getTestCourse()));
    Mockito.when(courseRepoMock.findByCourseName(any())).thenReturn(Optional.of(getTestCourse("OTHER_CODE", newName)));

    Course courseToUpdate = getTestCourse(CODE, newName);
    courseToUpdate.setDescription(newDescription);
    courseToUpdate.setCredit(newCredit);

    Assertions.assertThrows(CourseCollectionException.class, () -> onTest.updateCourse(CODE, courseToUpdate));
  }

  @SneakyThrows
  @Test
  void testUpdateCourseNameNoCourseFound() {
    String newName = "new_name";
    String newDescription = "new_description";
    int newCredit = 30;

    Course courseToUpdate = getTestCourse(CODE, newName);
    courseToUpdate.setDescription(newDescription);
    courseToUpdate.setCredit(newCredit);

    Assertions.assertThrows(CourseCollectionException.class, () -> onTest.updateCourse(CODE, courseToUpdate));
  }


  @SneakyThrows
  @Test
  void testDeleteCourseValid() {
    Mockito.when(courseRepoMock.findById(any())).thenReturn(Optional.of(getTestCourse()));
    onTest.deleteCourse(CODE);
    Mockito.verify(courseRepoMock).deleteById(CODE);
  }

  @SneakyThrows
  @Test
  void testDeleteCourseInvalid() {
    Assertions.assertThrows(CourseCollectionException.class, () -> onTest.deleteCourse(CODE));
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

}