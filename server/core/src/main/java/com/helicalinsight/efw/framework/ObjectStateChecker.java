package com.helicalinsight.efw.framework;

import com.helicalinsight.efw.utility.ConfigurationFileReader;
import org.jetbrains.annotations.NotNull;
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
    private static final Map<String, String> PROPERTIES_FILE = ConfigurationFileReader.getProjectPropertiesFile();

    public boolean isStateful(@NotNull Class<?> objectType) {
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
