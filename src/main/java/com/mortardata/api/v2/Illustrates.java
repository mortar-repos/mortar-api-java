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
 * TODO doc.
 */
public class Illustrates {

    private API api;

    /**
     * TODO doc.
     * @param api
     */
    public Illustrates(API api) {
        this.api = api;
    }


    /**
     * TODO doc.
     *
     * @param alias
     * @param gitRef
     * @param projectName
     * @param pigScriptName
     * @return describe_id
     * @throws java.io.IOException
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
     * TODO doc.
     *
     * @param illustrateId
     * @return
     * @throws IOException
     */
    public IllustrateResult getIllustrate(String illustrateId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("illustrates/" + illustrateId);
        return request.execute().parseAs(IllustrateResult.class);
    }

    /**
     * TODO doc.
     *
     * @param illustrateId
     * @param excludeResult
     * @return
     * @throws IOException
     */
    public IllustrateResult getIllustrate(String illustrateId, boolean excludeResult)
            throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("illustrates/" + illustrateId
                + "?exclude_result=" + excludeResult);
        return request.execute().parseAs(IllustrateResult.class);
    }

    /**
     * TODO doc.
     */
    public static class IllustrateResult {
        @Key("project_name")
        public String projectName;

        @Key("alias")
        public String alias;

        @Key("git_ref")
        public String gitRef;

        @Key("script_name")
        public String scriptName;

        @Key("illustrate_id")
        public String describeId;

        @Key("status_code")
        public String statusCode;

        @Key("status_description")
        public String statusDescription;

        @Key("web_result_url")
        public String webResultUrl;

        @Key("result")
        public Map<String, Object> result;
    }



}
