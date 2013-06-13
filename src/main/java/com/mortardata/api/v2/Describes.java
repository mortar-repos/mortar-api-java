/*
 * Copyright 2013 Mortar Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mortardata.api.v2;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Run and fetch describe requests from the Mortar API.
 *
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class Describes {

    private API api;

    /**
     * Construct a Describes V2 API.
     *
     * @param api API client
     */
    public Describes(API api) {
        this.api = api;
    }

    /**
     * Run a Pig DESCRIBE operation.
     *
     * @param alias Pig alias to describe
     * @param gitRef version of code (git hash) to use
     * @param projectName Mortar project to use
     * @param pigScriptName Pigscript to use (without path or extension)
     * @return describe_id ID of the describe that was requested
     * @throws IOException if unable to run describe on API
     */
    public String postDescribe(String alias, String gitRef, String projectName,
                               String pigScriptName) throws IOException {
        HashMap<String, String> arguments = new HashMap<String, String>();
        arguments.put("alias", alias);
        arguments.put("git_ref", gitRef);
        arguments.put("project_name", projectName);
        arguments.put("pigscript_name", pigScriptName);

        HttpRequest request = this.api.buildHttpPostRequest("describes", arguments);
        return (String) request.execute().parseAs(HashMap.class).get("describe_id");
    }

    /**
     * Get the results of a Pig DESCRIBE operation.
     *
     * @param describeId ID of the describe
     * @return requested DescribeResult
     * @throws IOException if describe does not exist or unable to fetch from the API
     */
    public DescribeResult getDescribe(String describeId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("describes/" + describeId);
        return request.execute().parseAs(DescribeResult.class);
    }

    /**
     * Get the results of a Pig DESCRIBE operation.
     *
     * @param describeId ID of the describe
     * @param excludeResult whether to exclude the result field (default: false)
     * @return requested DescribeResult
     * @throws IOException if describe does not exist or unable to fetch from the API
     */
    public DescribeResult getDescribe(String describeId, boolean excludeResult) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("describes/" + describeId
                + "?exclude_result=" + excludeResult);
        return request.execute().parseAs(DescribeResult.class);
    }


    /**
     * Result of a Pig DESCRIBE
     */
    public static class DescribeResult {

        /**
         * Name of the Mortar project for the describe.
         */
        @Key("project_name")
        public String projectName;

        /**
         * Pig alias described.
         */
        @Key("alias")
        public String alias;

        /**
         * Git hash or branch at which describe was run.
         */
        @Key("git_ref")
        public String gitRef;

        /**
         * Name of the script that was described.
         */
        @Key("script_name")
        public String scriptName;

        /**
         * ID of the describe
         */
        @Key("describe_id")
        public String describeId;

        /**
         * Describe status code.
         */
        @Key("status_code")
        public TaskStatus statusCode;

        /**
         * Full description of describe status.
         */
        @Key("status_description")
        public String statusDescription;

        /**
         * URL to view describe results
         */
        @Key("web_result_url")
        public String webResultUrl;

        /**
         * Describe results
         */
        @Key("result")
        public Map<String, Object> result;

        @Override
        public String toString() {
            return "DescribeResult [" +
                    "projectName='" + projectName + '\'' +
                    ", alias='" + alias + '\'' +
                    ", gitRef='" + gitRef + '\'' +
                    ", scriptName='" + scriptName + '\'' +
                    ", describeId='" + describeId + '\'' +
                    ", statusCode=" + statusCode +
                    ", statusDescription='" + statusDescription + '\'' +
                    ", webResultUrl='" + webResultUrl + '\'' +
                    ", result=" + result +
                    ']';
        }
    }
}

