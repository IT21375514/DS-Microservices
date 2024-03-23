package com.universityTimetableManagementSystem.model.data;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RRBookingId {
    @NotNull(message="Room/Resouce Id cannot be null")
    private String rrId;

    @NotNull(message="Date cannot be null")
    private Date date;

    @NotNull(message="Time cannot be null")
    private LocalTime startTime;

    @NotNull(message="Time cannot be null")
    private LocalTime endTime;
}

