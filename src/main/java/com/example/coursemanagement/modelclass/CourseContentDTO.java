package com.example.coursemanagement.modelclass;

public class CourseContentDTO {
	private String courseContentId;
	private String videoUrls;
    private String lectureNotesUrls;
    private String weekTitle;
	
	private String weekDescription;
	

	public String getCourseContentId() {
        return courseContentId;
    }

	public void setCourseContentId(String courseContentId) {
        this.courseContentId = courseContentId;
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
}
