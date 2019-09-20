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

package com.helicalinsight.hwf.validator;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.hwf.exception.HwfException;
import com.helicalinsight.hwf.exception.InvalidHwfFileException;
import com.helicalinsight.hwf.exception.InvalidInputException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author Somen
 *         Created  on 5/13/2016.
 */
public class WorkFlowValidator {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowValidator.class);

    /**
     * @param hwfFileName HWF file name
     * @param dir         Directory of the HWF file
     */

    public static String validateUserInput(String hwfFileName, String dir, HttpServletRequest request) {

        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        String fileExtension = FilenameUtils.getExtension(hwfFileName);

        if (dir == null) {
            throw new InvalidInputException("Directory name is null or empty");
        } else if (hwfFileName == null) {
            throw new InvalidInputException("File  name is null or empty");
        } else if (!fileExtension.equalsIgnoreCase("hwf")) {
            throw new InvalidInputException("File extension is not valid");
        } else if (request == null) {
            throw new InvalidInputException("The request is null");
        }
        return (solutionDirectory + File.separator + dir + File.separator + hwfFileName);
    }

    public static void validateHwfJson(JSONObject hwfFileAsJson) {
        if (hwfFileAsJson == null) {
            throw new InvalidHwfFileException("HWF file is not properly formatted");
        }
        if (!hwfFileAsJson.has("flow") || !hwfFileAsJson.has("output") || !hwfFileAsJson.has("input")) {
            throw new InvalidHwfFileException("HwfFile may not have flow or output or input tags");
        }
    }

    public static boolean isArrayComponentInput(JSONObject processJsonObject) {
        boolean isArrayProcessInput;
        if (!processJsonObject.containsKey("input")) {
            throw new HwfException("Execution id:  does not contains input  " + "tag");
        }

        Object processInput = processJsonObject.get("input");
        isArrayProcessInput = processInput instanceof JSONArray;
        boolean isProcessInputJsonObject = processInput instanceof JSONObject;

        if (!isArrayProcessInput && !isProcessInputJsonObject) {
            throw new HwfException("Execution id: does not contains value for input tag");
        }
        logger.info("Process Array is " + isArrayProcessInput);
        return isArrayProcessInput;
    }
}
