package com.helicalinsight.hwf.core.api;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.hwf.core.IRequest;
import com.helicalinsight.hwf.exception.HwfException;
import com.helicalinsight.hwf.exception.InvalidHwfFileException;
import com.helicalinsight.hwf.util.HWFUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public class HWFInputManipulator {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HWFInputManipulator.class);

    private JSONObject globalHwfInput;

    private HttpServletRequest request;

    public HWFInputManipulator(JSONObject globalHwfInput, HttpServletRequest request) {
        this.request = request;
        this.globalHwfInput = globalHwfInput;
        this.isGlobalInputsValid();
    }

    private void isGlobalInputsValid() {
        this.globalHwfInput.discard("@mandatory");
        int hwfInputSize = this.globalHwfInput.size();

        if (!(hwfInputSize > 0)) {
            throw new InvalidHwfFileException("There is no input in the HWF file");
        }
    }

    public JSONObject shuffleInput() {

        JSONObject inputJSON = new JSONObject();
        JSONObject commonInput = commonInputJson();

        List<String> commonInputKeys = JsonUtils.getKeys(commonInput);

        for (String commonInputKey : commonInputKeys) {

            JSONArray commonInputArray = new JSONArray();
            JSONObject commonInputJsonObject = new JSONObject();

            if (commonInput.get(commonInputKey) instanceof JSONArray) {
                commonInputArray = commonInput.getJSONArray(commonInputKey);
            } else {
                commonInputJsonObject = commonInput.getJSONObject(commonInputKey);
            }

            inputJSON.putAll(getInputFromIRequest(commonInputKey, commonInputArray, commonInputJsonObject));
        }
        return inputJSON;
    }

    public JSONObject commonInputJson() {
        JSONObject commonInputJson = new JSONObject();

        List<String> globalInputKeys = JsonUtils.getKeys(globalHwfInput);

        for (String globalInputKey : globalInputKeys) {
            JSONObject inputJson = globalHwfInput.getJSONObject(globalInputKey);
            String inputType = inputJson.getString("@type");
            JSONObject inputValue = inputJson.accumulate("inputKey", globalInputKey);
            commonInputJson.accumulate(inputType, inputValue);
        }
        logger.debug("commonInput: " + commonInputJson);
        logger.debug("globalInput: " + this.globalHwfInput);
        return commonInputJson;
    }

    public JSONObject getInputFromIRequest(String commonInputKey, JSONArray commonInputArray,
                                           JSONObject commonInputJson) {
        JSONObject inputJsonFromIRequest = new JSONObject();
        if (HWFUtils.getTypeClassMap().containsKey(commonInputKey)) {
            String hwfClass = HWFUtils.getTypeClassMap().get(commonInputKey);
            boolean isJavaClassExists = HWFUtils.isClass(hwfClass);
            logger.debug("Java class exists: " + isJavaClassExists);
            if (isJavaClassExists) {
                IRequest irequest = (IRequest) FactoryMethodWrapper.getUntypedInstance(hwfClass);
                inputJsonFromIRequest = irequest.getRequestValue(request, globalHwfInput, commonInputArray,
                        commonInputJson);
            }
        } else {
            throw new HwfException("The hwfClass " + commonInputKey + " does Not exists");
        }
        return inputJsonFromIRequest;
    }
}
