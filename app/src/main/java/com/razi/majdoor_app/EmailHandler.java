package com.razi.majdoor_app;

import android.content.Context;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHandler {

    private static EmailHandler instance;

    private EmailHandler() {
    }

    public static EmailHandler getInstance() {
        if (null == EmailHandler.instance) {
            EmailHandler.instance = new EmailHandler();
        }
        return EmailHandler.instance;
    }

    public boolean sendEmail(final String email, String body, String subject, Context context) {
        AtomicBoolean isEmailSent = new AtomicBoolean(true);
        new Thread(() -> {
            PropsLoader localProps = PropsLoader.getInstance(context);
            final String username = localProps.getProperty("email.user");
            final String password = localProps.getProperty("email.pass");
            String defSubject = localProps.getProperty("email.default.subject");
            defSubject = subject == null ? defSubject : subject;

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject(defSubject);
                message.setText(body);
                Transport.send(message);
            } catch (
                    MessagingException e) {
                e.printStackTrace();
                isEmailSent.set(false);
            }
        }
        ).start();
        return isEmailSent.get();
    }
}