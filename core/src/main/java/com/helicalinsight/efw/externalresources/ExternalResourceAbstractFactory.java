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

package com.helicalinsight.efw.externalresources;

/**
 * this is abstract class which is used to implements the abstract design
 * factory design pattern for reading the different files such as images, jss,
 * css.
 *
 * @author Muqtar Ahmed
 */

public abstract class ExternalResourceAbstractFactory {

    /**
     * this is abstract method which return the interface type of
     * IExternalResource
     *
     * @return IExternalResource interface
     */

    public abstract IExternalResource getExternalResource();

}
