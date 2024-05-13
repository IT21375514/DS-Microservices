package com.example.UniversityNotification.modal;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class NotificationTemplate {

	@NonNull
	private String username;

	private List<String> usernames;

	@Email
	private String sendTo;

	private String phoneNo;

	@Max(value = 100)
	private String subject;
	@Max(value = 500)
	private String body;

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}

