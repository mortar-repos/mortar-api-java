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

import java.util.HashMap;

/**
 * TODO doc.
 */
public class JobRequest {

    private String projectName;
    private String scriptName;
    private String gitRef;
    private int clusterSize;
    private boolean notifyOnJobFinish = true;
    private Jobs.ClusterType clusterType = Jobs.ClusterType.PERSISTENT;
    private HashMap parameters = new HashMap();
    private boolean isControlScript = false;
    private String clusterId;


    /**
     * TODO doc.
     *
     * @param projectName
     * @param scriptName
     * @param gitRef
     * @param clusterSize
     * @return
     */
    public JobRequest(String projectName, String scriptName, String gitRef, int clusterSize) {
        this.projectName = projectName;
        this.scriptName = scriptName;
        this.gitRef = gitRef;
        this.clusterSize = clusterSize;
    }

    /**
     * TODO doc.
     *
     * @param projectName
     * @param scriptName
     * @param gitRef
     * @param clusterId
     * @return
     */
    public JobRequest(String projectName, String scriptName, String gitRef, String clusterId) {
        this.projectName = projectName;
        this.scriptName = scriptName;
        this.gitRef = gitRef;
        this.clusterId = clusterId;
    }


    /**
     * TODO doc.
     */
    public HashMap<String, Object> getArguments() {
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("project_name", projectName);
        arguments.put("git_ref", gitRef);
        if (clusterId != null) {
            arguments.put("cluster_id", clusterId);
        } else {
            arguments.put("cluster_type", clusterType.toString());
            arguments.put("cluster_size", clusterSize);
        }
        arguments.put("parameters", parameters);
        arguments.put("notify_on_job_finish", notifyOnJobFinish);
        if (isControlScript) {
            arguments.put("controlscript_name", scriptName);
        } else {
            arguments.put("pigscript_name", scriptName);
        }
        return arguments;
    }

    public boolean isNotifyOnJobFinish() {
        return notifyOnJobFinish;
    }

    public void setNotifyOnJobFinish(boolean notifyOnJobFinish) {
        this.notifyOnJobFinish = notifyOnJobFinish;
    }

    public Jobs.ClusterType getClusterType() {
        return clusterType;
    }

    public void setClusterType(Jobs.ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    public HashMap getParameters() {
        return parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public boolean isControlScript() {
        return isControlScript;
    }

    public void setControlScript(boolean controlScript) {
        isControlScript = controlScript;
    }

    public String getClusterId() {
        return clusterId;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public String getGitRef() {
        return gitRef;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getProjectName() {
        return projectName;
    }
}
