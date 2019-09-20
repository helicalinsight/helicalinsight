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

package com.helicalinsight.admin.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is a Model class and used to hold the limit and offset data.
 * The Limit and Offset is used to control the pagination of the search
 *
 * @author Somen
 */

public class LimitOffsetModel {


    private Integer offset;

    private Integer limit;

    private String searchPhrase;

    private String searchOn;

    private Integer totalCount;

    public LimitOffsetModel() {
    }

    /**
     * The constructor is used to populate the fields  using the request object.
     * The parameters (offset, limit, searchPhrase, searchOn) is searched from the request object.
     *
     * @param request The request object holds the parameters mentioned above
     */
    public LimitOffsetModel(HttpServletRequest request) {
        String offset = request.getParameter("offset");
        String limit = request.getParameter("limit");
        String searchPhrase = request.getParameter("searchPhrase");
        String searchOn = request.getParameter("searchOn");
        this.setOffset(offset);
        this.setLimit(limit);
        this.setSearchOn(searchOn);
        this.setSearchPhrase(searchPhrase);
    }

    public Integer getTotalCount() {
        if (this.totalCount == null) {
            return 0;
        }
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getOffset() {
        if ((offset == null)) {
            this.offset = 0;
        }
        return offset;
    }

    public void setOffset(String offset) {
        if (StringUtils.isEmpty(offset)) {
            this.offset = 0;
        } else {
            try {
                this.offset = Integer.parseInt(offset);
            } catch (NumberFormatException nfe) {
                this.offset = 0;
            }
        }
    }

    public Integer getLimit() {
        if (limit == null) {
            this.limit = Integer.MAX_VALUE;
        }
        return limit;
    }

    public void setLimit(String limit) {
        if (StringUtils.isEmpty(limit) || (limit.trim().length() < 0)) {
            this.limit = Integer.MAX_VALUE;
        } else {
            try {
                this.limit = Integer.parseInt(limit);
            } catch (NumberFormatException nfe) {
                this.limit = Integer.MAX_VALUE;
            }
        }
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        if (searchPhrase == null) {
            this.searchPhrase = "%%";
        } else {
            this.searchPhrase = "%" + searchPhrase + "%";
        }
    }

    public String getSearchOn() {
        if (searchOn == null) {
            return "";
        } else {
            return searchOn;
        }
    }

    public void setSearchOn(String searchOn) {
        this.searchOn = searchOn;
    }


    @Override
    public String toString() {
        return "LimitOffsetModel{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", searchPhrase='" + searchPhrase + '\'' +
                ", searchOn='" + searchOn + '\'' +
                ", totalCount=" + totalCount +
                '}';
    }
}

