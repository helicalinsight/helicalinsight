package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by author on 10/15/2015.
 *
 * @author Rajasekhar
 */
interface GraphAlgorithm {

    @Nullable
    List<Node> connectedComponents();

    boolean isValid(@Nullable List<Node> combinedPath);
}
