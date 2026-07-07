package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.efw.framework.FrameworkObject;

/**
 * IQueryGenerator class extends {@link FrameworkObject}
 * Created by author on 03-03-13
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
public interface IQueryGenerator extends FrameworkObject {
	 /**
     * Prepares a SQL query based on the provided metadata JSON and form data.
     * 
     * @param metadataJson 		 JSON string representing the metadata.
     * @param formData     		 JSON string representing the form data.
     * @return The prepared SQL query string.
     */
    String prepareQuery(String metadataJson, String formData);

}
