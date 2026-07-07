package com.helicalinsight.efw.smtp.transport.strategy;

import jakarta.mail.*;

/**
 * {@link jakarta.mail.Session} supports actually 4 types of get transport which are handled by this transport strategy
 * <ol>
 * <li>{@link jakarta.mail.Session#getTransport()} =&gt; {@link  #newSessiontStrategy()} </li>
 * <li>{@link jakarta.mail.Session#getTransport(String)} )} =&gt; {@link  #newProtocolStrategy(String)} </li>
 * <li>{@link jakarta.mail.Session#getTransport(jakarta.mail.URLName)} ()} =&gt; {@link  #newUrlNameStrategy(jakarta.mail.URLName)} </li>
 * <li>{@link jakarta.mail.Session#getTransport(jakarta.mail.Address)} =&gt; {@link  #newUrlNameStrategy(jakarta.mail.URLName)} </li>
 * <li>{@link jakarta.mail.Session#getTransport(jakarta.mail.Provider)} =&gt; {@link  #newProviderStrategy(jakarta.mail.Provider)} </li>
 * </ol>
 * <p>
 * <p>
 * Created by nlabrot on 04/06/15.
 */
public class TransportStrategyFactory {

  public static TransportStrategy newSessiontStrategy() {
    return new TransportStrategy() {
      @Override
      public Transport getTransport(Session session) throws NoSuchProviderException {
        return session.getTransport();
      }
    };
  }

  public static TransportStrategy newProtocolStrategy(final String protocol) {
    return new TransportStrategy() {
      @Override
      public Transport getTransport(Session session) throws NoSuchProviderException {
        return session.getTransport(protocol);
      }
    };
  }

  public static TransportStrategy newUrlNameStrategy(final URLName urlName) {
    return new TransportStrategy() {
      @Override
      public Transport getTransport(Session session) throws NoSuchProviderException {
        return session.getTransport(urlName);
      }
    };
  }

  public static TransportStrategy newAddressStrategy(final Address address) {
    return new TransportStrategy() {
      @Override
      public Transport getTransport(Session session) throws NoSuchProviderException {
        return session.getTransport(address);
      }
    };
  }

  public static TransportStrategy newProviderStrategy(final Provider provider) {
    return new TransportStrategy() {
      @Override
      public Transport getTransport(Session session) throws NoSuchProviderException {
        return session.getTransport(provider);
      }
    };
  }

}
