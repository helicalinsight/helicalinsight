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

package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * The class is able to send e-mail messages to a single recipient or multiple
 * recipients with single or multiple attachments.
 *
 * @author Sharad Sinha
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class SendMail {

    private static final Logger logger = LoggerFactory.getLogger(SendMail.class);

    /**
     * This method sends email without attachments. Uses the Java Mail API.
     *
     * @param hostName        a <code>String</code> which specifies SMTP host name
     * @param port            a <code>String</code> which specifies SMTP port number
     * @param recipients      a <code>String</code> array which specifies recipients
     * @param from            a <code>String</code> which specifies the sender
     * @param isAuthenticated a <code>String</code> which specifies value true or false
     * @param isSSLEnabled    a <code>String</code> which specifies value true or false
     * @param user            The user who is sending mail
     * @param passCode        The password of the user who is sending the mail
     * @param subject         a <code>String</code> which specifies the subject of mail
     * @param body            a <code>String</code> which specifies body of mail
     * @throws MessagingException
     * @throws AddressException
     */
    public void sendMessage(String hostName, String port, String[] recipients, String from, String isAuthenticated,
                            String isSSLEnabled, String user, String passCode, String subject,
                            String body) throws MessagingException {
        if (logger.isInfoEnabled()) {
            logger.info("Sending mail message without any attachments....");
        }
        final String userName = user;
        final String password = passCode;
        Properties properties = new Properties();
        try {
            properties.put("mail.smtp.user", user);
            properties.put("mail.smtp.auth", isAuthenticated);
            properties.put("mail.debug", "true");
            properties.put("mail.smtp.starttls.enable", isSSLEnabled);
            properties.put("mail.smtp.host", hostName);
            properties.put("mail.smtp.port", port);
        } catch (NullPointerException ex) {
            throw new ConfigurationException("The property file 'mailConfiguration' seems to be incorrectly " +
                    "configured. Please check the system configurations.");
        }

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setSentDate(new Date());
        InternetAddress[] toMailAddressArray = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            toMailAddressArray[i] = new InternetAddress(recipients[i]);
        }
        message.addRecipients(Message.RecipientType.TO, toMailAddressArray);

        message.setSubject(subject);
        message.setContent(body, "text/html; charset=" + ApplicationUtilities.getEncoding());
        Transport.send(message);
        if (logger.isInfoEnabled()) {
            logger.info("Message Sent Successfully!");
        }
    }

    /**
     * This method can send emails with attachments. Uses the Java Mail API.
     *
     * @param hostname        a <code>String</code> which specifies SMTP host name
     * @param port            a <code>String</code> which specifies SMTP port number
     * @param recipients      a <code>String</code> array which specifies recipients
     * @param from            a <code>String</code> which specifies the sender
     * @param isAuthenticated a <code>String</code> which specifies value true or false
     * @param isSSLEnabled    a <code>String</code> which specifies value true or false
     * @param user            The user who is sending mail
     * @param passCode        The password of the user who is sending the mail
     * @param subject         a <code>String</code> which specifies the subject of mail
     * @param body            a <code>String</code> which specifies body of mail
     * @param attachments     a <code>String[]</code> specify attachments which has to be
     *                        send with mail
     * @throws MessagingException
     * @throws AddressException
     * @throws IOException
     */
    public void sendMessage(String hostname, String port, String[] recipients, String from, String isAuthenticated,
                            String isSSLEnabled, String user, String passCode, String subject, String body,
                            String[] attachments) throws MessagingException, IOException {
        if (logger.isInfoEnabled()) {
            logger.info("Sending mail message with attachment....");
        }
        final String userName = user;
        final String password = passCode;
        Properties properties = new Properties();
        try {
            properties.put("mail.smtp.user", user);
            properties.put("mail.smtp.auth", isAuthenticated);
            properties.put("mail.smtp.starttls.enable", isSSLEnabled);
            properties.put("mail.debug", "true");
            properties.put("mail.smtp.host", hostname);
            properties.put("mail.smtp.port", port);
        } catch (NullPointerException ex) {
            throw new ConfigurationException("The property file 'mailConfiguration' seems to be incorrectly " +
                    "configured. Please check the system configurations.");
        }

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setSubject(subject);
        message.setSentDate(new Date());

        if (logger.isDebugEnabled()) {
            logger.debug("Recipients are: ");
        }
        InternetAddress[] toMailAddressArray = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            toMailAddressArray[i] = new InternetAddress(recipients[i]);
            if (logger.isDebugEnabled()) {
                logger.debug(recipients[i]);
            }
        }

        message.addRecipients(Message.RecipientType.TO, toMailAddressArray);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html; charset=" + ApplicationUtilities.getEncoding());

        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(messageBodyPart);

        //noinspection UnusedAssignment
        messageBodyPart = new MimeBodyPart();
        for (String attachment : attachments) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachment);
            multiPart.addBodyPart(attachmentPart);
        }

        message.setContent(multiPart);
        Transport.send(message);
        if (logger.isInfoEnabled()) {
            logger.info("Message Sent Successfully!");
        }
    }
}
