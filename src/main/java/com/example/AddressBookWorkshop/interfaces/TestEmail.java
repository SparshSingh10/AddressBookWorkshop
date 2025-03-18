package com.example.AddressBookWorkshop.interfaces;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

public class TestEmail {
    public static void main(String[] args) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("sparsh262002@gmail.com");  // Replace with your email
        mailSender.setPassword("ibyjxduuoegbipbz");  // Replace with your App Password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        try {
            mailSender.testConnection();
            System.out.println("✅ SMTP Connection Successful!");
        } catch (Exception e) {
            System.out.println("❌ SMTP Connection Failed!");
            e.printStackTrace();
        }
    }
}
