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
import java.util.Map;

/**
 * Run and fetch illustrate requests from the Mortar API.
 *
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class Illustrates {

    private API api;

    /**
     * Construct an Illustrates V2 API.
     *
     * @param api API client
     */
    public Illustrates(API api) {
        this.api = api;
    }

    /**
     * Run a Pig ILLUSTRATE operation.
     *
     * @param alias Pig alias to illustrate (optional: if not provided, illustrate entire script)
     * @param gitRef version of code (git hash) to use
     * @param projectName Mortar project to use
     * @param pigScriptName Pigscript to use (without path or extension)
     * @return illustrate_id ID of the illustrate that was requested
     * @throws java.io.IOException if unable to run illustrate on API
     */
    public String postIllustrate(String alias, String gitRef, String projectName,
                                 String pigScriptName)
            throws IOException {
        HashMap<String, String> arguments = new HashMap<String, String>();
        if (alias != null) {
            arguments.put("alias", alias);
        }
        arguments.put("git_ref", gitRef);
        arguments.put("project_name", projectName);
        arguments.put("pigscript_name", pigScriptName);

        HttpRequest request = this.api.buildHttpPostRequest("illustrates", arguments);
        return (String) request.execute().parseAs(HashMap.class).get("illustrate_id");
    }

    /**
     * Get the results of a Pig ILLUSTRATE operation.
     *
     * @param illustrateId ID of the illustrate
     * @return requested IllustrateResult
     * @throws IOException if illustrate does not exist or unable to fetch from the API
     */
    public IllustrateResult getIllustrate(String illustrateId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("illustrates/" + illustrateId);
        return request.execute().parseAs(IllustrateResult.class);
    }

    /**
     * Get the results of a Pig ILLUSTRATE operation.
     *
     * @param illustrateId ID of the illustrate
     * @param excludeResult whether to exclude the result field (default: false)
     * @return requested IllustrateResult
     * @throws IOException if illustrate does not exist or unable to fetch from the API
     */
    public IllustrateResult getIllustrate(String illustrateId, boolean excludeResult)
            throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("illustrates/" + illustrateId
                + "?exclude_result=" + excludeResult);
        return request.execute().parseAs(IllustrateResult.class);
    }

    /**
     * Result of a Pig ILLUSTRATE
     */
    public static class IllustrateResult {

        /**
         * Name of the Mortar project for the illustrate.
         */
        @Key("project_name")
        public String projectName;

        /**
         * Pig alias illustrated, or null if entire script illustrated.
         */
        @Key("alias")
        public String alias;

        /**
         * Git hash or branch at which illustrate was run.
         */
        @Key("git_ref")
        public String gitRef;

        /**
         * Name of the script that was illustrated.
         */
        @Key("script_name")
        public String scriptName;

        /**
         * ID of the illustrate.
         */
        @Key("illustrate_id")
        public String describeId;

        /**
         * Illustrate status code.
         */
        @Key("status_code")
        public TaskStatus statusCode;

        /**
         * Full description of illustrate status.
         */
        @Key("status_description")
        public String statusDescription;

        /**
         * URL to view illustrate results
         */
        @Key("web_result_url")
        public String webResultUrl;

        /**
         * Illustrate results.
         */
        @Key("result")
        public Map<String, Object> result;

        @Override
        public String toString() {
            return "IllustrateResult [" +
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
