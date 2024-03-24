package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.CourseFacultyCollectionException;
import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.data.Course;
import com.universityTimetableManagementSystem.model.data.CourseFaculty;
import com.universityTimetableManagementSystem.model.data.CourseFacultyId;
import com.universityTimetableManagementSystem.model.data.Timetable;
import com.universityTimetableManagementSystem.repository.CourseRepo;
import com.universityTimetableManagementSystem.repository.RRBookingRepo;
import com.universityTimetableManagementSystem.repository.StudentEnrollmentRepo;
import com.universityTimetableManagementSystem.repository.TimetableRepo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class TimetableServiceImplTest {

    private StudentEnrollmentRepo studentEnrollmentRepoMock;

    private RRBookingRepo rrBookingRepoMock;

    private RRBookingService rrBookingServiceMock;

    private CourseFacultyService courseFacultyServiceMock;
    private TimetableServiceImpl onTest;
    private CourseRepo courseRepoMock;
    private TimetableRepo timetableRepoMock;
    public static final String TIMETABLEID = "1";
    @BeforeEach
    void setUp() {
        timetableRepoMock = Mockito.mock(TimetableRepo.class);
        onTest = new TimetableServiceImpl(courseRepoMock, studentEnrollmentRepoMock, rrBookingRepoMock, timetableRepoMock, rrBookingServiceMock, courseFacultyServiceMock);
    }

    @Test
    void testGetTimetable() throws TimetableCollectionException {
        Mockito.when(timetableRepoMock.findAll()).thenReturn(List.of(getTestTimetable()));
        List<Timetable> allCourseFaculty = onTest.getAllTimetable();
        Assertions.assertNotNull(allCourseFaculty);
        Assertions.assertEquals(1, allCourseFaculty.size());
    }

    @SneakyThrows
    @Test
    void testGetSingleTimetableFaculty() {
        Mockito.when(timetableRepoMock.findById(any())).thenReturn(Optional.of(getTestTimetable()));
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
        Mockito.when(timetableRepoMock.findById(any())).thenReturn(Optional.of(getTestTimetable()));
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
        timetable.setCode("CSE101");
        timetable.setFaculty("faculty");
        timetable.setClassRoomResource("Room 101");
        timetable.setCourseStartDate(new Date());
        timetable.setCourseDuration(4);
        timetable.setStartTime(LocalTime.of(9, 0));
        timetable.setEndTime(LocalTime.of(12, 0));
        timetable.setBatch("2024");
        return timetable;
    }
}
