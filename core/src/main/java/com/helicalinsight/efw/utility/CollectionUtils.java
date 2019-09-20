/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.utility;


import java.util.*;

/**
 * Created by author on 09-03-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class CollectionUtils {

    /**
     * Sorts the map elements in descending order based on values
     *
     * @param map Map which has to be sorted
     * @param <K> Key
     * @param <V> Value
     * @return Map that is sorted in descending order of values
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> first, Map.Entry<K, V> second) {
                return second.getValue().compareTo(first.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static List<String> removeAll(Collection<String> collection, Collection<String> remove) {
        List<String> list = new ArrayList<>();
        for (String string : collection) {
            if (!remove.contains(string)) {
                list.add(string);
            }
        }
        return list;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
