package com.example.UniversityNotification.service;

import java.util.List;

import com.example.UniversityNotification.modal.NotificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
	

	@Autowired
	private JavaMailSender javaMailSender;
	

		 public void sendTextEmail(NotificationTemplate notificationTemplate) {
			 String subject=notificationTemplate.getSubject();
			 String body=notificationTemplate.getBody();
			 String courseCode=notificationTemplate.getSubject();
			    if (subject == null || body == null) {
			        throw new IllegalArgumentException("Subject and body cannot be null");
			    }
	
			    try {
			        // Retrieve the student IDs enrolled in the course
//			        List<Enrollment> enrollments = enrollmentRespository.getEnrolledStudentsByCourseCode(courseCode);
//			        System.out.print("Email to be sent :");
//			        
//			        User user= new User();
//			        user.setEmail("JeyapalanSanju@gmail.com");
			    	
			        SimpleMailMessage msg = new SimpleMailMessage();
					System.out.println(notificationTemplate.getSendTo());
                    // Set email details
                    msg.setTo(notificationTemplate.getSendTo());
                    msg.setSubject(subject);
                    msg.setText(body);
                    
                    System.out.print("Email to be sent : JeyapalanSanju@gmail.com");

                    // Send the email
                    javaMailSender.send(msg);
			        			    	
			         
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		
}



