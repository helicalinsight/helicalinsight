package com.helicalinsight.efw.smtp.transport.factory;

import jakarta.mail.Session;
import java.util.Properties;

import static com.helicalinsight.efw.smtp.transport.strategy.ConnectionStrategyFactory.newConnectionStrategy;
import static com.helicalinsight.efw.smtp.transport.strategy.TransportStrategyFactory.newSessiontStrategy;

/**
 * {@link SmtpConnectionFactory} factory
 */
public final class SmtpConnectionFactories {

  private SmtpConnectionFactories() {
  }


  public static SmtpConnectionFactory newSmtpFactory() {
    return new SmtpConnectionFactory(Session.getInstance(new Properties()), newSessiontStrategy(), newConnectionStrategy(), false);
  }


  public static SmtpConnectionFactory newSmtpFactory(Session session) {
    return new SmtpConnectionFactory(session, newSessiontStrategy(), newConnectionStrategy(), false);
  }


}