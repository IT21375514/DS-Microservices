package com.example.coursemanagement.service;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class GoogleService {
    private final String googleDriveBaseUrl = "https://drive.google.com/drive/folders/1ZIO0BCMmhiQWJV5HCeYmeS02zB8DfpPR?usp=drive_link";
    private final String serviceAccountKeyPath = "resources/cred.json";

    public String uploadFileToGoogleDrive(MultipartFile file) {
        try {
            HttpHeaders headers = createHeaders();
            MultiValueMap<String, Object> body = createBody(file);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = sendRequest(requestEntity);

            return handleResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String handleResponse(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            System.out.println("File uploaded successfully to Google Drive: " + responseBody);
            return responseBody;
        } else {
            System.err.println("Error uploading file to Google Drive: " + response.getBody());
            return null; // Or throw an exception
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + serviceAccountKeyPath);
        return headers;
    }

    private MultiValueMap<String, Object> createBody(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getInputStream());
        return body;
    }

    private ResponseEntity<String> sendRequest(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(googleDriveBaseUrl, HttpMethod.POST, requestEntity, String.class);
    }
}
