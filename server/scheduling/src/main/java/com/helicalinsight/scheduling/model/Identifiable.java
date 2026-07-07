package com.helicalinsight.scheduling.model;

import java.io.Serializable;

/**
 * Created by author on 4/20/2020.
 *
 * @author Rajesh
 */
public interface Identifiable<T extends Serializable> {
    T getId();
}
