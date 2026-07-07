package com.helicalinsight.efw;

/**
 * @author Somen
 * created on 3/5/2018.
 */
public interface IBackgroundService {
   // Todo Should move to core module
	void start();
    void stop();
    boolean setUp();
    boolean cleanUp();
}
