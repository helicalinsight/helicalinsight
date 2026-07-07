package com.helicalinsight.efw.smtp.transport.factory;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import com.helicalinsight.efw.smtp.transport.connection.ClosableSmtpConnection;
import com.helicalinsight.efw.smtp.transport.connection.DefaultClosableSmtpConnection;
import com.helicalinsight.efw.smtp.transport.strategy.ConnectionStrategy;
import com.helicalinsight.efw.smtp.transport.strategy.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.event.TransportListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A part of the code of this class is taken from the Spring <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSenderImpl.html">JavaMailSenderImpl class</a>.
 */
public class SmtpConnectionFactory implements PooledObjectFactory<ClosableSmtpConnection> {

  private static final Logger LOG = LoggerFactory.getLogger(SmtpConnectionFactory.class);

  protected final Session session;

  protected final TransportStrategy transportFactory;
  protected final ConnectionStrategy connectionStrategy;

  protected final boolean invalidateConnectionOnException;

  protected List<TransportListener> defaultTransportListeners;

  public SmtpConnectionFactory(Session session, TransportStrategy transportStrategy, ConnectionStrategy connectionStrategy, boolean invalidateConnectionOnException, Collection<TransportListener> defaultTransportListeners) {
    this.session = session;
    this.transportFactory = transportStrategy;
    this.connectionStrategy = connectionStrategy;
    this.invalidateConnectionOnException = invalidateConnectionOnException;
    this.defaultTransportListeners = new ArrayList<>(defaultTransportListeners);
  }

  public SmtpConnectionFactory(Session session, TransportStrategy transportFactory, ConnectionStrategy connectionStrategy, boolean invalidateConnectionOnException) {
    this(session, transportFactory, connectionStrategy, invalidateConnectionOnException, Collections.<TransportListener>emptyList());
  }


  @Override
  public PooledObject<ClosableSmtpConnection> makeObject() throws Exception {
    LOG.debug("makeObject");

    Transport transport = transportFactory.getTransport(session);
    connectionStrategy.connect(transport);

    DefaultClosableSmtpConnection closableSmtpTransport = new DefaultClosableSmtpConnection(transport, invalidateConnectionOnException);
    initDefaultListeners(closableSmtpTransport);

    return new DefaultPooledObject(closableSmtpTransport);
  }

  @Override
  public void destroyObject(PooledObject<ClosableSmtpConnection> pooledObject) {
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("destroyObject [{}]", pooledObject.getObject().isConnected());
      }
      pooledObject.getObject().clearListeners();
      pooledObject.getObject().getDelegate().close();
    } catch (Exception e) {
      LOG.warn(e.getMessage(), e);
    }
  }

  @Override
  public boolean validateObject(PooledObject<ClosableSmtpConnection> pooledObject) {
    boolean connected = pooledObject.getObject().isConnected();
    LOG.debug("Is connected [{}]", connected);
    return connected;
  }


  @Override
  public void activateObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
    initDefaultListeners(pooledObject.getObject());
  }

  @Override
  public void passivateObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("passivateObject [{}]", pooledObject.getObject().isConnected());
    }
    pooledObject.getObject().clearListeners();
  }


  public List<TransportListener> getDefaultListeners() {
    return Collections.unmodifiableList(defaultTransportListeners);
  }

  public boolean isInvalidateConnectionOnException() {
    return invalidateConnectionOnException;
  }

  public Session getSession() {
    return session;
  }

  public TransportStrategy getTransportFactory() {
    return transportFactory;
  }

  public ConnectionStrategy getConnectionStrategy() {
    return connectionStrategy;
  }

  private void initDefaultListeners(ClosableSmtpConnection smtpTransport) {
    for (TransportListener transportListener : defaultTransportListeners) {
      smtpTransport.addTransportListener(transportListener);
    }
  }
}