package com.helicalinsight.resourcesecurity.maxims;

import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */

public abstract class AbstractMaxim {

    @NotNull
    public abstract ISecurityMaxim maxim(String type);

}
