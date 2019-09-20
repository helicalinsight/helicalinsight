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

package com.helicalinsight.hwf.controller;

import com.helicalinsight.hwf.core.api.HWFEngine;
import com.helicalinsight.hwf.validator.WorkFlowValidator;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@SuppressWarnings("ALL")
public class HWFController {

    private static final Logger logger = LoggerFactory.getLogger(HWFController.class);

    @RequestMapping(value = "/workflow", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getData(HttpServletRequest request, HttpServletResponse response) {

        String hwfFileName = request.getParameter("fileName");
        String dir = request.getParameter("dir");
        String hwfpath = WorkFlowValidator.validateUserInput(hwfFileName, dir, request);

        HWFEngine hwfEngine = new HWFEngine(hwfpath, request, response);
        JSONObject output = hwfEngine.processHWF();
        String viewPage = output.optString("viewPage");
        if (viewPage == null || viewPage.isEmpty()) {
            viewPage = "workflow";
        }
        output.discard("viewPage");
        return new ModelAndView(viewPage, "json", output);
    }


}
