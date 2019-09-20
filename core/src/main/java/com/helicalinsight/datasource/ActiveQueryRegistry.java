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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("unused")
public enum ActiveQueryRegistry {
    QUERY_REGISTRY_INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ActiveQueryRegistry.class);
    private final Map<String, RunningQueryDetails> activeQueryDetailsMap = new ConcurrentHashMap<>();
    private final Map<String, String> queryIdThreadNameMap = new ConcurrentHashMap<>();
    private final Set<String> requestedForCancelSet = new HashSet<>();

    public static ActiveQueryRegistry getRegistry() {
        return QUERY_REGISTRY_INSTANCE;
    }

    public void registerStatements(String threadId, RunningQueryDetails runningQueryDetails) {
        String[] split = threadId.split("##");
        if (split.length < 3) {
            return;
        }
        String queryId = split[2];
        if (requestedForCancelSet.contains(queryId)) {
            logger.info("Statement not registered, request to cancel query with queryId " + queryId + " was found");
            runningQueryDetails.cancelQuery();
            requestedForCancelSet.remove(queryId);
            return;
        }
        logger.info("The statement is now registered for threadName" + threadId);
        if (activeQueryDetailsMap.containsKey(threadId)) {
            throw new DuplicateEntryException("The uuid is already registered");
        }
        this.activeQueryDetailsMap.put(threadId, runningQueryDetails);
    }

    public void registerQueryIdThreadName(String queryId, String threadName) {
        this.queryIdThreadNameMap.put(queryId, threadName);
    }

    public String findThreadNameForQueryId(String queryId) {
        return this.queryIdThreadNameMap.get(queryId);
    }

    public void deRegisterStatement(String threadId) {
        logger.info("The statement is deRegistered at " + System.currentTimeMillis());
        this.activeQueryDetailsMap.remove(threadId);
    }


    public JSONObject cancelQuery(String queryId) {
        String threadName = this.queryIdThreadNameMap.get(queryId);
        logger.info("ThreadName to cancel is " + threadName);
        RunningQueryDetails runningQueryDetails = null;
        if (threadName != null) {
            runningQueryDetails = this.activeQueryDetailsMap.get(threadName);
        }
        JSONObject response = new JSONObject();
        if (runningQueryDetails == null) {
            requestedForCancelSet.add(queryId);
            logger.info("Could not cancel the thread, Queued in the list ");
            response.put("message", "Could not cancel query, queued in for cancellation");
            return response;
        }
        response.put("message", "Query cancelled successfully");
        runningQueryDetails.cancelQuery();
        return response;
    }

    public JSONObject cancelQueryByThreadName(String threadName) {
        RunningQueryDetails runningQueryDetails = this.activeQueryDetailsMap.get(threadName);
        JSONObject response = new JSONObject();
        if (runningQueryDetails == null) {
            response.put("message", "Could not be cancel query, the threadName " + threadName + "does not exists");
            return response;
        }
        runningQueryDetails.cancelQuery();
        response.put("message", "Query cancelled successfully");
        runningQueryDetails.cancelQuery();
        return response;

    }


    public JSONArray listDetails() {
        JSONArray data = new JSONArray();
        for (String key : activeQueryDetailsMap.keySet()) {
            RunningQueryDetails runningQueryDetails = activeQueryDetailsMap.get(key);
            data.add(runningQueryDetails.getDatabaseDetails());
        }
        if (!requestedForCancelSet.isEmpty()) {
            JSONObject queuedSet = new JSONObject();
            queuedSet.put("queue", requestedForCancelSet);
            data.add(queuedSet);
        }
        return data;
    }
}
