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

package com.helicalinsight.efw.framework;

import com.helicalinsight.efw.utility.ConfigurationFileReader;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 20-Jan-15.
 *
 * @author Rajasekhar
 */
@Component
final class ObjectStateChecker {
    private static final Map<String, String> PROPERTIES_FILE = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");

    public boolean isStateful(Class<?> objectType) {
        //Except supported loggers all the other instance variables are considered as stateful
        Field[] declaredFields = objectType.getDeclaredFields();
        List<Field> fields = Arrays.asList(declaredFields);
        if (fields.size() != 0) {
            String supportedLoggers = PROPERTIES_FILE.get("supportedLoggers");
            if (supportedLoggers == null) {
                return true;
            }
            String[] loggers = supportedLoggers.split(",");
            List<String> supportedLoggingFrameworks = Arrays.asList(loggers);
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (!(Modifier.isFinal(modifiers) && (Modifier.isStatic(modifiers)) &&
                        supportedLoggingFrameworks.contains(field.getType().toString()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
