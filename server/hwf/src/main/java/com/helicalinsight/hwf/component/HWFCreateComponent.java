package com.helicalinsight.hwf.component;

import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import com.helicalinsight.hwf.exception.HwfException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
public class HWFCreateComponent implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(HWFCreateComponent.class);
    private boolean isUpdate = false;


    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String uuid;
        if (formJson.has("uuid")) {
            uuid = formJson.getString("uuid");
            if (uuid == null || "".equals(uuid) || "".equals(uuid.trim())) {
                throw new IllegalArgumentException("The parameter uuid is null or empty");
            }
            isUpdate = true;
        } else {
            uuid = UUID.randomUUID().toString();
            isUpdate = false;
        }
        //common parameters
        String dir = formJson.getString("dir");


        //save back-end related file HWF.
        JSONObject backendData = formJson.getJSONObject("backendData");
        HWF hwf = prepareHWFObject(backendData);
        String hwfFileName = saveHWFFile(JsonUtils.getHWFExtension(), hwf, uuid, dir);

        //save front-end related file HWFD.
        JSONObject frontEndData = formJson.getJSONObject("frontEndData");
        HWFD hwfd = prepareHWFDObject(frontEndData, dir, hwfFileName);
        saveHWFDFile(JsonUtils.getHWFDExtension(), hwfd, uuid, dir);

        JSONObject responseJson;
        responseJson = new JSONObject();

        responseJson.accumulate("uuid", uuid + "." + JsonUtils.getHWFExtension());
        if (!isUpdate)
            responseJson.accumulate("message", "HWF is saved successfully");
        else if (isUpdate)
            responseJson.accumulate("message", "HWF is updated successfully");
        return responseJson.toString();
    }

    private String saveHWFFile(String hwfExtension, HWF hwf, String uuid, String dir) {
        File hwfdFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + hwfExtension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(hwf, hwfdFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The hwf couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
        return uuid + "." + hwfExtension;
    }

    private HWF prepareHWFObject(JSONObject backendData) {
        HWF hwf = ApplicationContextAccessor.getBean(HWF.class);
        //prepare input
        JSONArray inputsArray = backendData.getJSONArray("inputs");
        Inputs inputs = prepareInputSection(inputsArray);

        //prepare flow
        JSONObject flowJson = backendData.getJSONObject("flow");
        Flow flow = prepareFlowSection(flowJson);
        //prepare output
        JSONObject outputsJson = backendData.getJSONObject("outputs");
        Output output = prepareOutputSection(outputsJson);

        hwf.setInputs(inputs);
        hwf.setFlow(flow);
        hwf.setOutput(output);

        return hwf;
    }

    private Output prepareOutputSection(JSONObject outputsJson) {
        Output output = ApplicationContextAccessor.getBean(Output.class);
        output.setShowFlow(outputsJson.optString("showFlow"));
        output.setShowStack(outputsJson.optString("showStack"));
        if (outputsJson.has("viewPage")) {
            output.setViewPage(outputsJson.optString("viewPage"));
        }
        JSONArray outputJsonArray = outputsJson.getJSONArray("output");
        List<Element> listOfElements = new ArrayList<>();
        for (int index = 0; index < outputJsonArray.size(); index++) {
            JSONObject eachJson = outputJsonArray.getJSONObject(index);
            DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element element = document.createElement(eachJson.getString("responseName"));
            element.setAttribute("type", eachJson.getString("responseType"));
            element.appendChild(document.createTextNode(eachJson.getString("responseValue")));
            listOfElements.add(element);
        }
        output.setListOfElement(listOfElements);
        return output;
    }

    private Flow prepareFlowSection(JSONObject flowJson) {
        Flow flow = ApplicationContextAccessor.getBean(Flow.class);
        List<Execution> executionsList = new ArrayList<>();
        Iterator<String> keys = flowJson.keys();
        while (keys.hasNext()) {
            Execution execution = ApplicationContextAccessor.getBean(Execution.class);
            String eachKey = keys.next();
            JSONObject eachFlowJson = flowJson.getJSONObject(eachKey);
            String type = eachFlowJson.getString("type");
            Boolean isConditionalFlag = false;
            Boolean isIterativeFlag = false;
            Boolean isGroovyFlag = false;
            Boolean isDbQueryFlag = false;
            // prepareFlag(isConditionalFlag, isIterativeFlag, isGroovyFlag, isDbQueryFlag, type);
            switch (type) {
                case "job.executeDbQuery":
                    isDbQueryFlag = true;
                    break;
                case "job.conditional":
                    isConditionalFlag = true;
                    break;
                case "com.helicalinsight.loop.for":
                    isIterativeFlag = true;
                    break;
                case "job.executeGroovy":
                    isGroovyFlag = true;
                    break;
            }

            //prepareExecutionInput
            ExecutionInput executionInput = prepareExecutionInput(eachFlowJson);

            //prepareExecutionCode
            String code = null;
            if (isGroovyFlag) {
                code = eachFlowJson.getString("code");
            }
            //prepareExecutionQuery
            String query = null;
            if (isDbQueryFlag) {
                query = eachFlowJson.getString("Query");
            }

            //prepareExecutionSteps
            ExecutionSteps executionSteps = null;
            if (isConditionalFlag) {
                executionSteps = prepareExecutionStep(eachFlowJson);
            } else if (isIterativeFlag) {
                executionSteps = prepareExecutionStepForIterative(eachFlowJson);
            }

            //prepareExecutionOutput
            String output = eachFlowJson.getString("outputs");

            //prepareExecutionAction

            ExecutionAction executionAction = prepareExecutionAction(eachFlowJson, isConditionalFlag, isIterativeFlag);

            execution.setAlias(eachFlowJson.getString("alias"));
            execution.setId(eachFlowJson.getString("id"));
            execution.setType(eachFlowJson.getString("type"));
            execution.setExecutionInput(executionInput);
            if (code != null) {
                execution.setExecutionCode(code);
            }
            if (query != null) {
                execution.setQuery(query);
            }
            if (executionSteps != null) {
                execution.setExecutionSteps(executionSteps);
            }
            execution.setExecutionOutput(output);
            if (executionAction != null) {
                execution.setExecutionAction(executionAction);
            }
            executionsList.add(execution);
        }
        flow.setListOfExecution(executionsList);
        return flow;
    }

    private ExecutionSteps prepareExecutionStepForIterative(JSONObject eachFlowJson) {
        if (!eachFlowJson.has("steps")) {
            throw new HwfException("steps are mandatory for Execution Type :Iterative.");
        }
        JSONObject stepsJson = eachFlowJson.getJSONObject("steps");
        ExecutionSteps executionStep = ApplicationContextAccessor.getBean(ExecutionSteps.class);
        StepExpression stepExpression = ApplicationContextAccessor.getBean(StepExpression.class);
        stepExpression.setExpressionType(stepsJson.getString("type"));
        stepExpression.setInitialization(stepsJson.getString("initialization"));
        stepExpression.setCondition(stepsJson.getString("condition"));
        stepExpression.setIteration(stepsJson.getString("iteration"));
        executionStep.setStepExpression(stepExpression);
        return executionStep;
    }

    private ExecutionAction prepareExecutionAction(JSONObject eachFlowJson, boolean isConditionalFlag, boolean isIterativeFlag) {
        if (!eachFlowJson.getBoolean("isAction")) {
            return null;
        }
        ExecutionAction executionAction = ApplicationContextAccessor.getBean(ExecutionAction.class);
        JSONObject actionJson = eachFlowJson.getJSONObject("action");

        if (isConditionalFlag) {
            if (actionJson.has("ifTrue"))
                executionAction.setIfTrue(actionJson.getString("ifTrue"));
            if (actionJson.has("ifFalse"))
                executionAction.setIfFalse(actionJson.getString("ifFalse"));
        } else if (isIterativeFlag) {
            if (actionJson.has("next"))
                executionAction.setExecuteNext(actionJson.getString("next"));
            if (actionJson.has("iterate"))
                executionAction.setIterate(actionJson.getString("iterate"));
            if (actionJson.has("exit"))
                executionAction.setExit(actionJson.getString("exit"));
            if (actionJson.has("onException"))
                executionAction.setOnException(actionJson.getString("onException"));
        } else {
            if (actionJson.has("next")) {
                executionAction.setExecuteNext(actionJson.getString("next"));
            }
        }

        return executionAction;
    }

    private ExecutionSteps prepareExecutionStep(JSONObject eachFlowJson) {
        if (!eachFlowJson.has("steps")) {
            throw new HwfException("steps are mandatory for Execution Type :Conditional.");
        }
        JSONObject stepsJson = eachFlowJson.getJSONObject("steps");
        ExecutionSteps executionStep = ApplicationContextAccessor.getBean(ExecutionSteps.class);
        StepExpressionForGroovy stepExpression = ApplicationContextAccessor.getBean(StepExpressionForGroovy.class);
        stepExpression.setExpressionType(stepsJson.getString("type"));
        stepExpression.setExpressionValue(stepsJson.getString("expression"));
        executionStep.setStepExpressionForGroovy(stepExpression);
        return executionStep;
    }

    private ExecutionInput prepareExecutionInput(JSONObject eachFlowJson) {
        ExecutionInput executionInput = ApplicationContextAccessor.getBean(ExecutionInput.class);
        JSONObject inputsJson = eachFlowJson.getJSONObject("inputs");
        List<ReferenceForInput> listOfReferenceInput = prepareRefNames(inputsJson);
        executionInput.setListOfRefs(listOfReferenceInput);
        return executionInput;
    }

    private List<ReferenceForInput> prepareRefNames(JSONObject inputsJson) {
        List<ReferenceForInput> listOfReferenceInput = new ArrayList<>();
        JSONArray inputsArray = inputsJson.getJSONArray("inputs");
        for (int index = 0; index < inputsArray.size(); index++) {
            ReferenceForInput referenceForInput = ApplicationContextAccessor.getBean(ReferenceForInput.class);
            JSONObject eachInputJson = inputsArray.getJSONObject(index);
            if (eachInputJson.has("forName")) {
                referenceForInput.setForName(eachInputJson.getString("forName"));
                if (eachInputJson.has("required"))
                    referenceForInput.setRequired(eachInputJson.getString("required"));
            }
            referenceForInput.setRefName(eachInputJson.getString("name"));
            listOfReferenceInput.add(referenceForInput);
        }
        return listOfReferenceInput;
    }

    private Inputs prepareInputSection(JSONArray inputsArray) {
        Inputs inputs = ApplicationContextAccessor.getBean(Inputs.class);
        DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
        Document document = documentBuilder.newDocument();
        List<Element> listOfElements = new ArrayList<>();
        for (int index = 0; index < inputsArray.size(); index++) {
            JSONObject eachInputJson = inputsArray.getJSONObject(index);
            Element element = document.createElement(eachInputJson.getString("name"));

            if (eachInputJson.has("value"))
                element.appendChild(document.createTextNode(eachInputJson.getString("value")));
            else
                element.appendChild(document.createTextNode(eachInputJson.getString("name")));

            element.setAttribute("default", eachInputJson.getString("defaultValue"));
            element.setAttribute("type", eachInputJson.getString("type"));


            listOfElements.add(element);
        }
        inputs.setInputValues(listOfElements);
        return inputs;
    }

    public HWFD prepareHWFDObject(JSONObject frontEndData, String dir, String hwfFileName) {
        HWFD hwfd = ApplicationContextAccessor.getBean(HWFD.class);
        hwfd.setState(frontEndData.optString("state"));
        hwfd.setDiagramData(frontEndData.optString("diagramData"));
        hwfd.setHwfFile(hwfFileName);
        hwfd.setHwfFileDir(dir);
        return hwfd;
    }

    public void saveHWFDFile(String hwfdExtension, HWFD hwfd, String uuid, String dir) {
        File hwfdFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + hwfdExtension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(hwfd, hwfdFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The hwfd couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
