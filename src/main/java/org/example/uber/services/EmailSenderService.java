package org.example.uber.services;

public interface EmailSenderService {

    public void sendEmail(String to, String subject, String body);

    public void sendEmail(String[] to, String subject, String body);
}
