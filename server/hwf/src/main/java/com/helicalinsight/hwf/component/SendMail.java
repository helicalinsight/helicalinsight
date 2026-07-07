package com.helicalinsight.hwf.component;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

public class SendMail {
    public void sendMessage(String hostname, String port, String[] to, String from, String isAuthenticated,
                            String isSSLEnabled, String user, String pass, String subject, String body) {
        final String username = user;
        final String password = pass;
        Properties props = new Properties();
        props.put("mail.smtp.auth", isAuthenticated);
        props.put("mail.smtp.starttls.enable", isSSLEnabled);
        props.put("mail.smtp.host", hostname);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setSentDate(new Date());
            InternetAddress[] mailAddress_TO = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                mailAddress_TO[i] = new InternetAddress(to[i]);
            }
            message.addRecipients(Message.RecipientType.TO, mailAddress_TO);

            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException mex) {
        } catch (Exception ex) {
        }
    }

    public void sendMessage(String hostname, String port, String[] to, String from, String isAuthenticated,
                            String isSSLEnabled, String user, String pass, String subject, String body,
                            String[] attachFile) {
        final String username = user;
        final String password = pass;
        Properties props = new Properties();
        props.put("mail.smtp.auth", isAuthenticated);
        props.put("mail.smtp.starttls.enable", isSSLEnabled);
        props.put("mail.smtp.host", hostname);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setSentDate(new Date());

            InternetAddress[] mailAddress_TO = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                mailAddress_TO[i] = new InternetAddress(to[i]);
            }
            message.addRecipients(Message.RecipientType.TO, mailAddress_TO);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            for (int attach = 0; attach < attachFile.length; attach++) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(attachFile[attach]);
                multipart.addBodyPart(attachPart);
            }
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException mex) {
        } catch (Exception ex) {
        }
    }
}
