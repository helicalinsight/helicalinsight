package com.helicalinsight.adhoc.genericsql;

import java.util.List;

/**
 * Created by author on 10/21/2015.
 *
 * @author Rajasekhar
 */
interface BreadthFirstSearch<T extends Comparable<T>> {

    List<T> bfs(T source, T destination);
}
