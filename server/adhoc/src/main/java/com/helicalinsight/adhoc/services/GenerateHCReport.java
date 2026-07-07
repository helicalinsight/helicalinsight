package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.jreport.HelicalJasperReportRunner;
import com.helicalinsight.admin.management.JasperMultiReportManager;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.stream.StreamSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Created by @author on 10/1/2019.
 *
 * @author Rajesh
 */
@SuppressWarnings("unused")
public class GenerateHCReport implements IComponent {
    private static final Logger logger = LoggerFactory.getLogger(GenerateHCReport.class);

    
	@Override
	public void streamResponse(String jsonFormData, StreamSession session ) {
		JsonObject response = new JsonObject();
		JsonObject formData = new Gson().fromJson(jsonFormData, JsonObject.class);
		String requestId = RequestContext.get();

		JasperMultiReportManager multiReportManager = ApplicationContextAccessor.getBean(JasperMultiReportManager.class);
		HelicalJasperReportRunner runnable = new HelicalJasperReportRunner(formData, response, session);
		try {
			multiReportManager.registerAndExecute(runnable, requestId);
		} catch (Exception e) {
			List<String> messageChain = ControllerUtils.getMessageChain(e);
			logger.error("Error occurred ", e);
			throw new HCRException(("" + messageChain + "").replaceAll("\\[", "").replaceAll("\\]", ""));
		}
	}
    
    
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject response = new JsonObject();
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        String requestId =  RequestContext.get();  
        
        JasperMultiReportManager multiReportManager = ApplicationContextAccessor.getBean(JasperMultiReportManager.class);
            HelicalJasperReportRunner runnable = new HelicalJasperReportRunner(formData, response,null);
            try {
                Future<?> future = multiReportManager.registerAndExecute(runnable, requestId);
                future.get();
            } 
            catch (Exception e) {
                List<String> messageChain = ControllerUtils.getMessageChain(e);
                logger.error("Error occurred ", e);
                throw new HCRException(("" + messageChain + "").replaceAll("\\[", "").replaceAll("\\]", ""));
            } 
        return response.toString();
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
