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
@Document(collection = "coursecontent")
public class Coursecontent {
	
	@Id
	private String courseContentId;
	
	private String weekId;
	
	private String weekTitle;
	
	private String weekDescription;
	
	private String courseId;

	private String lectureNotesUrls;
	
	private String videoUrls;
	
	//private List<String> quizes = new ArrayList<>();
	
	@CreatedDate
	private Date createdAt;
	
	@LastModifiedDate
	private Date updatedAt;
	
	public String getCourseContentId() {
        return courseContentId;
    }

	public void setCourseContentId(String courseContentId) {
        this.courseContentId = courseContentId;
    }
	
    public void setWeekId(String weekId) {
        this.weekId = weekId;
    }
    
    public String getWeekId() {
        return weekId;
    }
    
    public void setWeekTitle(String weekTitle) {
        this.weekTitle = weekTitle;
    }
    
    public String getWeekTitle() {
        return weekTitle;
    }
    
    public void setWeekDescription(String weekDescription) {
        this.weekDescription = weekDescription;
    }
    
    public String getWeekDescription() {
        return weekDescription;
    }	
	
	public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLectureNotesUrls() {
      return lectureNotesUrls;
    }

    public void setLectureNotesUrls(String lectureNotesUrls) {
        this.lectureNotesUrls = lectureNotesUrls;
    }
    
    public String getVideoUrls() {
        return videoUrls;
      }

      public void setVideoUrls(String videoUrls) {
        this.videoUrls = videoUrls;
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
