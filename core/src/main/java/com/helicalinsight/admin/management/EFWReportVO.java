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

package com.helicalinsight.admin.management;

public class EFWReportVO implements Comparable<EFWReportVO> {
    private String reportPath;
    private long lastModified;
    private String title;
    private String dir;
    private String file;
    private String logicalPath;

    @Override
    public String toString() {
        return "EFWReportVO{" +
                "reportPath='" + reportPath + '\'' +
                ", lastModified=" + lastModified +
                ", title='" + title + '\'' +
                ", dir='" + dir + '\'' +
                ", file='" + file + '\'' +
                ", logicalPath='" + logicalPath + '\'' +
                '}';
    }

    public EFWReportVO(String reportPath, long lastModified, String title, String dir, String file, String logicalPath) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
        this.title = title;
        this.dir = dir;
        this.file = file;
        this.logicalPath = logicalPath;
    }

    public String getLogicalPath() {
        return logicalPath;
    }

    public void setLogicalPath(String logicalPath) {
        this.logicalPath = logicalPath;
    }

    public EFWReportVO(String reportPath, long lastModified, String title, String dir, String file) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
        this.title = title;
        this.dir = dir;
        this.file = file;
    }

    public EFWReportVO(String reportPath, long lastModified, String title) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
        this.title = title;
    }

    public EFWReportVO(String reportPath, long lastModified) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int compareTo(EFWReportVO efwReportVO) {
        long compareQuantity = efwReportVO.getLastModified();

        //ascending order
        // return this.lastModified - compareQuantity;

        //descending order
        if(compareQuantity==this.lastModified){
            return 0;
        }else if((compareQuantity -this.lastModified)>0){
            return 1;
        }else if((compareQuantity -this.lastModified)<0){
            return -1;
        };
        return 0;
    }
}