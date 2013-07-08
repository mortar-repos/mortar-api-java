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
import java.util.List;

/**
 * Fetch and update Web Projects from the Mortar API.
 */
public class WebProjects {

    private API api;


    /**
     * Construct a WebProjects V2 API.
     *
     * @param api API client
     */
    public WebProjects(API api) {
        this.api = api;
    }

    /**
     * Get all Web Projects from the API.  Web Projects retrieved this way will not have their
     * pigContents, jythonContents, or pythonContents fields populated.
     *
     * @return list of all Web Projects
     * @throws IOException if unable to fetch data from the API
     */
    public WebProjectList getWebProjects() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("webprojects");
        return request.execute().parseAs(WebProjectList.class);
    }

    /**
     * Get a specific Web Project from the API.
     *
     * @param projectName name of the Web Project
     * @return requested Web Project
     * @throws IOException if Web Project does not exist or unable to fetch Web Project from the API
     */
    public WebProject getWebProject(String projectName) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("webprojects/" + projectName);
        return request.execute().parseAs(WebProject.class);
    }

    /**
     * Update an existing web project, or create it if the name does not already exist.
     *
     * @param project Web Project to be created or updated
     * @throws IOException if unable to create or update Web Project
     */
    public void createOrUpdateWebProject(WebProject project) throws IOException {
        HttpRequest request = this.api.buildHttpPutRequest("webprojects/" + project.getName(),
                project);
        request.execute();
    }

    /**
     * List of WebProject objects.
     */
    public static class WebProjectList {

        /**
         * List of WebProject objects.
         */
        @Key("scripts")
        public List<WebProject> webProjects;

        @Override
        public String toString() {
            return "WebProjectList [" +
                    "webProjects=" + webProjects +
                    ']';
        }
    }

    /**
     * A Web Project.
     */
    public static class WebProject {

        @Key
        private String name;

        @Key("script_key")
        private String scriptKey;

        @Key("create_timestamp")
        private String createTimestamp;

        @Key("update_timestamp")
        private String updateTimestamp;

        @Key("pig_contents")
        private String pigContents;

        @Key("python_contents")
        private String pythonContents;

        @Key("jython_contents")
        private String jythonContents;


        public WebProject() {
            //no args constructor for serialization
        }

        public WebProject(String name, String pigContents) {
            this.name = name;
            this.pigContents = pigContents;
        }

        /**
         * Web Project name.
         */
        public String getName() {
            return name;
        }

        /**
         * Identifier key of the Web Project.
         */
        public String getScriptKey() {
            return scriptKey;
        }

        /**
         * Timestamp when WebProject was created.
         */
        public String getCreateTimestamp() {
            return createTimestamp;
        }

        /**
         * Timestamp when WebProject was most recently updated.
         */
        public String getUpdateTimestamp() {
            return updateTimestamp;
        }

        /**
         * Pigscript of this project.
         */
        public String getPigContents() {
            return pigContents;
        }

        /**
         * Python code for this project, if any.
         */
        public String getPythonContents() {
            return pythonContents;
        }

        /**
         * Set Python code for this project.
         */
        public void setPythonContents(String pythonContents) {
            this.pythonContents = pythonContents;
        }

        /**
         * Jython code for this project, if any.
         */
        public String getJythonContents() {
            return jythonContents;
        }

        /**
         * Set Jython code for this project.
         */
        public void setJythonContents(String jythonContents) {
            this.jythonContents = jythonContents;
        }

        @Override
        public String toString() {
            return "WebProject [" +
                    "name='" + name + '\'' +
                    ", scriptKey='" + scriptKey + '\'' +
                    ", createTimestamp='" + createTimestamp + '\'' +
                    ", updateTimestamp='" + updateTimestamp + '\'' +
                    ", pigContents='" + pigContents + '\'' +
                    ", pythonContents='" + pythonContents + '\'' +
                    ", jythonContents='" + jythonContents + '\'' +
                    ']';
        }
    }
}
