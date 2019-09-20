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

import com.google.gson.Gson;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
public class JaxbUtils {

    public static <T> void marshal(T pojo, File location) {
        JAXBContext jaxbContext;
        JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
        try {
            jaxbContext = jaxbContexts.getContextForClass(pojo.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            String charsetName = ControllerUtils.defaultCharSet();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);
            CharacterEscapeHandler characterEscapeHandler = new CharacterEscapeHandler() {
                @Override
                public void escape(char[] array, int i, int j, boolean flag,
                                   Writer writer) throws IOException {
                    writer.write(array, i, j);
                }
            };
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), characterEscapeHandler);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(location), charsetName);
            marshaller.marshal(pojo, writer);
            writer.flush();
            writer.close();
        } catch (Exception exception) {
            throw new ConfigurationException("Could not write the xml file in the " +
                    "location " + location, exception);
        }
    }


    public static <T> T jsonStringToObject(Class<T> clazz, String fileAsJson) {
        if (fileAsJson == null) {
            return null;
        }
        return new Gson().fromJson(fileAsJson, clazz);
    }

    public static <T> String getFileAsGson(Class<T> clazz, File resource) {
        if (resource == null) {
            throw new IllegalArgumentException("File can't be null");
        }
        T t = unMarshal(clazz, resource);
        return new Gson().toJson(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> T unMarshal(Class<T> clazz, File location) {
        JAXBContext jaxbContext;
        JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
        try {
            jaxbContext = jaxbContexts.getContextForClass(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            //noinspection unchecked
            return (T) unmarshaller.unmarshal(location);
        } catch (JAXBException ex) {
            throw new ConfigurationException("Could not convert the xml to java object for the location " + location,
                    ex);
        }
    }
}
