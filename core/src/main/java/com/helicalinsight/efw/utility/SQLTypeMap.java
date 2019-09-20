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


import java.sql.Types;

/**
 * Converts database types to Java class types.
 *
 * @author Somen
 * @author Rajasekhar
 */
public class SQLTypeMap {
    /**
     * Translates a data type from an integer (java.sql.Types value) to a string
     * that represents the corresponding class.
     *
     * @param type The java.sql.Types value to convert to its corresponding class.
     * @return The class that corresponds to the given java.sql.Types
     * value, or Object.class if the type has no known mapping.
     */

    public static Class<?> toClass(int type) {
        Class<?> result = Object.class;

        switch (type) {
            case Types.CHAR://fallthrough
            case Types.VARCHAR://fallthrough
            case Types.LONGVARCHAR:
                result = String.class;
                break;

            case Types.NUMERIC://fallthrough
            case Types.DECIMAL://fallthrough
                result = java.math.BigDecimal.class;
                break;

            case Types.BIT:
                result = Boolean.class;
                break;

            case Types.TINYINT:
                result = Byte.class;
                break;

            case Types.SMALLINT:
                result = Short.class;
                break;

            case Types.INTEGER:
                result = Integer.class;
                break;

            case Types.BIGINT:
                result = Long.class;
                break;

            case Types.REAL://fallthrough
            case Types.FLOAT:
                result = Float.class;
                break;

            case Types.DOUBLE:
                result = Double.class;
                break;

            //All other types are treated as java.lang.Object
            /*case Types.BINARY://fallthrough
            case Types.VARBINARY://fallthrough
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;*/

            case Types.DATE:
                result = java.sql.Date.class;
                break;

            case Types.TIME:
                result = java.sql.Time.class;
                break;

            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
        }
        return result;
    }
}