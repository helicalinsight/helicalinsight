package com.helicalinsight.datasource;

import com.helicalinsight.efw.framework.FrameworkObject;
import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
public interface IConnectionFactory extends FrameworkObject {

    @Nullable
    Object getConnection(String type, String jsonInfo);
}
