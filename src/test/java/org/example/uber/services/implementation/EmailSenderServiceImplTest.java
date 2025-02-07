package org.example.uber.services.implementation;

import org.example.uber.services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailSenderServiceImplTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Test
    void sendEmail() {
//        emailSenderService.sendEmail(
//                "suspiciousnash5@tomorjerry.com",
//                "This is sample subject",
//                "This is sample body"
//        );

        String[] emails ={"suspiciousnash5@tomorjerry.com", "thakurabhiraj802@gmail.com"};
        emailSenderService.sendEmail(emails,"Sample subject", "Sample body");
    }
}