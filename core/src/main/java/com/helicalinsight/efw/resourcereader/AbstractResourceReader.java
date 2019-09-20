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

package com.helicalinsight.efw.resourcereader;

import net.sf.json.JSONObject;

/**
 * this is abstract class for implementing the abstract factory design pattern
 * for resources like json, xml
 *
 * @author Muqtar Ahmed
 */

public abstract class AbstractResourceReader {

    /**
     * abstract method which return the IResource interface type
     *
     * @param resource          resource type
     * @param fileType          file type
     * @param objectClass       class type
     * @param path              root path
     * @param visibleExtensions jsonobject
     * @return IResource interface
     */

    public abstract IResourceReader getIResource(String resource, String fileType, String objectClass, String path,
                                                 JSONObject visibleExtensions);
}
