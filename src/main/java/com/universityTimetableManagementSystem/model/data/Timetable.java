package com.universityTimetableManagementSystem.model.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="timetable")
public class Timetable {

    @Id
    private String id;

    @NotNull(message="Course cannot be null")
    private String code;

    @NotNull(message="Faculty user cannot be null")
    private String faculty;

    @NotNull(message="Class Location cannot be null")
    private String classRoomResource;

    @NotNull(message="Start date cannot be null")
    private Date courseStartDate;

    @NotNull(message="Course duration cannot be null")
    private int courseDuration;

    @NotNull(message="Start Time cannot be null")
    private LocalTime startTime;

    @NotNull(message="End Time cannot be null")
    private LocalTime endTime;

    @NotNull(message="Batch cannot be null")
    private String batch;

    private LocalDateTime updated;

}
