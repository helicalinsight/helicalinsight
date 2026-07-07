package com.helicalinsight.efw.resourceloader.rules;

/**
 * @author Rajesh
 *         Created by helical019 on 2/20/2019.
 */
@Deprecated
public interface ApplicationCacheDumpRule extends IRule {
    public void dumpCachedDataToDB(Object object);
}
