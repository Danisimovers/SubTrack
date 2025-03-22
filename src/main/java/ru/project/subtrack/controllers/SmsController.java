package ru.project.subtrack.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.services.SmsService;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/sms")
public class SmsController {
    private static final Logger logger = Logger.getLogger(SmsController.class.getName());

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestParam String phone, @RequestParam String message) {
        logger.info("Запрос на отправку SMS: телефон=" + phone + ", сообщение=" + message);

        if (phone == null || phone.isBlank()) {
            logger.warning("Ошибка: телефон не может быть пустым");
            return ResponseEntity.badRequest().body("Ошибка: телефон не может быть пустым");
        }

        if (message == null || message.isBlank()) {
            logger.warning("Ошибка: сообщение не может быть пустым");
            return ResponseEntity.badRequest().body("Ошибка: сообщение не может быть пустым");
        }

        String result = smsService.sendSms(phone, message);
        logger.info("Результат отправки: " + result);

        if (result.startsWith("Ошибка")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }

        return ResponseEntity.ok(result);
    }
}
