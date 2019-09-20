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

package com.helicalinsight.datasource;

/**
 * Created by user on 1/29/2016.
 *
 * @author Rajasekhar
 */
public interface DataSourceProviders {

    public static final String HIKARI = "hikari";
    public static final String TOMCAT = "tomcat";
    public static final String JNDI = "jndi";
    public static final String NONE = "none";
    public static final String CALCITE = "calcite";
}
