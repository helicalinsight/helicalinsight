package com.helicalinsight.efw;

/**
 * @author Somen
 *         Created by helical021 on 2/19/2019.
 *         The main intention behind this class is to manage all the thread under one class.
 */
public class HIManagedThread extends Thread {
    public HIManagedThread() {
        super();
    }

    public HIManagedThread(Runnable runnable) {
        super(runnable);
    }
}
