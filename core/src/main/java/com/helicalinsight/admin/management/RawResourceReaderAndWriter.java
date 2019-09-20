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

package com.helicalinsight.admin.management;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Somen
 * Created  on 10/6/2015.
 */
@SuppressWarnings("unused")
public class RawResourceReaderAndWriter implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String action = formJson.getString("action");
        String file = formJson.getString("file");
        String dir = formJson.optString("dir");
        String path;
        if (dir != null && !dir.isEmpty()) {
            path = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator + dir + File.separator
                    + file;
        } else {
            path = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File
                    .separator + file;
        }

        switch (action) {
            case "read":
                return readResource(path);
            case "write":
                String content = formJson.getString("content");
                return writeFile(content, path);

            default:
                throw new EfwServiceException("This action is not found");
        }
    }

    public String readResource(String path) {
        try {
            JSONObject content = new JSONObject();

            File file = new File(path);
            if (file.exists()) {
                content.put("content", FileUtils.readFileToString(file, Charset.defaultCharset()));
                return content.toString();
            } else {
                throw new ResourceNotFoundException("The given file does not exists");
            }
        } catch (IOException ex) {
            throw new OperationFailedException("There was some problem " + ex.getMessage());
        }
    }

    public String writeFile(String content, String path) {
        File file = new File(path);
        JSONObject response = new JSONObject();
        try {
            FileUtils.write(file, content, ControllerUtils.defaultCharSet());
            response.put("message", "File Saved Successfully");
            return response.toString();
        } catch (IOException ex) {
            throw new OperationFailedException("File could not be saved. " + ex.getMessage());
        }
    }
}
