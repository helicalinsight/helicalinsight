package com.helicalinsight.admin.management;

/**
 * Created by author on 12/3/2019.
 *
 * @author Rajesh
 */
public class HIJasperThread extends Thread {

    private CancellableRunnable runnable;

    public HIJasperThread(CancellableRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        runnable.cancel();
    }

}
