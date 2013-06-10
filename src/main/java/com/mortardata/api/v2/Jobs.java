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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

/**
 * TODO doc.
 */
public class Jobs {
    
    private API api;
    private static final int POLLING_DELAY = 5000;


    public static final Set<JobStatus> JOB_STATUS_COMPLETE = new HashSet<JobStatus>
            (Arrays.asList(JobStatus.SCRIPT_ERROR, JobStatus.PLAN_ERROR, JobStatus.SUCCESS,
                    JobStatus.EXECUTION_ERROR, JobStatus.SERVICE_ERROR, JobStatus.STOPPED));

    /**
     * TODO doc.
     * @param api
     */
    public Jobs(API api) {
        this.api = api;
    }

    /**
     * TODO doc.
     * 
     * @return
     * @throws IOException
     */
    public JobsList getJobs() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs");
        return request.execute().parseAs(Jobs.JobsList.class);
    }

    /**
     * TODO doc.
     * 
     * @param skip
     * @param limit
     * @return
     * @throws IOException
     */
    public JobsList getJobs(Integer skip, Integer limit) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs?skip=" + skip + "&limit=" + limit);
        return request.execute().parseAs(Jobs.JobsList.class);
    }

    /**
     * TODO doc.
     * 
     * @param jobId
     * @return
     * @throws IOException
     */
    public Job getJob(String jobId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs/" + jobId);
        return request.execute().parseAs(Jobs.Job.class);
    }

    /**
     * TODO doc.
     *
     * @param jobId
     * @return
     * @throws IOException
     */
    public void stopJob(String jobId) throws IOException {
        HttpRequest request = this.api.buildHttpDeleteRequest("jobs/" + jobId);
        request.execute();
    }

    /**
     * TODO doc.
     *
     * @param jobRequest
     * @return job_id
     * @throws IOException
     */
    public String postJob(JobRequest jobRequest) throws IOException {
        HttpRequest request = this.api.buildHttpPostRequest("jobs", jobRequest.getArguments());
        return (String) request.execute().parseAs(HashMap.class).get("job_id");
    }

    /**
     * TODO doc.
     *
     * @param jobId
     * @return statusCode
     * @throws IOException
     */
    public JobStatus getJobStatus(String jobId) throws IOException {
        Job job = getJob(jobId);
        return job.statusCode;
    }

    /**
     * TODO doc.
     *
     * @param jobId
     * @return final statusCode
     * @throws IOException
     * @throws InterruptedException
     */
    public JobStatus blockUntilJobComplete(String jobId) throws IOException, InterruptedException {
        while (true) {
            JobStatus jobStatus = getJobStatus(jobId);
            if (JOB_STATUS_COMPLETE.contains(jobStatus)) {
                return jobStatus;
            }
            Thread.sleep(POLLING_DELAY);
        }
    }


    /**
     * TODO doc.
     */
    public static class JobsList {
        @Key("job_count")
        public Integer jobCount;
        
        @Key
        public List<Jobs.Job> jobs;
    }

    /**
     * TODO doc.
     */
    public static class Job {
        @Key("status_code")
        public JobStatus statusCode;
    
        @Key("status_description")
        public String statusDescription;
    
        @Key("script_name")
        public String scriptName;
        
        @Key("pigscript_name")
        public String pigscriptName;
        
        @Key("cluster_id")
        public String clusterId;
    
        @Key
        public String note;
    
        @Key
        public Integer progress;
    
        @Key("script_type")
        public String scriptType;
        
        @Key("project_name")
        public String projectName;
        
        @Key("script_parameters")
        public Map<String, String> scriptParameters;
        
        @Key("git_ref")
        public String gitRef;
        
        @Key("start_timestamp")
        public String startTimestamp;
    
        @Key("stop_timestamp")
        public String stopTimestamp;
    
    }

    /**
     * TODO doc.
     */
    public enum JobStatus {

        @Value("starting")
        STARTING,
        @Value("validating_script")
        VALIDATING_SCRIPT,
        @Value("script_error")
        SCRIPT_ERROR,
        @Value("plan_error")
        PLAN_ERROR,
        @Value("starting_cluster")
        STARTING_CLUSTER,
        @Value("running")
        RUNNING,
        @Value("success")
        SUCCESS,
        @Value("execution_error")
        EXECUTION_ERROR,
        @Value("service_error")
        SERVICE_ERROR,
        @Value("stopping")
        STOPPING,
        @Value("stopped")
        STOPPED;

    }

}
