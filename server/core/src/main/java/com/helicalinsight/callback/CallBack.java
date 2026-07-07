package com.helicalinsight.callback;

/**
 * This interface can be used throughout the application for performing any callback operation.
 * @param <T>
 */

@FunctionalInterface
public interface CallBack<T> {	
	void process(T t);
}
