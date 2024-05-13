package com.example.UniversityNotification.service;

import com.example.UniversityNotification.modal.NotificationTemplate;
import com.example.UniversityNotification.modal.SMSTemplate;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.vonage.client.VonageClient;

@Service
public class SMSService {

  private final VonageClient vonageClient;

  public SMSService(@Value("${vonage.api.key}") String apiKey,
                    @Value("${vonage.api.secret}") String apiSecret) {
    this.vonageClient = VonageClient.builder()
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .build();
  }

  public boolean sendConfirmationSMS(NotificationTemplate notificationTemplate) {
    String from = "Edu Matrix";
    String to = notificationTemplate.getPhoneNo();
    String message = notificationTemplate.getBody();

    System.out.println(to);

    try {
      SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(new TextMessage(from, to, message));
      return response != null && response.getMessages().stream().allMatch(msg -> msg.getStatus() == MessageStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
