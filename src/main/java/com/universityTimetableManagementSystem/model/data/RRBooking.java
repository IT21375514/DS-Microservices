package com.universityTimetableManagementSystem.model.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="rr-booking")
public class RRBooking {

    @Id
    @NotNull(message="Room/Resource BookingId cannot be null")
    private RRBookingId rrBookingId;

    @NotNull(message="Faculty user cannot be null")
    private String faculty;

    private String timeTableReference;

    private LocalDateTime updated;

}
