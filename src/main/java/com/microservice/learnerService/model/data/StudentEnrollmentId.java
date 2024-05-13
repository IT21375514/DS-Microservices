package com.microservice.learnerService.model.data;

import java.io.Serializable;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentEnrollmentId implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull(message="Course Id cannot be null")
    private String code;

    @NotNull(message="Student cannot be null")
    private String studentUserName;

	

//    @NotNull(message="Student period cannot be null")
//    private String studentPeriod;
}
