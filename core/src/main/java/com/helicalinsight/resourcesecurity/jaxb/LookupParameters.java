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

package com.helicalinsight.resourcesecurity.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

@SuppressWarnings("unused")
@Component
@Scope("prototype")
@XmlRootElement(name = "lookupParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class LookupParameters {

    @XmlAttribute
    private String type;

    @XmlElement
    private String directory;

    @XmlElement
    private String file;

    @XmlElement
    private Parameter parameter;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "LookupParameters{" +
                "type='" + type + '\'' +
                ", directory='" + directory + '\'' +
                ", file='" + file + '\'' +
                ", parameter=" + parameter +
                '}';
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "parameter")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Parameter {

        @XmlAttribute
        private String dataType;

        @XmlValue
        private String parameter;

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "dataType='" + dataType + '\'' +
                    ", parameter='" + parameter + '\'' +
                    '}';
        }
    }
}