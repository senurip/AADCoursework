package com.example.CeylonEE.controller;

import com.example.CeylonEE.dto.MailDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/mail")
public class MailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody MailDetailsDTO mailDetailsDTO){
        try {
            // Validate input
            if (mailDetailsDTO.getName() == null || mailDetailsDTO.getEmail() == null ||
                    mailDetailsDTO.getSubject() == null || mailDetailsDTO.getMessage() == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("zenurie09@gmail.com");
            message.setFrom("senurirajapaksha1234@gmail.com");
            message.setSubject(mailDetailsDTO.getSubject());
            message.setText("Email: " + mailDetailsDTO.getEmail()
                    + "\nName: " + mailDetailsDTO.getName()
                    + "\nMessage: " + mailDetailsDTO.getMessage()
            );

            mailSender.send(message);
            return ResponseEntity.ok("Mail sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }
}
