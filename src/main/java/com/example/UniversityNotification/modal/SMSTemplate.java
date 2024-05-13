package com.example.UniversityNotification.modal;


import jakarta.validation.constraints.Max;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class SMSTemplate {

        @NonNull
        private String username;

        private String sendTo;
        @Max(value = 100)
        private String subject;
        @Max(value = 500)
        private String body;


    }
