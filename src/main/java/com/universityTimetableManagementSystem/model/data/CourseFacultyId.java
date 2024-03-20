package com.universityTimetableManagementSystem.model.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseFacultyId {
  @NotNull(message="Course Id cannot be null")
  private String code;

  @NotNull(message="Faculty cannot be null")
  private String facultyId;
}
