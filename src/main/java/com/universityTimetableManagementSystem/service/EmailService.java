package com.universityTimetableManagementSystem.service;

import com.universityTimetableManagementSystem.model.data.EmailData;
import com.universityTimetableManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;


    public void sendTextEmail(EmailData email) {
        if (email.getSendTo() == null || email.getBody() == null || email.getSubject() == null) {
            throw new IllegalArgumentException("Subject and body cannot be null");
        }

        try {
                            SimpleMailMessage msg = new SimpleMailMessage();

                            // Set email details
                            msg.setTo(email.getSendTo());
                            msg.setSubject(email.getSubject());
                            msg.setText(email.getBody());

                            // Send the email
                            javaMailSender.send(msg);
            System.out.println("Email sent successfully to: " + email.getSendTo());
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + email.getSendTo());
            e.printStackTrace();
        }
    }



}
