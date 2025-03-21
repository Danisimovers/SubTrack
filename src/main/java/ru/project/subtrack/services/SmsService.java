package ru.project.subtrack.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class SmsService {

    @Value("${smsc.login}")
    private String login;

    @Value("${smsc.password}")
    private String password;

    @Value("${smsc.api_url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendSms(String phone, String message) {
        try {
            String url = String.format("%s?login=%s&psw=%s&phones=%s&mes=%s&fmt=3",
                    apiUrl,
                    URLEncoder.encode(login, StandardCharsets.UTF_8),
                    URLEncoder.encode(password, StandardCharsets.UTF_8),
                    URLEncoder.encode(phone, StandardCharsets.UTF_8),
                    URLEncoder.encode(message, StandardCharsets.UTF_8)
            );

            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка отправки SMS: " + e.getMessage();
        }
    }
}


