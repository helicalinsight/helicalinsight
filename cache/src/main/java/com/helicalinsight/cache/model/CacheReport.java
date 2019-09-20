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

package com.helicalinsight.cache.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cache_report")
public class CacheReport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;
    @Column(name = "cache_id")
    private Long cacheId;
    @Column(name = "report_path")
    private String reportPath;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cache_id", nullable = true, insertable = false, updatable = false)
    private com.helicalinsight.cache.model.Cache cache;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public com.helicalinsight.cache.model.Cache getCache() {
        return cache;
    }

    public void setCache(com.helicalinsight.cache.model.Cache cache) {
        this.cache = cache;
    }

    public Long getCacheId() {
        return cacheId;

    }

    public void setCacheId(Long cacheId) {
        this.cacheId = cacheId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }
}