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

/**
 * Run and fetch validate requests from the Mortar API.
 *
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class Validates {

    private API api;

    /**
     * Construct a Validates V2 API.
     * @param api
     */
    public Validates(API api) {
        this.api = api;
    }

    /**
     * Run a Pig VALIDATE operation.
     *
     * @param gitRef version of code (git hash) to use
     * @param projectName Mortar project to use
     * @param pigScriptName Pigscript to use (without path or extension)
     * @return describe_id ID of the describe that was requested
     * @throws java.io.IOException if unable to run describe on API
     */
    public String postValidate(String gitRef, String projectName, String pigScriptName)
            throws IOException {
        HashMap<String, String> arguments = new HashMap<String, String>();
        arguments.put("git_ref", gitRef);
        arguments.put("project_name", projectName);
        arguments.put("pigscript_name", pigScriptName);

        HttpRequest request = this.api.buildHttpPostRequest("validates", arguments);
        return (String) request.execute().parseAs(HashMap.class).get("validate_id");
    }

    /**
     * Get the results of a Pig VALIDATE operation.
     *
     * @param validateId ID of the describe.
     * @return requested ValidateResult
     * @throws IOException if describe does not exist or unable to fetch from the API
     */
    public ValidateResult getValidate(String validateId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("validates/" + validateId);
        return request.execute().parseAs(ValidateResult.class);
    }

    /**
     * Result of a Pig VALIDATE
     */
    public static class ValidateResult {

        @Key("project_name")
        private String projectName;

        @Key("git_ref")
        private String gitRef;

        @Key("script_name")
        private String scriptName;

        @Key("validate_id")
        private String validateId;

        @Key("status_code")
        private String statusCode;

        @Key("status_description")
        private String statusDescription;


        /**
         * Name of the Mortar project for the validate.
         */
        public String getProjectName() {
            return projectName;
        }

        /**
         * Git hash or branch at which validate was run.
         */
        public String getGitRef() {
            return gitRef;
        }

        /**
         * Name of the script that was validated.
         */
        public String getScriptName() {
            return scriptName;
        }

        /**
         * ID of the validate
         */
        public String getValidateId() {
            return validateId;
        }

        /**
         * Validate status code.
         */
        public TaskStatus getStatusCode() {
            return TaskStatus.getEnum(statusCode);
        }

        /**
         * Validate status code original string.
         */
        public String getStatusCodeString() {
            return statusCode;
        }

        /**
         * Full description of validate status.
         */
        public String getStatusDescription() {
            return statusDescription;
        }

        @Override
        public String toString() {
            return "ValidateResult [" +
                    "projectName='" + projectName + '\'' +
                    ", gitRef='" + gitRef + '\'' +
                    ", scriptName='" + scriptName + '\'' +
                    ", validateId='" + validateId + '\'' +
                    ", statusCode=" + statusCode +
                    ", statusDescription='" + statusDescription + '\'' +
                    ']';
        }
    }
}
