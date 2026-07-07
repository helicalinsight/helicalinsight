package com.helicalinsight.adhoc.jreport;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.management.CancellableRunnable;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.stream.StreamSession;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * This class HelicalJasperReportRunner implements {@link CancellableRunnable}
 * and is responsible for generating HCR (Helical Canned Report) reports. It
 * uses a provided form data to generate the report and sends the response back.
 * Class is used to generate HCR report and sends response back.
 * 
 * Created by author on 12/3/2019.
 * 
 * @author Rajesh
 */
public class HelicalJasperReportRunner implements CancellableRunnable {
    private static final Logger logger = LoggerFactory.getLogger(HelicalJasperReportRunner.class);
    private JsonObject formData;
    private JsonObject response;
    private final StreamSession session;
    private MutableBoolean bool = new MutableBoolean();


    public HelicalJasperReportRunner(JsonObject formData, JsonObject response, StreamSession session) {
        this.formData = formData;
        this.response = response;
        this.session = session;
    }

    @Override
    public void cancel() {
        bool.setValue(false);
        response.addProperty("message", "Interrupted/Canceled the current executing process.");
    }

    @Override
    public void run() {
        bool.setValue(true);
        if (bool.booleanValue()) {
            IHCRGenerator generator = (IHCRGenerator) ApplicationContextAccessor.getBean(JsonUtils.getHCRGeneratorType());
            long now = System.currentTimeMillis();
            if ( session != null ) {
            	generator.generateHCReportStreaming(formData, session);
            }
            else {
            	JsonObject responseFromJasper =  generator.generateHCReport(formData);
	            for(String key: responseFromJasper.keySet()){
	            	response.add(key, responseFromJasper.get(key));
	            }
            }
            long time = System.currentTimeMillis();
            logger.info("Time taken for Entire Report :" + (time - now) + ".ms");
        }
    }
}
