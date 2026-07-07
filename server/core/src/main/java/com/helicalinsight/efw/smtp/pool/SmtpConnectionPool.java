package com.helicalinsight.efw.smtp.pool;

import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.helicalinsight.efw.smtp.transport.connection.ClosableSmtpConnection;
import com.helicalinsight.efw.smtp.transport.factory.SmtpConnectionFactory;

import jakarta.mail.Session;

/**
 * Created by nlabrot on 30/04/15.
 */
public class SmtpConnectionPool extends GenericObjectPool<ClosableSmtpConnection> {

  public SmtpConnectionPool(SmtpConnectionFactory factory) {
    super(factory);
  }

  public SmtpConnectionPool(SmtpConnectionFactory factory, GenericObjectPoolConfig config) {
    super(factory, config);
  }

  public SmtpConnectionPool(SmtpConnectionFactory factory, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
    super(factory, config, abandonedConfig);
  }

  @Override
  public ClosableSmtpConnection borrowObject() throws Exception {
    ClosableSmtpConnection object = super.borrowObject();
    if (object instanceof ObjectPoolAware) {
      ((ObjectPoolAware) object).setObjectPool(this);
    }
    return object;
  }

  @Override
  public ClosableSmtpConnection borrowObject(long borrowMaxWaitMillis) throws Exception {
    ClosableSmtpConnection object = super.borrowObject(borrowMaxWaitMillis);
    if (object instanceof ObjectPoolAware) {
      ((ObjectPoolAware) object).setObjectPool(this);
    }
    return object;
  }

  public Session getSession() {
    return ((SmtpConnectionFactory) getFactory()).getSession();
  }


}
