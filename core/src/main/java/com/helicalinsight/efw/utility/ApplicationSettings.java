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

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by author on 02-09-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("request")
@SuppressWarnings("unused")
public class ApplicationSettings {
    private String enableReportSave;
    private String rootDirectoryPermission;
    private String defaultEmailResourceType;
    private boolean provideExportViaHtml;

    public ApplicationSettings() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        this.enableReportSave = applicationProperties.getEnableSavedResult();
        this.defaultEmailResourceType = applicationProperties.getDefaultEmailResourceType();
        this.provideExportViaHtml = applicationProperties.isProvideExportViaHtml();
        this.rootDirectoryPermission = String.valueOf(ApplicationContextAccessor.getBean
                (ResourcePermissionLevelsHolder.class).readWriteDeleteAccessLevel());
    }

    public String getEnableReportSave() {
        return enableReportSave;
    }

    public void setEnableReportSave(String enableReportSave) {
        this.enableReportSave = enableReportSave;
    }

    public String getRootDirectoryPermission() {
        return rootDirectoryPermission;
    }

    public void setRootDirectoryPermission(String rootDirectoryPermission) {
        this.rootDirectoryPermission = rootDirectoryPermission;
    }

    public boolean isProvideExportViaHtml() {
        return provideExportViaHtml;
    }

    public void setProvideExportViaHtml(boolean provideExportViaHtml) {
        this.provideExportViaHtml = provideExportViaHtml;
    }

    public String getDefaultEmailResourceType() {
        return defaultEmailResourceType;
    }

    public void setDefaultEmailResourceType(String defaultEmailResourceType) {
        this.defaultEmailResourceType = defaultEmailResourceType;
    }
}
