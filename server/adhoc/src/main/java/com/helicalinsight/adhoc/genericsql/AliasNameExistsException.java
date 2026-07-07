package com.helicalinsight.adhoc.genericsql;

/**
 * @author Somen
 * Created on 7/3/2018.
 */
public class AliasNameExistsException extends  RuntimeException {
    public AliasNameExistsException(String message){
        super(message);
    }
}
