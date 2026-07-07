package com.helicalinsight.efw.serviceframework;
import com.google.gson.JsonObject;
import com.helicalinsight.parallelprocessor.api.AbstractSplitter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Somen
 * Created by helical021 on 2/14/2019.
 */
@Component
@Scope("prototype")
public class DataSourceSplitter  extends AbstractSplitter{
	
    /**
     * using gson
     * newPrepareFormDataList()
     */
    @Override
    public List<JsonObject> newPrepareFormDataList() {
        List<JsonObject> formDataList = new ArrayList<>();
        formDataList.add(this.newActualFormData);
        return formDataList;
    }
    @Override
    public Object getCacheObject() {
        return null;
    }

	
}
