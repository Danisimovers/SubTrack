package ru.project.subtrack.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        // Указываем почту отправителя (она должна совпадать с spring.mail.username)
        message.setFrom("Lancet122@yandex.ru");

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        System.out.println("Отправка письма:");
        System.out.println("От: " + message.getFrom());
        System.out.println("Кому: " + to);
        System.out.println("Тема: " + subject);
        System.out.println("Текст: " + text);

        mailSender.send(message);
    }
}
