package com.helicalinsight.adhoc.metadata;

import com.helicalinsight.efw.framework.FrameworkObject;
/**
 * ISecureMetadata
 * 
 * This interface defines methods for evaluating conditions and obtaining filters
 * for secure metadata.
 */
public interface ISecureMetadata extends FrameworkObject {
	
	/**
     * Evaluates the given condition.
     * 
     * @param condition 		 condition to evaluate.
     * @return {@code true} if the condition evaluates to true, otherwise {@code false}
     */
    boolean evaluateCondition(String condition);
    /**
     * Gets the filters based on the provided filter.
     * 
     * @param filter 		 filter to obtain.
     * @return The filters.
     */
    String getFilters(String filter);
}