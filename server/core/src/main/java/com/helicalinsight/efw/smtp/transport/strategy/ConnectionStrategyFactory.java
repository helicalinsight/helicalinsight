package com.helicalinsight.efw.smtp.transport.strategy;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;

/**
 * {@link jakarta.mail.Transport} supports actually 4 types of connections which are handled by this connection strategy factory
 * <ol>
 * <li>{@link jakarta.mail.Transport#connect()} =&gt; {@link  #newConnectionStrategy()} </li>
 * <li>{@link jakarta.mail.Transport#connect(String, String)} ()} =&gt; {@link  #newConnectionStrategy(String, String)} </li>
 * <li>{@link jakarta.mail.Transport#connect(String, String, String)} ()} =&gt; {@link  #newConnectionStrategy(String, String, String)} </li>
 * <li>{@link jakarta.mail.Transport#connect(String, int, String, String)} ()} =&gt; {@link  #newConnectionStrategy(String, int, String, String)} </li>
 * </ol>
 * <p>
 * Created by nlabrot on 04/06/15.
 */
public class ConnectionStrategyFactory {


  public static ConnectionStrategy newConnectionStrategy() {
    return new ConnectionStrategy() {
      @Override
      public void connect(Transport transport) throws MessagingException {
        transport.connect();
      }
    };
  }

  public static ConnectionStrategy newConnectionStrategy(final String username, final String password) {
    return new ConnectionStrategy() {
      @Override
      public void connect(Transport transport) throws MessagingException {
        transport.connect(username, password);
      }

      @Override
      public String toString() {
        return "ConnectionStrategy{" +
            "username=" + username +
            '}';
      }

    };
  }

  public static ConnectionStrategy newConnectionStrategy(final String host, final String username, final String password) {
    return new ConnectionStrategy() {
      @Override
      public void connect(Transport transport) throws MessagingException {
        transport.connect(host, username, password);
      }

      @Override
      public String toString() {
        return "ConnectionStrategy{" +
            "host=" + host +
            ", username=" + username +
            '}';
      }
    };
  }

  public static ConnectionStrategy newConnectionStrategy(final String host, final int port, final String username, final String password) {
    return new ConnectionStrategy() {
      @Override
      public void connect(Transport transport) throws MessagingException {
        transport.connect(host, port, username, password);
      }

      @Override
      public String toString() {
        return "ConnectionStrategy{" +
            "host=" + host +
            ", port=" + port +
            ", username=" + username +
            '}';
      }
    };
  }
}
