package com.example.coursemanagement.modelclass;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "course")
public class Course {

	@Id
	private String courseId;
	
//	@NotNull(message= "Name cannot be null")
	private String courseName;
	
//	@NotNull(message= "Code cannot be null")
	private int weekCount;
	
//	@NotNull(message= "Description cannot be null")
	private String description;
	
	private Double courseAmount;
	
//	@NotNull(message= "Credit cannot be null")
//	private List<String> lectureNotesUrls = new ArrayList<>();
//	
//	private List<String> videoUrls = new ArrayList<>();
//	
//	private List<String> quizes = new ArrayList<>();

	private String instructorId;
	
	private String courseEnrollKey;
	
	private String approve;
	

	@CreatedDate
	private Date createdAt;
	
	@LastModifiedDate
	private Date updatedAt;
	
	public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }
    
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
        
    public Double getCourseAmount() {
        return courseAmount;
    }

    public void setCourseAmount(Double courseAmount) {
        this.courseAmount = courseAmount;
    }
    
    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
    
    public String getCourseEnrollKey() {
        return courseEnrollKey;
    }

    public void setCourseEnrollKey(String courseEnrollKey) {
        this.courseEnrollKey = courseEnrollKey;
    }
    
    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
	
}
