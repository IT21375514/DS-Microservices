package com.example.coursemanagement.modelclass;

import lombok.Data;

@Data
public class Res {
    private int status;
    private String message;
    private String fileUrl;
    private String videoUrl;
}