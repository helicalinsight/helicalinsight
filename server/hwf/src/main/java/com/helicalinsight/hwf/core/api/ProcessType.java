package com.helicalinsight.hwf.core.api;

/**
 * @author Somen
 *         Created on 5/14/2016.
 */
public interface ProcessType {
    int NORMAL_PROCESS = 1;
    int CONDITIONAL_PROCESS_TRUE = 2;
    int CONDITIONAL_PROCESS_FALSE = 3;
    int ITERATIVE_PROCESS = 4;
    int FOUND_ITERATOR_IN_STACK = 5;
    int ITERATIVE_SUCCESS = 6;
    int ITERATIVE_EXCEPTION = 7;

}
