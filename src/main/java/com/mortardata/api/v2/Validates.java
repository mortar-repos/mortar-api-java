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
 * TODO doc.
 */
public class Validates {

    private API api;

    /**
     * TODO doc.
     * @param api
     */
    public Validates(API api) {
        this.api = api;
    }

    /**
     * TODO doc.
     *
     * @param gitRef
     * @param projectName
     * @param pigScriptName
     * @return describe_id
     * @throws java.io.IOException
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
     * TODO doc.
     *
     * @param validateId
     * @return
     * @throws IOException
     */
    public ValidateResult getValidate(String validateId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("validates/" + validateId);
        return request.execute().parseAs(ValidateResult.class);
    }

    /**
     * TODO doc.
     */
    public static class ValidateResult {
        @Key("project_name")
        public String projectName;

        @Key("git_ref")
        public String gitRef;

        @Key("script_name")
        public String scriptName;

        @Key("validate_id")
        public String validateId;

        @Key("status_code")
        public TaskStatus statusCode;

        @Key("status_description")
        public String statusDescription;
    }
}
