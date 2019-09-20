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

import java.io.File;

/**
 * Created by author on 12-08-2015.
 *
 * @author Rajasekhar
 */
public final class FileDeleteRule implements IDeleteRule {

    /**
     * Private for the purpose of singleton pattern
     */
    private FileDeleteRule() {
    }

    /**
     * Singleton getter
     *
     * @return An instance of the same class
     */

    public static IDeleteRule getInstance() {
        return FileDeleteRuleHolder.INSTANCE;
    }

    /**
     * @param file The file under concern
     * @return Always true
     */
    public boolean isDeletable(File file) {
        return true;
    }

    /**
     * @param file The file under concern
     */
    public void delete(File file) {
        if (file != null && file.exists()) {
            IOOperationsUtility.deleteWithLogs(file);
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
    private static class FileDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new FileDeleteRule();
    }
}
