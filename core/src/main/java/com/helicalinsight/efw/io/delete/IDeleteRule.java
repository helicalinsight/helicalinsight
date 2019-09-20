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

import com.helicalinsight.efw.resourceloader.rules.IRule;

import java.io.File;

/**
 * Used for file delete operations based on various implementations
 * <p/>
 * Created by author on 12-Oct-14.
 *
 * @author Rajasekhar
 */
public interface IDeleteRule extends IRule {

    /**
     * The file is deletable only when it has matching user credentials
     *
     * @param file The file under concern
     * @return true if deletable
     */
    boolean isDeletable(File file);

    /**
     * Simply deletes the file
     *
     * @param file The file under concern
     */
    void delete(File file);

}
