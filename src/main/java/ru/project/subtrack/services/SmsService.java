package ru.project.subtrack.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class SmsService {
    private static final Logger logger = Logger.getLogger(SmsService.class.getName());

    @Value("${sms.aero.email}")
    private String email;

    @Value("${sms.aero.api_key}")
    private String apiKey;

    @Value("${sms.aero.api_url}")
    private String apiUrl;

    @Value("${sms.aero.sign}")
    private String sign;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendSms(String phone, String message) {
        try {
            logger.info("Начало отправки SMS на " + phone);
            if (phone.startsWith("+")) {
                phone = phone.substring(1);
            }

            String url = apiUrl + "/sms/send";

            Map<String, Object> requestBody = Map.of(
                    "number", phone,
                    "text", message,
                    "sign", sign
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(email, apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            logger.info("Ответ SMS Aero: " + response);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object success = response.getBody().get("success");
                if (Objects.equals(success, true)) {
                    logger.info("SMS успешно отправлено!");
                    return "SMS успешно отправлено!";
                } else {
                    logger.warning("Ошибка SMS-сервиса: " + response.getBody());
                    return "Ошибка SMS-сервиса: " + response.getBody();
                }
            }

            logger.warning("Ошибка SMS-сервиса: Некорректный ответ");
            return "Ошибка SMS-сервиса: Некорректный ответ";
        } catch (Exception e) {
            logger.severe("Ошибка отправки SMS: " + e.getMessage());
            return "Ошибка отправки SMS: " + e.getMessage();
        }
    }
}
