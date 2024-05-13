package com.example.UniversityNotification.controller;

import com.example.UniversityNotification.modal.NotificationTemplate;
import com.example.UniversityNotification.modal.SMSTemplate;
import com.example.UniversityNotification.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.UniversityNotification.service.EmailService;
import com.example.UniversityNotification.service.SMSService;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private final RestTemplate restTemplate;

    public NotificationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

        @PostMapping("/email")
        public ResponseEntity<String> sendConfirmationEmail(HttpServletRequest request, HttpServletResponse response, @RequestBody NotificationTemplate notificationTemplate) throws IOException {
            // Extract JWT token from request header


            String jwtToken = request.getHeader("Authorization");
            System.out.println(jwtToken);

    //        if (jwtToken == null || !jwtUtils.validateJwtToken(jwtToken)) {
    //            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
    //            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid or missing JWT token");
    //        }

    // Retrieve username from the JWT token
            String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            System.out.println("Username: " + username);

    // Retrieve roles from the JWT token
            List<String> roles = jwtUtils.getUserRolesFromJwtToken(jwtToken);
            System.out.println("Roles: " + roles);


            // Retrieve email address from the other microservice
            String userId = username;
            String emailByUsernameUrl = "http://localhost:8080/tms/auth/emailByUsername?username=" + userId;
            String output = restTemplate.getForObject(emailByUsernameUrl, String.class);

            System.out.println(output);

            String emailAddress = output.split("#")[0];
            String phoneNo = output.split("#")[1];
            System.out.println("@@@@@@@@@@@@@@@" + emailAddress + " "+ phoneNo);
            System.out.println("AAAAAAAAAAAAAAA"+phoneNo);
            notificationTemplate.setSendTo(emailAddress);
            notificationTemplate.setPhoneNo(phoneNo);


            System.out.println("Email address :" + emailAddress);

            emailService.sendTextEmail(notificationTemplate);
//            smsService.sendConfirmationSMS(notificationTemplate);
            return ResponseEntity.ok("Email sent successfully");
        }



//    @PostMapping("/email")
//    public ResponseEntity<String> sendConfirmationEmail(@RequestBody EmailTemplate emailTemplate) {
//        // Retrieve email addresses from the other microservice
//        List<String> usernames = emailTemplate.getUsernames();
//        String emailByUIDsUrl = "http://localhost:8080/tms/auth/emailsByUIDs";
//        List<String> emailAddresses = restTemplate.postForObject(emailByUIDsUrl, usernames, List.class);
//
//        // Set email addresses as recipients in the email template
//        emailTemplate.setSendTo(emailAddresses);
//
//        // Send email to each recipient
//        for (String emailAddress : emailAddresses) {
//            // Set recipient's email address in the template
//            emailTemplate.setSendTo(emailAddress);
//
//            // Send email
//            emailService.sendTextEmail(emailTemplate);
//        }
//
//        return ResponseEntity.ok("Email sent successfully");
//    }


//    @PostMapping("/sms")
//    public ResponseEntity<String> sendConfirmationSMS(@RequestBody SMSTemplate smsTemplate) {
//        // Logic to send confirmation SMS
//        smsService.sendConfirmationSMS(smsTemplate);
//        return ResponseEntity.ok("SMS sent successfully");
//    }




}
