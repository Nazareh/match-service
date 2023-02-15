package com.turminaz.matchservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendSimpleMessage(
            String to, String subject, String text) {

        log.info("sending email");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nazarehteeeeeurmina@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
