/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.hwf.component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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
