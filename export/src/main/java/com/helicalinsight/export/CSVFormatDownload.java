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

package com.helicalinsight.export;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@Scope("prototype")
public class CSVFormatDownload implements IDownload {

    public StringBuilder downloadFormat(JSONArray jsonData, JSONObject conversionOptions) {
        String booleanValue = (String) conversionOptions.opt("blockQuote");
        Boolean appendQuote = !"false".equals(booleanValue);
        StringBuilder firstRow = headerRowAsCsv(jsonData, appendQuote);
        StringBuilder otherData = new StringBuilder();
        for (int i = 0; i < jsonData.size(); i++) {
            otherData.append(rowAsCsv(jsonData.getJSONObject(i), appendQuote)).append("\n");
        }
        firstRow.append("\n").append(otherData);
        return firstRow;
    }

    public static StringBuilder headerRowAsCsv(JSONArray jsonData, Boolean appendQuote) {
        StringBuilder headerRow = new StringBuilder();
        long length = jsonData.size();

        if (length > 0) {
            Object firstObject = jsonData.get(0);
            if (firstObject instanceof JSONObject) {

                JSONObject firstData = (JSONObject) firstObject;
                Iterator<?> keys = firstData.keys();

                while (keys.hasNext()) {
                    if (appendQuote) {
                        headerRow.append(appendQuotes((String) keys.next())).append(",");
                    } else {
                        headerRow.append(keys.next()).append(",");
                    }
                }
            }
        }
        if (headerRow.length() > 0) {
            headerRow.setLength(headerRow.length() - 1);
        }

        return headerRow;
    }

    private StringBuilder rowAsCsv(JSONObject jObject, Boolean appendQuote) {

        StringBuilder rowData = new StringBuilder();
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object retrievedObject = jObject.get(key);
            if (appendQuote) {
                rowData.append(appendQuotes("" + retrievedObject)).append(",");
            } else {
                rowData.append(retrievedObject).append(",");
            }
        }

        if (rowData.length() > 0) {
            rowData.setLength(rowData.length() - 1);
        }
        return rowData;
    }

    public static String appendQuotes(String data) {
        return "\"" + data + "\"";
    }
}







