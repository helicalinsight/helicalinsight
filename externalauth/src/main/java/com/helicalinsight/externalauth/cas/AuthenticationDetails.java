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

package com.helicalinsight.externalauth.cas;

import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serializable;


public class AuthenticationDetails implements Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;


    private final String context;


    public AuthenticationDetails(Object context) {
        this.context = context == null ? "" : context.toString();
        doPopulateAdditionalInformation(context);
    }


    @SuppressWarnings("UnusedParameters")
    protected void doPopulateAdditionalInformation(Object context) {
    }

    public boolean equals(Object obj) {
        if (obj instanceof AuthenticationDetails) {
            AuthenticationDetails rhs = (AuthenticationDetails) obj;
            // this.context cannot be null
            return context.equals(rhs.getContext());
        }

        return false;
    }

    
    public String getContext() {
        return context;
    }

    public String toString() {
        return (super.toString() + ": ") + "Context: " + this.getContext();
    }
}
