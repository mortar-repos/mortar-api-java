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

import com.google.api.client.util.GenericData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the set of arguments needed to request a job run from the Mortar API
 */
public class JobRequest {

    private String projectName;
    private String scriptName;
    private String gitRef;
    private int clusterSize;
    private boolean notifyOnJobFinish = true;
    private Clusters.ClusterType clusterType = Clusters.ClusterType.PERSISTENT;
    private Map<String, String> parameters = new HashMap<String, String>();
    private boolean isControlScript = false;
    private String clusterId;


    /**
     * Construct a JobRequest object for running a job on a new cluster
     *
     * @param projectName Mortar project name
     * @param scriptName name of script to be run
     * @param gitRef git branch to be run
     * @param clusterSize size of new cluster
     */
    public JobRequest(String projectName, String scriptName, String gitRef, int clusterSize) {
        this.projectName = projectName;
        this.scriptName = scriptName;
        this.gitRef = gitRef;
        this.clusterSize = clusterSize;
    }

    /**
     * Construct a JobRequest object for running a job on an existing cluster
     *
     * @param projectName Mortar project name
     * @param scriptName name of script to be run
     * @param gitRef git branch to be run
     * @param clusterId id of existing cluster
     */
    public JobRequest(String projectName, String scriptName, String gitRef, String clusterId) {
        this.projectName = projectName;
        this.scriptName = scriptName;
        this.gitRef = gitRef;
        this.clusterId = clusterId;
    }


    /**
     * Collect all job arguments into a form compatible with the Mortar API
     *
     * @returns GenericData object populated with all job run arguments
     */
    public GenericData getArguments() {
        GenericData arguments = new GenericData();
        arguments.put("project_name", projectName);
        arguments.put("git_ref", gitRef);
        if (clusterId != null) {
            arguments.put("cluster_id", clusterId);
        } else {
            arguments.put("cluster_type", clusterType.toString());
            arguments.put("cluster_size", clusterSize);
        }
        arguments.put("parameters", convertParameters());
        arguments.put("notify_on_job_finish", notifyOnJobFinish);
        if (isControlScript) {
            arguments.put("controlscript_name", scriptName);
        } else {
            arguments.put("pigscript_name", scriptName);
        }
        return arguments;
    }

    /**
     * For all elements of parameters, put them in a form compatible with the Mortar API
     *
     * @return List of Map objects with "name" as the key for the parameter name, and "value"
     * as the key for the parameter value.
     */
    private List<Map<String, String>> convertParameters() {
        List<Map<String, String>> params = new ArrayList<Map<String, String>>();
        for (String s : parameters.keySet()) {
            HashMap<String, String> valMap = new HashMap<String, String>();
            valMap.put("name", s);
            valMap.put("value", parameters.get(s));
            params.add(valMap);
        }
        return params;
    }

    /**
     * @return whether the user should be notified when the job is complete
     */
    public boolean isNotifyOnJobFinish() {
        return notifyOnJobFinish;
    }

    /**
     * Set whether the user should be notified when the job is complete.  Default is true.
     */
    public void setNotifyOnJobFinish(boolean notifyOnJobFinish) {
        this.notifyOnJobFinish = notifyOnJobFinish;
    }

    /**
     * @return cluster type requested for the job, if job is requesting a new cluster
     */
    public Clusters.ClusterType getClusterType() {
        return clusterType;
    }

    /**
     * Set cluster type if starting a new cluster.  Default is PERSISTENT.
     */
    public void setClusterType(Clusters.ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    /**
     * @return Parameters for running the script
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Set parameters needed for running the script.
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return Whether the named script is a control script
     */
    public boolean isControlScript() {
        return isControlScript;
    }

    /**
     * Set whether the named script is a control script.  Default is false.
     */
    public void setControlScript(boolean controlScript) {
        isControlScript = controlScript;
    }

    /**
     * @return cluster id of cluster for the job to run on, if job is using existing cluster
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     * @return number of nodes for cluster to be created to run this job
     */
    public int getClusterSize() {
        return clusterSize;
    }

    /**
     * @return git branch to be used for code deploy
     */
    public String getGitRef() {
        return gitRef;
    }

    /**
     * @return name of the script to be run
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * @return name of the project being run
     */
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String toString() {
        return "JobRequest [" +
                "projectName='" + projectName + '\'' +
                ", scriptName='" + scriptName + '\'' +
                ", gitRef='" + gitRef + '\'' +
                ", clusterSize=" + clusterSize +
                ", notifyOnJobFinish=" + notifyOnJobFinish +
                ", clusterType=" + clusterType +
                ", parameters=" + parameters +
                ", isControlScript=" + isControlScript +
                ", clusterId='" + clusterId + '\'' +
                ']';
    }
}
