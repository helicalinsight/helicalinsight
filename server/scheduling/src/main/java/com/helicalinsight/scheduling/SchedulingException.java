package com.helicalinsight.scheduling;

/**
 * 
 * Custom exception class used for handling exceptions related to scheduling operations.
 * This SchedulingException class extends the {@code RuntimeException} class.
 * Created by author on 3/31/2020.
 * @author Rajesh
 */
public class SchedulingException extends RuntimeException {
    private static final long serialVersionUID = 6153629785582904829L;
    /**
     * Constructs a new {@code SchedulingException} with the specified detail message.
     * @param message The detail message.
     */
    public SchedulingException(String message) {
        super(message);
    }
    /**
     * Constructs a new {@code SchedulingException} with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public SchedulingException(String message, Exception cause) {
        super(message, cause);
    }
    /**
     * Constructs a new {@code SchedulingException} with the specified cause.
     * @param cause The cause of the exception.
     */
    public SchedulingException(Throwable cause) {
        super(cause);
    }
}
