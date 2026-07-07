package com.helicalinsight.efw.smtp.transport.strategy;

import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Transport;

/**
 * Connection strategy that abstract {@link jakarta.mail.Session#getTransport}
 * <p>
 * <p>
 * Created by nlabrot on 04/06/15.
 */
public interface TransportStrategy {

  Transport getTransport(Session session) throws NoSuchProviderException;

}
