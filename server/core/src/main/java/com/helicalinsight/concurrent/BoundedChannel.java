package com.helicalinsight.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BoundedChannel<T>  {
	
	private final BlockingQueue<T> queue;
	private final T poison;
	private final int consumers;
	
	public BoundedChannel(int capacity, int consumers, T poison) {
		this.queue = new ArrayBlockingQueue<>(capacity);
		this.consumers = consumers;
		this.poison = poison;
	}
	
	public void publish(T item) throws InterruptedException {
		queue.put(item);
	}
	
	public T consume() throws InterruptedException {
		return queue.take();
	}
	
	public void clear() {
		queue.clear();
	}
	public void close() throws InterruptedException {
		for(int i=0; i< consumers; i++) {
			queue.put(poison);
		}
	}
}
