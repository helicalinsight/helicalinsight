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

package com.helicalinsight.efw;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


public class ProductInformation extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("application/json");

        JSONObject jsonObject = new JSONObject();
        ApplicationInformation productInfo = ApplicationInformation.getInstance();
        Map<String, String> details = new HashMap<>();

        details.put("Product Name", productInfo.getProductName());
        details.put("Product Type", productInfo.getProductType());
        details.put("Version", productInfo.getVersion());
        details.put("Build", productInfo.getBuild());

        jsonObject.putAll(details);

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.print(jsonObject);
            printWriter.flush();
        } catch (IOException ignore) {
            throw new RuntimeException("Unable to retrieve product information", ignore);
        } finally {
            ApplicationUtilities.closeResource(printWriter);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        doGet(request, response);
    }
}