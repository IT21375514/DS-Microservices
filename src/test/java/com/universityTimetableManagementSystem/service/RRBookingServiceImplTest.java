package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.exception.RRBookingCollectionException;
import com.universityTimetableManagementSystem.exception.TimetableCollectionException;
import com.universityTimetableManagementSystem.model.data.*;
import com.universityTimetableManagementSystem.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class RRBookingServiceImplTest {


    public static final String CODE = "code";
    private RRBookingRepo rrBookingRepoMock;
    private RRBookingServiceImpl onTest;
    private  CourseRepo courseRepoMock;
    private UserRepository userRepoMock;
    private StudentEnrollmentRepo studentEnrollmentRepoMock;
    private static RRBookingId rrBookingId = new RRBookingId();

    @BeforeEach
    void setUp() {
        rrBookingRepoMock = Mockito.mock(RRBookingRepo.class);
        onTest = new RRBookingServiceImpl(courseRepoMock,studentEnrollmentRepoMock,rrBookingRepoMock,userRepoMock);
    }

    @Test
    void testGetRRBooking() throws RRBookingCollectionException {
        when(rrBookingRepoMock.findAll()).thenReturn(List.of(getTestRRBooking()));
        List<RRBooking> allRRBooking = onTest.getAllRRBooking();
        Assertions.assertNotNull(allRRBooking);
        Assertions.assertEquals(1, allRRBooking.size());
    }

    @Test
    void testGetSingleRRBooking() throws RRBookingCollectionException {
        // Prepare test data
        String id = "1";
        Date date = new Date();
        RRBooking expectedBooking = new RRBooking();
        when(rrBookingRepoMock.findByIdDate(id, date)).thenReturn(List.of(expectedBooking));
        List<RRBooking> result = onTest.getSingleRRBooking(id, date);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedBooking, result.get(0));
    }

    @Test
    void testGetSingleRRBooking_NoResult() throws RRBookingCollectionException {
        String id = "1";
        Date date = new Date();
        when(rrBookingRepoMock.findByIdDate(id, date)).thenReturn(Collections.emptyList());
        List<RRBooking> result = onTest.getSingleRRBooking(id, date);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    void testDeleteRRBooking_Success() throws RRBookingCollectionException {
        // Prepare test data
        String id = "1";
        Date date = new Date();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 30);
        String userName = "faculty";

        RRBookingId rrId = RRBookingId.builder()
                .rrId(id)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        RRBooking rrBooking = new RRBooking();
        rrBooking.setFaculty(userName);
        RRBookingRepo rrBookingRepoMock = Mockito.mock(RRBookingRepo.class);
        when(rrBookingRepoMock.findById(rrId)).thenReturn(Optional.of(rrBooking));
        onTest.deleteRRBooking(id, date, startTime, endTime, userName);
        verify(rrBookingRepoMock, times(1)).deleteById(rrId);
    }

    @Test
    void testDeleteRRBooking_Failure_OwnerMismatch() {
        // Prepare test data
        String id = "1";
        Date date = new Date();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 30);
        String userName = "faculty";
        String differentUser = "otherFaculty";

        RRBookingId rrId = RRBookingId.builder()
                .rrId(id)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        RRBooking rrBooking = new RRBooking();
        rrBooking.setFaculty(differentUser);

        // Mock the behavior of RRBookingRepo
        RRBookingRepo rrBookingRepoMock = Mockito.mock(RRBookingRepo.class);
        when(rrBookingRepoMock.findById(rrId)).thenReturn(Optional.of(rrBooking));
        assertThrows(NoSuchElementException.class,
                () -> onTest.deleteRRBooking(id, date, startTime, endTime, userName));
        assertThrows(RRBookingCollectionException.class,
                () -> onTest.deleteRRBooking(id, date, startTime, endTime, userName));

    }

    @Test
    void testDeleteRRBooking_Failure_NotFound() {
        // Prepare test data
        String id = "1";
        Date date = new Date();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 30);
        String userName = "faculty";

        RRBookingId rrId = RRBookingId.builder()
                .rrId(id)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        // Mock the behavior of RRBookingRepo to return empty Optional
        RRBookingRepo rrBookingRepoMock = Mockito.mock(RRBookingRepo.class);
        when(rrBookingRepoMock.findById(rrId)).thenReturn(Optional.empty());
        assertThrows(RRBookingCollectionException.class,
                () -> onTest.deleteRRBooking(id, date, startTime, endTime, userName));
    }
    private static RRBooking getTestRRBooking() {
        rrBookingId.setRrId("1");
        rrBookingId.setDate(new Date());
        rrBookingId.setStartTime(LocalTime.of(9, 0));
        rrBookingId.setEndTime(LocalTime.of(10, 30));
        return getTestRRBooking(rrBookingId);
    }
    private static RRBooking getTestRRBooking(RRBookingId rrBookingId) {
        RRBooking rrBooking = new RRBooking();
        RRBookingId rrBookingId2 = new RRBookingId();
        rrBookingId2.setRrId(rrBookingId.getRrId());
        rrBookingId2.setDate(rrBookingId.getDate());
        rrBookingId2.setStartTime(rrBookingId.getStartTime());
        rrBookingId2.setEndTime(rrBookingId.getEndTime());
        rrBooking.setRrBookingId(rrBookingId2);
        rrBooking.setFaculty("faculty");
        return rrBooking;
    }
}
