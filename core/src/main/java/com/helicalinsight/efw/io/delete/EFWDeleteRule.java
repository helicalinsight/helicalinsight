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

package com.helicalinsight.efw.io.delete;

import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * The efw files are not supposed to be deleted. So this class though configured
 * in the setting.xml does not allow the user to delete the efw files.
 * <p/>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 */
public final class EFWDeleteRule implements IDeleteRule {

    /**
     * Private for the purpose of singleton pattern
     */
    private EFWDeleteRule() {
    }

    /**
     * Singleton getter
     *
     * @return An instance of the same class
     */

    public static IDeleteRule getInstance() {
        return EfwDeleteRuleHolder.INSTANCE;
    }

    /**
     * EFW files can't be deleted
     *
     * @param file The file under concern
     * @return Always true
     */
    public boolean isDeletable(File file) {
        return true;
    }

    /**
     * Deletes the template of the efw and the efw itself.
     *
     * @param file The file under concern
     */
    public void delete(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        final JSONObject jsonObject = ResourceProcessorFactory.getIProcessor().getJSONObject(file.getAbsolutePath(), false);
        if (!jsonObject.has("template")) {
            IOOperationsUtility.deleteWithLogs(file);
        } else {
            final String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(file.getAbsolutePath());
            if (fullPathNoEndSeparator != null) {
                File template = new File(fullPathNoEndSeparator + File.separator + jsonObject.getString("template"));
                IOOperationsUtility.deleteWithLogs(template);
                IOOperationsUtility.deleteWithLogs(file);
            }
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new EFWDeleteRule();
    }
}
