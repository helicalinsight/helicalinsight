package com.helicalinsight.resourcesecurity;

import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public abstract class AbstractAccessLevel {

    @NotNull
    abstract IAccessLevel accessLevel(String type);

}
