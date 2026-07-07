package com.helicalinsight.hwf.component.input;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.hwf.core.IRequest;
import com.helicalinsight.hwf.exception.HwfException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class InputProcess implements IRequest {
    private static final Logger logger = LoggerFactory.getLogger(InputProcess.class);

    public String sanitize(String input) {
        String result = null;
        if (input != null) {
            Base64 base64 = new Base64();
            try {
                result = new String(base64.decode(input), ControllerUtils.defaultCharSet());
            } catch (Exception ignore) {
                logger.error("Encoding exception occurred " + ignore);
            }
            return result;
        } else {
            return input;
        }
    }

    private String[] getDecodedIfEncoded(HttpServletRequest request, String paramName) {
        String values[] = request.getParameterValues(paramName);
        if (values == null) {
            return null;
        }
        for (int index = 0; index < values.length; index++) {
            values[index] = sanitize(values[index]);
        }

        return values;
    }

    private String getDecodedIfEncodedParam(HttpServletRequest request, String paramName) {
        String values = request.getParameter(paramName);
        if (values == null) {
            return null;
        }
        values = sanitize(values);

        return values;
    }

    @Override
    public JSONObject getRequestValue(HttpServletRequest request, JSONObject globalInput, JSONArray commonInputArray,
                                      JSONObject commonInputJson) {

        JSONObject inputJsonObjectWithParameterValue = new JSONObject();
        Enumeration<String> parameterNames = request.getParameterNames();
        ArrayList<String> parameterList = Collections.list(parameterNames);

        String base64Encoded = request.getParameter("base64Encoded");
        boolean isBase64Requested = StringUtils.isNotBlank(base64Encoded) && base64Encoded.equalsIgnoreCase("true");
        for (int count = 0; count < commonInputArray.size(); count++) {

            JSONObject commonInputArrayItem = commonInputArray.getJSONObject(count);
            String commInputArrayItemKey = commonInputArrayItem.getString("inputKey");

            String urlKeyName = commonInputArrayItem.getString("#text");
            if (parameterList.contains(urlKeyName)) {
                logger.debug("Conditional Block" + urlKeyName);
                String[] parameterValues;

                if (isBase64Requested) {
                    parameterValues = getDecodedIfEncoded(request, urlKeyName);

                } else {
                    parameterValues = request.getParameterValues(urlKeyName);
                }
                inputJsonObjectWithParameterValue.accumulate(commInputArrayItemKey,
                        parameterValues.length == 1 ? parameterValues[0] : parameterValues);
            } else {
                boolean defaultData = commonInputArrayItem.has("@default");
                if (defaultData) {
                    inputJsonObjectWithParameterValue.accumulate(commInputArrayItemKey,
                            commonInputArrayItem.getString("@default"));
                } else {
                    throw new HwfException("Input value " + commInputArrayItemKey + " is not available in " +
                            "request/Input tag does not have default value ");
                }
            }
        }

        if (!(commonInputArray.size() > 0)) {
            String urlKey = commonInputJson.getString("#text");
            if (parameterList.contains(urlKey)) {
                String parameter = request.getParameter(urlKey);
                inputJsonObjectWithParameterValue.accumulate(commonInputJson.getString("inputKey"),
                        isBase64Requested ? getDecodedIfEncodedParam(request, urlKey) : parameter);
            } else {
                boolean defaultData = commonInputJson.has("@default");
                if (defaultData) {
                    inputJsonObjectWithParameterValue.accumulate(commonInputJson.getString("inputKey"),
                            commonInputJson.getString("@default"));
                } else {
                    throw new HwfException("Input value is not available in request/Input tag does not have default "
                            + "value ");
                }
            }
        } else {
            logger.info("if not called " + commonInputArray.size());
        }
        logger.debug("inputJsonObjectWithParameterValue:  " + inputJsonObjectWithParameterValue);
        return inputJsonObjectWithParameterValue;
    }
}
