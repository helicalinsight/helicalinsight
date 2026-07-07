package com.helicalinsight.efw.smtp.transport.connection;

import com.helicalinsight.efw.smtp.exception.MailSendException;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.event.TransportListener;
import jakarta.mail.internet.MimeMessage;

/**
 * Created by nlabrot on 30/04/15.
 */
public interface ClosableSmtpConnection extends AutoCloseable {

  String HEADER_MESSAGE_ID = "Message-ID";

  /**
   * Marks this pooled object to be invalid such that it is not returned in the pool when closed.
   * This is equivalent to setInvalid(true).
   */
  void invalidate();

  /**
   * Allows setting the invalid flag to true or false
   * @param invalid true if the object should not be returned in the pool when closed.
   */
  void setInvalidateConnectionOnClose(boolean invalid);

  /**
   * Send a message to a list of recipients
   *
   * @param msg
   * @param recipients
   * @throws jakarta.mail.MessagingException
   * @throws MailSendException
   */
  void sendMessage(MimeMessage msg, Address[] recipients) throws MessagingException, MailSendException;

  /**
   * Send a message. The list of recipients are taken from {@link jakarta.mail.internet.MimeMessage#getAllRecipients()}
   *
   * @param msg MimeMessage
   * @throws jakarta.mail.MessagingException
   */
  void sendMessage(MimeMessage msg) throws MessagingException;

  /**
   * Send the given array of JavaMail MIME messages in batch. Do not stop the batch when a message could not be sent
   * {@link MailSendException#getFailedMessages()} will contain the failed messages
   *
   * @param msgs Array of MimeMessage
   * @throws MailSendException in case of failure when sending a message
   */
  void sendMessages(MimeMessage... msgs) throws MailSendException;

  /**
   * Test if the current connection is connected
   *
   * @return
   */
  boolean isConnected();

  /**
   * Add a new {@link jakarta.mail.event.TransportListener}
   *
   * @param l
   */
  void addTransportListener(TransportListener l);

  /**
   * Remove the provided {@link jakarta.mail.event.TransportListener}
   *
   * @param l
   */
  void removeTransportListener(TransportListener l);

  /**
   * Clear the list of {@link jakarta.mail.event.TransportListener}
   */
  void clearListeners();

  /**
   * @return the {@link jakarta.mail.Transport} associated to this connection
   */
  Transport getDelegate();

  /**
   * @return the {@link jakarta.mail.Session}
   */
  Session getSession();
}
