package com.bytewheel.carrent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  public JavaMailSender emailSender;

  /**
   * Method to send a message/alerts to user
   *
   * @param toSend      email-id of the user where actual messageText needs to be sent
   * @param subject     subject of the mail
   * @param messageText actual message to be sent
   */
  public void sendSimpleMessage(String toSend, String subject, String messageText) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toSend);
    message.setSubject(subject);
    message.setText(messageText);
    emailSender.send(message);
  }
}
