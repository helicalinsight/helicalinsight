package com.helicalinsight.admin.management;

/**
 * Created by author on 10/28/2019.
 *
 * @author Rajesh
 */
public interface CancellableRunnable extends Runnable {
    public void cancel();
}
