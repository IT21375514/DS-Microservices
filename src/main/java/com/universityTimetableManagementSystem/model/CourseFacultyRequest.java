package com.universityTimetableManagementSystem.model;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Getter;

@Getter
public class CourseFacultyRequest {
  @NotNull(message="Course Id cannot be null")
  private String code;

  @NotNull(message="Faculty cannot be null")
  private String facultyId;

  private Date created;
}
