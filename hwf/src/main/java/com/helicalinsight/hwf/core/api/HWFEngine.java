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

import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.hwf.core.ICondition;
import com.helicalinsight.hwf.core.IIterator;
import com.helicalinsight.hwf.core.IJobProcess;
import com.helicalinsight.hwf.exception.HwfException;
import com.helicalinsight.hwf.util.HWFUtils;
import com.helicalinsight.hwf.validator.WorkFlowValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class HWFEngine {

    private static final Logger logger = LoggerFactory.getLogger(HWFEngine.class);


    private String nextComponent;
    private JSONObject action;
    private Stack<JSONObject> iteratorStack = new Stack<>();
    private List<String> executionSequence;
    private Map<String, JSONObject> flowMap;
    private JSONObject hwfFileAsJson;
    private String hwfFilePath;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private JSONObject scheduledInput;


    public HWFEngine(String hwfFilePath, HttpServletRequest request,HttpServletResponse response) {
        this.hwfFilePath = hwfFilePath;
        this.request = request;
        this.response=response;
        this.flowMap = new HashMap<>();
        this.executionSequence = new ArrayList<>();
        this.setHwfJson();
    }

    /**
     * Convert the HWF file into JSON object to start further processing.
     */
    private void setHwfJson() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        hwfFileAsJson = processor.getJSONObject(hwfFilePath, false);
        WorkFlowValidator.validateHwfJson(this.hwfFileAsJson);
    }

    public HWFEngine(String hwfFilePath, JSONObject shuffledInput) {
        this.hwfFilePath = hwfFilePath;
        this.flowMap = new HashMap<>();
        this.executionSequence = new ArrayList<>();
        this.scheduledInput = shuffledInput;
        this.setHwfJson();
    }

    public JSONObject processHWF() {

        JSONObject globalHwfInput = hwfFileAsJson.getJSONObject("input");
        JSONArray hwfFlowList = hwfFileAsJson.getJSONArray("flow");
        JSONObject shuffledInput;
        if (request != null) {
            HWFInputManipulator hwfInputOperation = new HWFInputManipulator(globalHwfInput, this.request);
            shuffledInput = hwfInputOperation.shuffleInput();
        } else {
            shuffledInput = this.scheduledInput;
        }
        logger.info("shuffledInput " + shuffledInput);
        this.createFlowMap(hwfFlowList);

        JSONObject outputJSON = doFlow(shuffledInput, hwfFlowList);

        JSONObject hwfOutputJson = hwfFileAsJson.getJSONObject("output");

        HWFOutPutOperation hwfOutPutOperation = new HWFOutPutOperation();

        JSONObject jsonOutput = new JSONObject();
        jsonOutput.put("viewPage", hwfOutputJson.optString("@viewPage"));
        boolean isFlow = hwfOutputJson.optBoolean("@showFlow");
        boolean isStack = hwfOutputJson.optBoolean("@showStack");
        hwfOutputJson.discard("@showFlow");
        hwfOutputJson.discard("@showStack");
        hwfOutputJson.discard("@viewPage");

        jsonOutput.putAll(setActualOutput( shuffledInput, outputJSON, hwfOutputJson, hwfOutPutOperation));
        if (isFlow) {
            jsonOutput.put("flowSequence", executionSequence);
        }
        if (isStack) {
            jsonOutput.put("stacks", iteratorStack);
        }

        return jsonOutput;
    }

    public void createFlowMap(JSONArray hwfFlow) {
        this.nextComponent = hwfFlow.getJSONObject(0).getString("@alias");

        for (int index = 0; index < hwfFlow.size(); index++) {
            JSONObject componentJson = hwfFlow.getJSONObject(index);
            String aliasName = componentJson.getString("@alias");
            JSONObject action = new JSONObject();
            if (componentJson.has("action")) {
                Object actionArray = componentJson.get("action");
                if (actionArray instanceof JSONArray) {
                    action.put("executeNext", componentJson.getJSONArray("action").getString(0));
                } else {
                    action = componentJson.getJSONObject("action");
                }
            } else {
                action.put("executeNext", "null");
            }
            action.put("index", index);
            flowMap.put(aliasName, action);
        }
        logger.info("Flow map is " + flowMap);
    }

    public JSONObject doFlow(JSONObject inputJson, JSONArray executionList) {

        this.action = flowMap.get(nextComponent);
        JSONObject outputJson = new JSONObject();

        while (evaluateCondition()) {
            executionSequence.add(nextComponent);
            int index = action.getInt("index");
            JSONObject processJson = executionList.getJSONObject(index);

            JSONObject jobInput = processInputs(inputJson, outputJson, processJson);
            executeFlow(outputJson, jobInput, processJson);

            logger.info("nextComponent is set to " + nextComponent);
            action = flowMap.get(nextComponent);
        }

        logger.debug("The outputJson is  " + outputJson);
        return outputJson;
    }

    private JSONObject setActualOutput(JSONObject inputJSON, JSONObject outputJSON, JSONObject hwfOutputJson,
                                       HWFOutPutOperation hwfOutPutOperation) {
        JSONObject mainOutputFilter = hwfOutPutOperation.outputJSON(hwfOutputJson);

        JSONObject display = hwfOutPutOperation.outProcess(hwfOutputJson, outputJSON, inputJSON);
        List<String> outPutKeys = JsonUtils.getKeys(mainOutputFilter);

        JSONObject jsonOutput = new JSONObject();
        for (String outPutType : outPutKeys) {

            String outputFromInputTag = "";
            if (mainOutputFilter.get(outPutType) instanceof JSONArray) {
                processOutputArray(hwfOutPutOperation, jsonOutput, mainOutputFilter, display, outPutType,
                        outputFromInputTag);
            } else {
                processOutputJson(hwfOutPutOperation, jsonOutput, mainOutputFilter, display, outPutType,
                        outputFromInputTag);
            }
        }
        return jsonOutput;
    }

    public boolean evaluateCondition() {

        if (!iteratorStack.isEmpty() && "null".equalsIgnoreCase(nextComponent)) {
            setNextComponent(ProcessType.FOUND_ITERATOR_IN_STACK);
            return true;
        }

        if (action == null && (!"null".equalsIgnoreCase(nextComponent))) {
            throw new HwfException("No such component (" + nextComponent + ") found");
        }

        return !"null".equalsIgnoreCase(nextComponent);
    }

    private JSONObject processInputs(JSONObject inputJson, JSONObject outputManipulation,
                                     JSONObject processJsonObject) {
        boolean isArrayProcessInput = WorkFlowValidator.isArrayComponentInput(processJsonObject);
        JSONObject jobInputs = new JSONObject();
        JSONArray inputArray;
        if (isArrayProcessInput) {
            inputArray = processJsonObject.getJSONArray("input");
            for (int index = 0; index < inputArray.size(); index++) {
                Object expectedInputValue = inputArray.get(index);
                String inputValue;
                String forString = null;
                if (expectedInputValue instanceof JSONObject) {
                    JSONObject modifiedInput = inputArray.getJSONObject(index);
                    inputValue = modifiedInput.getString("#text");
                    forString = modifiedInput.getString("@for");
                    String required = modifiedInput.optString("@required");
                    if ("true".equalsIgnoreCase(required) && !(inputJson.containsKey(inputValue) ||
                            outputManipulation.containsKey(inputValue))) {
                        throw new HwfException("The required input for " + forString + " is missing");

                    }

                } else {
                    inputValue = expectedInputValue.toString();
                }
                boolean isInputPresent = inputJson.containsKey(inputValue);
                if (isInputPresent) {
                    jobInputs.accumulate(forString == null ? inputValue : forString, inputJson.getString(inputValue));
                } else if (outputManipulation.containsKey(inputValue)) {
                    jobInputs.accumulate(forString == null ? inputValue : forString,
                            outputManipulation.getString(inputValue));
                }
            }
        } else {
            String inputValue = processJsonObject.getString("input");
            jobInputs.accumulate(inputValue, inputJson.getString(inputValue));
        }
        return jobInputs;
    }

    private void executeFlow(JSONObject outputManipulation, JSONObject inputForSpecificJob,
                             JSONObject processJsonObject) {

        JSONObject output = executeProcess(inputForSpecificJob, processJsonObject);

        if (processJsonObject.containsKey("output")) {
            String outputString = processJsonObject.getString("output");
            boolean outputExist = outputManipulation.containsKey(outputString);
            if (outputExist) {
                outputManipulation.discard(outputString);
            }
            outputManipulation.accumulate(outputString, output);
        }
    }

    private void processOutputArray( HWFOutPutOperation hwfOutPutOperation, JSONObject jsonOutput,
                                    JSONObject mainOutputFilter, JSONObject display, String outPutType,
                                    String outputFromInputTag) {
        JSONArray filterOutputJSONArray = mainOutputFilter.getJSONArray(outPutType);
        for (int index = 0; index < filterOutputJSONArray.size(); index++) {
            JSONObject displayResult;
            JSONObject outPutData;
            JSONObject filterOutputArrayItem = filterOutputJSONArray.getJSONObject(index);
            String filterOutputTextKey = filterOutputArrayItem.getString("#text");
            if (display.containsKey(filterOutputTextKey)) {
                if (display.get(filterOutputTextKey) instanceof JSONObject) {
                    outPutData = display.getJSONObject(filterOutputTextKey);
                    displayResult = hwfOutPutOperation.getHwfSourcesOutPut(response, outPutType, outPutData, outputFromInputTag);
                    jsonOutput.accumulate(filterOutputTextKey, displayResult);
                } else if (display.get(filterOutputTextKey) instanceof JSONArray) {
                    JSONArray jsonArray = display.getJSONArray(filterOutputTextKey);
                    jsonOutput.accumulate(filterOutputTextKey, display.getJSONArray(filterOutputTextKey)
                            .getJSONObject(jsonArray.size() - 1));
                    break;
                } else {
                    outputFromInputTag = display.getString(filterOutputTextKey);
                    if (outputFromInputTag.length() > 0) {
                        jsonOutput.accumulate(filterOutputTextKey, outputFromInputTag);
                    }

                }
            }
        }
    }

    private void processOutputJson( HWFOutPutOperation hwfOutPutOperation, JSONObject jsonOutput,
                                   JSONObject mainOutputFilter, JSONObject display, String outPutType,
                                   String outputFromInputTag) {
        JSONObject jsonOuputDisplay;
        logger.debug("mainOutputFilter:  " + mainOutputFilter);
        logger.debug("outPutType:  " + outPutType);
        String outputFilterTextKey = mainOutputFilter.getJSONObject(outPutType).getString("#text");
        if (display.containsKey(outputFilterTextKey)) {
            if (display.get(outputFilterTextKey) instanceof JSONObject) {
                jsonOuputDisplay = display.getJSONObject(outputFilterTextKey);
                jsonOuputDisplay = hwfOutPutOperation.getHwfSourcesOutPut(response, outPutType, jsonOuputDisplay,
                        outputFromInputTag);
                logger.debug("jsonoutPutToDiaplay:  " + jsonOuputDisplay);
                jsonOutput.accumulate(outputFilterTextKey, jsonOuputDisplay);
            } else if (display.get(outputFilterTextKey) instanceof JSONArray) {
                logger.debug("OutPut is an instance of jsonArray:");
                int size = display.getJSONArray(outputFilterTextKey).size();
                jsonOuputDisplay = display.getJSONArray(outputFilterTextKey).getJSONObject(size - 1);
                jsonOuputDisplay = hwfOutPutOperation.getHwfSourcesOutPut(response, outPutType, jsonOuputDisplay,
                        outputFromInputTag);
                logger.debug("jsonoutPutToDiaplay:  " + jsonOuputDisplay);
                jsonOutput.accumulate(outputFilterTextKey, jsonOuputDisplay);
            } else {
                jsonOutput.accumulate(outputFilterTextKey, display.getString(outputFilterTextKey));
            }
        }
    }

    public void setNextComponent(int type) {
        switch (type) {

            case ProcessType.CONDITIONAL_PROCESS_TRUE:
                if (action.has("ifTrue")) {
                    nextComponent = action.getString("ifTrue");
                } else {
                    nextComponent = "null";
                }
                break;

            case ProcessType.CONDITIONAL_PROCESS_FALSE:
                if (action.has("ifFalse")) {
                    nextComponent = action.getString("ifFalse");
                } else {
                    nextComponent = "null";
                }
                break;

            case ProcessType.NORMAL_PROCESS:
                nextComponent = action.getString("executeNext");
                break;

            case ProcessType.ITERATIVE_PROCESS:
                if (action.has("executeNext")) {
                    nextComponent = action.getString("executeNext");
                } else {
                    nextComponent = action.getString("iterate");
                }
                break;

            case ProcessType.ITERATIVE_SUCCESS:
                if (action.has("exit")) {
                    nextComponent = action.getString("exit");
                } else if (action.has("executeNext")) {
                    nextComponent = action.getString("executeNext");
                } else {
                    nextComponent = "null";
                }
                break;

            case ProcessType.ITERATIVE_EXCEPTION:
                if (action.has("onException")) {
                    nextComponent = action.getString("onException");
                } else {
                    nextComponent = "null";
                }

            case ProcessType.FOUND_ITERATOR_IN_STACK:
                JSONObject iterateJson = iteratorStack.pop();
                this.action = iterateJson.getJSONObject("action");
                this.action.put("state", iterateJson.getJSONObject("state"));
                nextComponent = iterateJson.getString("aliasName");
                break;

        }

    }

    public JSONObject executeProcess(JSONObject jsonInput, JSONObject processData) {
        String type = processData.getString("@type");
        IJobProcess iJobProcess;
        JSONObject returnValue;
        if (HWFUtils.getTypeClassMap().containsKey(type)) {
            String hwfClass = HWFUtils.getTypeClassMap().get(type);
            Object instanceClass = instantiateClass(hwfClass);
            if (HWFUtils.isClass(hwfClass)) {
                iJobProcess = (IJobProcess) instanceClass;
                Integer executionStatus;
                if (instanceClass instanceof ICondition) {
                    returnValue = iJobProcess.jobProcess(jsonInput, processData);
                    boolean response = returnValue.getBoolean("response");
                    executionStatus = response ? ProcessType.CONDITIONAL_PROCESS_TRUE : ProcessType
                            .CONDITIONAL_PROCESS_FALSE;
                    setNextComponent(executionStatus);

                } else if (instanceClass instanceof IIterator) {
                    returnValue = processIteration(jsonInput, processData, iJobProcess, (IIterator) instanceClass);

                } else {
                    returnValue = iJobProcess.jobProcess(jsonInput, processData);
                    executionStatus = ProcessType.NORMAL_PROCESS;
                    setNextComponent(executionStatus);
                }
                this.executionSequence.add(iJobProcess.executionStatus().toString());
                logger.debug("returnValue:   " + returnValue);

            } else {
                throw new ClassNotConfiguredException("The class (" + hwfClass + ") is not found");
            }
        } else {
            throw new ClassNotConfiguredException("The type (" + type + ") is not configured");
        }

        return returnValue;
    }

    private Object instantiateClass(String hwfClass) {
        return FactoryMethodWrapper.getUntypedInstance(hwfClass);
    }

    private JSONObject processIteration(JSONObject jsonInput, JSONObject processData, IJobProcess iJobProcess,
                                        IIterator iIterator) {
        if (action.has("state")) {
            jsonInput.put("nextState", action.getJSONObject("state"));
        }
        JSONObject returnValue = iJobProcess.jobProcess(jsonInput, processData);
        //should invoke a stack that will put the counter
        JSONObject response = new JSONObject();
        if (iIterator.hasNext()) {
            JSONObject iteratorJson = new JSONObject();
            iteratorJson.put("action", action);
            iteratorJson.put("aliasName", nextComponent);
            response = returnValue.getJSONObject("response");
            iteratorJson.put("state", response);
            iteratorJson.getJSONObject("state").discard("process");
            iteratorStack.push(iteratorJson);
        }


        if (response.has("process")) {
            Object process = response.get("process");
            if (process instanceof JSONObject) {
                JSONObject processToJson = (JSONObject) process;
                returnValue.putAll(processToJson);
                returnValue.discard("response");
                returnValue.discard("status");
            }
        }
        //executionSequence.add(iteratorStack.toString());

        int iteratorExecutionStatus = iIterator.executionStatus().getInt("executionStatus");
        if (iteratorExecutionStatus == ExecutionStatus.SUCCESS) {
            setNextComponent(ProcessType.ITERATIVE_SUCCESS);
        } else if (iteratorExecutionStatus == ExecutionStatus.ERROR) {
            setNextComponent(ProcessType.ITERATIVE_EXCEPTION);
        } else {
            setNextComponent(ProcessType.ITERATIVE_PROCESS);
        }
        return returnValue;
    }


}
