package ru.project.subtrack.controllers;

import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.services.SmsService;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public String sendSms(@RequestParam String phone, @RequestParam String message) {
        return smsService.sendSms(phone, message);
    }
}
