package it.cleverad.engine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class TESTMAIL {

    @Autowired
    private JavaMailSender emailSender;

    @Test
    public void testaMail(){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("test@cleverad.it");
        message.setTo("ivo@lachilah.it");
        message.setSubject("TEST MAIL CLEVERAD");
        message.setText("CONTENUTO DI TESTTTT");

        emailSender.send(message);

    }


}
