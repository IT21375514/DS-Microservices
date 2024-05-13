package com.microservice.learnerService.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="student-enrollment")
public class StudentEnrollment {

    @Id
    private StudentEnrollmentId id;

    private Date created;
    
    private String courseId;
    private String studentUserName;
    private String weekId;
    private String paymentId;
    private boolean videoViewed;
    private boolean notesRead;
    
    private int progressCount;
    
    private Map<String, Map<String, Boolean>>  courseContent;
    
    public Map<String, Map<String, Boolean>> getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(Map<String, Map<String, Boolean>> courseContent) {
        this.courseContent = courseContent;
    }
    
}
