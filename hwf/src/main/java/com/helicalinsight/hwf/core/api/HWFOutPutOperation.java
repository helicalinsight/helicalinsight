/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.hwf.core.api;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.hwf.core.IResponse;
import com.helicalinsight.hwf.util.HWFUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class HWFOutPutOperation {
    private static final Logger logger = LoggerFactory.getLogger(HWFOutPutOperation.class);

    public JSONObject outputJSON(JSONObject output) {

        JSONObject commonOutputType = new JSONObject();
        output.discard("@mandatory");
        List<String> outputKeyList = JsonUtils.getKeys(output);
        String outputtypecheck = "";
        for (int index = 0; index < outputKeyList.size(); index++) {
            String outputKeyString = outputKeyList.get(index);
            boolean isOutputJsonArray = output.get(outputKeyString) instanceof JSONArray;
            String inputtype = "";
            if (isOutputJsonArray) {
                inputtype = output.getJSONArray(outputKeyString).getJSONObject(index).getString("@type");
            } else {
                inputtype = output.getJSONObject(outputKeyString).getString("@type");
            }
            for (int innerOutputcount = 0; innerOutputcount < outputKeyList.size(); innerOutputcount++) {
                boolean outputJSONArray1 = output.get(outputKeyString) instanceof JSONArray;
                logger.debug("isOutputJsonArray:  " + isOutputJsonArray);
                if (outputJSONArray1) {
                    outputtypecheck = output.getJSONArray(outputKeyList.get(innerOutputcount)).getJSONObject
                            (innerOutputcount).getString("@type");
                } else {
                    outputtypecheck = output.getJSONObject(outputKeyList.get(innerOutputcount)).getString("@type");
                }
                if (inputtype.equalsIgnoreCase(outputtypecheck)) {
                    JSONObject inputValue = new JSONObject();
                    if (isOutputJsonArray) {
                        for (int count = 0; count < output.getJSONArray(outputKeyString).size(); count++) {
                            inputValue = output.getJSONArray(outputKeyString).getJSONObject(count);
                            commonOutputType.accumulate(outputtypecheck, inputValue);

                        }
                    } else {
                        logger.debug("Else");
                        inputValue = output.getJSONObject(outputKeyString);
                        logger.debug("checkcommonOutputtype: " + commonOutputType.size());
                        if (commonOutputType.size() > 0) {
                            logger.info("here commonOutputType is " + commonOutputType);
                            boolean check = commonOutputType.getJSONObject(outputtypecheck).getString("#text").trim()
                                    .equals(inputValue.getString("#text").trim());
                            if (check) {
                                commonOutputType.discard(outputtypecheck);
                            }
                        }

                        commonOutputType.accumulate(outputtypecheck, inputValue);
                    }
                    break;
                }

            }
        }
        logger.debug("commonOutput: " + commonOutputType);
        return commonOutputType;
    }

    public JSONObject outProcess(JSONObject output, JSONObject filterOutPut, JSONObject inputJSON) {
        logger.debug("output===" + output);
        logger.debug("filterOutPut:  " + filterOutPut);
        JSONObject displayOutput = new JSONObject();
        List<String> keys = JsonUtils.getKeys(filterOutPut);
        logger.debug("output keys:  " + keys);
        for (String key : keys) {
            logger.debug("output contains key:  " + output.containsKey(key));
            if (output.containsKey(key)) {
                JSONObject outputJSON = new JSONObject();
                if (filterOutPut.get(key) instanceof JSONObject) {
                    outputJSON.accumulate(key, filterOutPut.getJSONObject(key));
                    logger.debug("outputJSON:  " + outputJSON);
                    // displayOutput.accumulate(output.getJSONObject(key).getString("#text"),
                    // filterOutPut.getJSONObject(key));
                }
                if (filterOutPut.get(key) instanceof JSONArray) {
                    logger.debug("filterOutPut is jsonarray");
                    outputJSON.accumulate(key, filterOutPut.getJSONArray(key));
                }
                if (output.get(key) instanceof JSONArray) {
                    for (int outputcount = 0; outputcount < output.getJSONArray(key).size(); outputcount++) {
                        displayOutput.accumulate(output.getJSONArray(key).getJSONObject(outputcount).getString
                                ("#text"), outputJSON);
                    }
                } else if (output.get(key) instanceof JSONObject) {
                    displayOutput.accumulate(output.getJSONObject(key).getString("#text"), outputJSON);
                }
                // displayOutput.accumulate(output.getJSONObject(key).getString("#text"),
                // outputJSON);
            }
        }
        keys = JsonUtils.getKeys(inputJSON);
        for (String key : keys) {
            logger.debug("output contains key:  " + output.containsKey(key));
            logger.debug("KEY IN INPUT:  " + key);
            if (output.containsKey(key)) {
                JSONObject outputJSON = new JSONObject();
                outputJSON.accumulate(key, inputJSON.getString(key));
                displayOutput.accumulate(output.getJSONObject(key).getString("#text"), outputJSON);
            }
        }

        logger.debug("displayOutput:   " + displayOutput);
        return displayOutput;
    }

    public JSONObject getHwfSourcesOutPut(HttpServletResponse response,String output, JSONObject mainOutput, String outputFromInputTag) {
        JSONObject outputJson = new JSONObject();
        logger.debug("Invoking getHwfSourcesOutPut()");
        String settingPath = ApplicationProperties.getInstance().getSettingPath();
        IResponse iresponse;
        logger.debug("output:   " + output);
        logger.debug("settingPath:  " + settingPath);

        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonFxml = processor.getJSONObject(settingPath, false);
        JSONArray setting_hwfSource = jsonFxml.getJSONArray("HwfSources");

        for (int count = 0; count < setting_hwfSource.size(); count++) {
            if (output.equalsIgnoreCase(setting_hwfSource.getJSONObject(count).getString("@type"))) {
                String hwfClass = setting_hwfSource.getJSONObject(count).getString("@class");
                logger.debug("hwfClass: " + hwfClass);
                HWFUtils chkclass = new com.helicalinsight.hwf.util.HWFUtils();
                boolean exist = chkclass.isClass(hwfClass);
                logger.debug("class exist: " + exist);
                if (exist) {
                    try {
                        iresponse = (IResponse) FactoryMethodWrapper.getUntypedInstance(hwfClass);
                        outputJson = iresponse.setOutPutResponse(response, mainOutput, outputFromInputTag);
                        logger.debug("outputJson :   " + outputJson);
                    } catch (Exception e) {
                        //handle error
                    }
                }
            }
        }
        logger.debug("outputJson:   " + outputJson);
        return outputJson;
    }
}
