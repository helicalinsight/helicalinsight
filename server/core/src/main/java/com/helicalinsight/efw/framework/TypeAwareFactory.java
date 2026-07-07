package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 20-Jan-15.
 *
 * @author Rajasekhar
 */
interface TypeAwareFactory {

    @Nullable
    <T extends FrameworkObject> T getInstanceOfType(String clazz, Class<T> type);
}
