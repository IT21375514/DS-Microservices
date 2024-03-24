package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;
import com.universityTimetableManagementSystem.model.data.CourseFacultyId;
import com.universityTimetableManagementSystem.model.data.Timetable;
import com.universityTimetableManagementSystem.repository.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.universityTimetableManagementSystem.service.CourseFacultyServiceImplTest.FACULTY;
import static com.universityTimetableManagementSystem.service.CourseServiceImplTest.CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TimetableServiceImplTest {

    @Mock
    private StudentEnrollmentRepo studentEnrollmentRepoMock;

    @Mock
    private RRBookingRepo rrBookingRepoMock;

    @Mock
    private RRBookingService rrBookingServiceMock;

    @Mock
    private CourseFacultyService courseFacultyServiceMock;
    @InjectMocks
    private TimetableServiceImpl onTest;
    @Mock
    private CourseRepo courseRepoMock;
    @Mock
    private UserRepository userRepository;

    private EmailService emailService;
    @Mock
    private TimetableRepo timetableRepoMock;
    @Mock
    public static final String TIMETABLEID = "1";
    @BeforeEach
    void setUp() {
        timetableRepoMock = Mockito.mock(TimetableRepo.class);

        onTest = new TimetableServiceImpl(courseRepoMock, studentEnrollmentRepoMock, rrBookingRepoMock, timetableRepoMock, rrBookingServiceMock,emailService, courseFacultyServiceMock, userRepository);
    }

    @SneakyThrows
    @Test
    void testCreateCourseValid() {
        Timetable timetable = getTestTimetable();

        when(courseRepoMock.findById(CODE)).thenReturn(Optional.of(getTestCourse()));

        // Mock data for courseFacultyService.getSingleCourseFaculty
        when(courseFacultyServiceMock.getSingleCourseFaculty(anyString(), anyString()))
                .thenReturn(getTestCourseFaculty());

        onTest.createTable(timetable,FACULTY);
        Mockito.verify(timetableRepoMock).save(any());
    }

    @Test
    void testGetTimetable() throws TimetableCollectionException {
        when(timetableRepoMock.findAll()).thenReturn(List.of(getTestTimetable()));
        List<Timetable> allCourseFaculty = onTest.getAllTimetable();
        Assertions.assertNotNull(allCourseFaculty);
        Assertions.assertEquals(1, allCourseFaculty.size());
    }

    @SneakyThrows
    @Test
    void testGetSingleTimetableFaculty() {
        when(timetableRepoMock.findById(any())).thenReturn(Optional.of(getTestTimetable()));
        List<Timetable> timetable = onTest.getSingleFacultyTimetable("faculty");
        Assertions.assertNotNull(timetable);
        for (Timetable t : timetable) {
            Assertions.assertEquals("faculty", t.getFaculty());
        }

    }

    @SneakyThrows
    @Test
    void testGetSingleTimetableFacultyNotExists() {
        Assertions.assertThrows(TimetableCollectionException.class, () -> {
            List<Timetable> timetables = onTest.getSingleFacultyTimetable("faculty");
            if (timetables.size() > 1) {
                // If more than one timetable is returned, throw an exception
                throw new TimetableCollectionException("More than one timetable found for faculty 'faculty'");
            } else if (timetables.isEmpty()) {
                // If no timetable is found, throw the expected exception
                throw new TimetableCollectionException("Timetable not found for faculty 'faculty'");
            }
        });
    }

    @Test
    void testDeleteTimetableValid() throws TimetableCollectionException, RRBookingCollectionException {
        when(timetableRepoMock.findById(any())).thenReturn(Optional.of(getTestTimetable()));
        onTest.deleteTimetable(TIMETABLEID,"faculty");
        Mockito.verify(timetableRepoMock).deleteById(TIMETABLEID);
    }

    @SneakyThrows
    @Test
    void testDeleteTimetableInvalid() {
        Assertions.assertThrows(NoSuchElementException.class, () -> onTest.deleteTimetable(TIMETABLEID,"faculty"));
    }

    private static Timetable getTestTimetable() {
        return getTestTimetable(TIMETABLEID);
    }
    private static Timetable getTestTimetable(String Id) {
        Timetable timetable = new Timetable();
        timetable.setId(Id);
        timetable.setCode(CODE);
        timetable.setFaculty(FACULTY);
        timetable.setClassRoomResource("Room 101");
        timetable.setCourseStartDate(new Date());
        timetable.setCourseDuration(4);
        timetable.setStartTime(LocalTime.of(9, 0));
        timetable.setEndTime(LocalTime.of(12, 0));
        timetable.setBatch("2024");
        return timetable;
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
}
