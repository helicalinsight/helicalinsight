package com.helicalinsight.efw.exceptions;

/**
 * This exception is thrown when the <code>FactoryMethodWrapper.getTypedInstance()</code> is
 * called with a class that is stateful.
 * <p/>
 * A class is stateful when it does not have its fields marked with final modifiers.
 * <p/>
 * Created by author on 05-12-2014.
 *
 * @author Rajasekhar
 */
public class UnSupportedRuleImplementationException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnSupportedRuleImplementationException() {
        super();
    }

    public UnSupportedRuleImplementationException(String message) {
        super(message);
    }

    public UnSupportedRuleImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedRuleImplementationException(Throwable cause) {
        super(cause);
    }
}
