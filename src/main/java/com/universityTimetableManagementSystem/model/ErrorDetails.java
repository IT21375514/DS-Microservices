package com.universityTimetableManagementSystem.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ErrorDetails {
  private HttpStatus status;
  private String message;
  private List<String> errors;
}
