package com.blessedbits.SchoolHub.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private String email = "schoolhub.educ.platform@gmail.com";
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String buildConfirmEmail(String username, String token) {
        return "Hello " + username + ",\n\nPlease confirm your email by clicking the link below:\n" +
               "https://ypgblessedbits.azurewebsites.net/auth/confirm?token=" + token;
    }

    public String buildResetPasswordEmail(String username, String token) {
        return "Hello " + username + ",\n\nWe received a request to reset your password. Please click the link below to reset it:\n" +
               "https://ypgblessedbits.azurewebsites.net/auth/reset-password?token=" + token;
    }

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(email);

        mailSender.send(message);
    }
}
