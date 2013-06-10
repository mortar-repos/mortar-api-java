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
 * TODO doc.
 */
public class Describes {

    private API api;

    /**
     * TODO doc.
     * @param api
     */
    public Describes(API api) {
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
     * @throws IOException
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
     * TODO doc.
     *
     * @param describeId
     * @return
     * @throws IOException
     */
    public DescribeResult getDescribe(String describeId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("describes/" + describeId);
        return request.execute().parseAs(DescribeResult.class);
    }

    /**
     * TODO doc.
     *
     * @param describeId
     * @param excludeResult
     * @return
     * @throws IOException
     */
    public DescribeResult getDescribe(String describeId, boolean excludeResult) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("describes/" + describeId
                + "?exclude_result=" + excludeResult);
        return request.execute().parseAs(DescribeResult.class);
    }


    /**
     * TODO doc.
     */
    public static class DescribeResult {
        @Key("project_name")
        public String projectName;

        @Key("alias")
        public String alias;

        @Key("git_ref")
        public String gitRef;

        @Key("script_name")
        public String scriptName;

        @Key("describe_id")
        public String describeId;

        @Key("status_code")
        public TaskStatus statusCode;

        @Key("status_description")
        public String statusDescription;

        @Key("web_result_url")
        public String webResultUrl;

        @Key("result")
        public Map<String, List<Table>> result;
    }

    /**
     * TODO doc.
     */
    public static class Table {
        @Key("alias")
        public String alias;

        @Key("fields")
        public List<String> fields;

        @Key("op")
        public String op;
    }
}

