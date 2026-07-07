package com.helicalinsight.hwf.component.output;

import com.helicalinsight.hwf.core.IResponse;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;


public class OutputProcess implements IResponse {
    private static final Logger logger = LoggerFactory.getLogger(OutputProcess.class);

    @Override
    public JSONObject setOutPutResponse(HttpServletResponse response, JSONObject outputTag, String outputFromInputTag) {
        logger.debug("outputTag:  " + outputTag);
        logger.debug("outputValue:  " + outputTag.size());
        logger.debug("outputFromInputTag:  " + outputFromInputTag.length());
        return outputTag;
    }
}
