package com.universityTimetableManagementSystem.model.data;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="courses")
public class Course {
	
	@Id
	@NotNull(message="Course Code cannot be null")
	private String code;
	
	@NotNull(message="Course Name cannot be null")
	private String courseName;
	
	@NotNull(message="Course Description cannot be null")
	private String description;
	
	@Min(value = 1, message = "Course Credit must be greater than 0")
    @Max(value = 4, message = "Course Credit must be less than or equal to 4")
	private int credit;
	
	private Date created;
	private Date updated;

}
