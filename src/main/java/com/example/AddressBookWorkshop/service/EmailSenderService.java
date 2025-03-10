package com.example.AddressBookWorkshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sparsh262002@gmail.com");  // Ensure this email is authorized in SMTP settings
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (MailException e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "🔒 Password Reset Request";
        String body = "Hello,\n\n" +
                "We received a request to reset your password. Click the link below to reset it:\n\n" +
                "➡ http://localhost:8080/api/auth/reset-password?token=" + resetToken + "\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Regards,\nYour Team";

        sendEmail(toEmail, subject, body);
    }
}
