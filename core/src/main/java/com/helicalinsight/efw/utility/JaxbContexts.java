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

package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.EfwServiceException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by author on 03-08-2015.
 *
 * @author Rajasekhar
 */
public enum JaxbContexts {

    JAXB_CONTEXTS;

    private static final ConcurrentHashMap<String, JAXBContext> CONTEXTS_MAP = new ConcurrentHashMap<>(16, 0.9f, 1);

    public static JaxbContexts getJaxbContexts() {
        return JAXB_CONTEXTS;
    }

    public <T> JAXBContext getContextForClass(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("The type parameter can't be null.");
        }
        String typeName = type.getName();
        JAXBContext jaxbContext = CONTEXTS_MAP.get(typeName);
        if (jaxbContext != null) {
            return jaxbContext;
        } else {
            try {
                jaxbContext = JAXBContext.newInstance(type);
                if (jaxbContext == null) {
                    throw new EfwServiceException("Fatal error. Couldn't obtain the jaxb context.");
                }
                synchronized (this) {
                    CONTEXTS_MAP.put(typeName, jaxbContext);
                }
                return jaxbContext;
            } catch (JAXBException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
