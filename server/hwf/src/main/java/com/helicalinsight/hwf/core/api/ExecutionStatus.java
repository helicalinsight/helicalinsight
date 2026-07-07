package com.helicalinsight.hwf.core.api;


/**
 * @author Somen
 *         Created on 5/12/2016.
 */
public interface ExecutionStatus {
    Integer SUCCESS = 0;
    Integer FAIL = 1;
    Integer RUNNING = 2;
    Integer ERROR = 3;
    Integer NOT_SET = -1;
    Boolean HAS_NEXT_NOT_SET = false;


}
