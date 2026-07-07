package com.helicalinsight.efw.smtp.pool;

/**
 * Created by nlabrot on 30/04/15.
 */
public interface ObjectPoolAware {
  /**
   * Called after the object has been borrowed on the pool to set the pool on the object.
   *
   * @param objectPool
   */
  void setObjectPool(SmtpConnectionPool objectPool);

  SmtpConnectionPool getObjectPool();
}
