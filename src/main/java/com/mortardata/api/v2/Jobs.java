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

/**
 * Run and fetch jobs from the Mortar API.
 * 
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class Jobs {
    
    private API api;
    private static final int POLLING_DELAY = 5000;

    /**
     * status_code values that indicate a job in a final state.
     */
    public static final Set<JobStatus> JOB_STATUS_COMPLETE = new HashSet<JobStatus>
            (Arrays.asList(JobStatus.SCRIPT_ERROR, JobStatus.PLAN_ERROR, JobStatus.SUCCESS,
                    JobStatus.EXECUTION_ERROR, JobStatus.SERVICE_ERROR, JobStatus.STOPPED));

    /**
     * Construct a Jobs V2 API.
     * 
     * @param api API client
     */
    public Jobs(API api) {
        this.api = api;
    }

    /**
     * Get all jobs from the API.
     * 
     * @return list of all Jobs
     * @throws IOException if unable to fetch data from the API
     */
    public JobsList getJobs() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs");
        return request.execute().parseAs(Jobs.JobsList.class);
    }

    /**
     * Get a subset of jobs from the API.
     * 
     * @param skip Number of jobs to skip (jobs sorted in desc order of startTimestamp)
     * @param limit Maximum number of jobs to return
     * @return list of Jobs
     * @throws IOException if unable to fetch data from the API
     */
    public JobsList getJobs(Integer skip, Integer limit) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs?skip=" + skip + "&limit=" + limit);
        return request.execute().parseAs(Jobs.JobsList.class);
    }

    /**
     * Get a job from the API by ID.
     * 
     * @param jobId ID of the Job
     * @return requested Job
     * @throws IOException if Job does not exist or unable to fetch job from the API
     */
    public Job getJob(String jobId) throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs/" + jobId);
        return request.execute().parseAs(Jobs.Job.class);
    }

    /**
     * Stop a running job.
     *
     * @param jobId ID of Job to stop
     * @throws IOException if unable to stop Job
     */
    public void stopJob(String jobId) throws IOException {
        HttpRequest request = this.api.buildHttpDeleteRequest("jobs/" + jobId);
        request.execute();
    }

    /**
     * Run a new Job.
     *
     * @param jobRequest Info about job to run
     * @return job_id ID of job that was started
     * @throws IOException if unable to run job on API
     */
    public String postJob(JobRequest jobRequest) throws IOException {
        HttpRequest request = this.api.buildHttpPostRequest("jobs", jobRequest.getArguments());
        return (String) request.execute().parseAs(HashMap.class).get("job_id");
    }

    /**
     * Get the status of Job.
     *
     * @param jobId ID of job for which to request status
     * @return status of the job 
     * @throws IOException if job does not exist or unable to contact API
     */
    public JobStatus getJobStatus(String jobId) throws IOException {
        Job job = getJob(jobId);
        return job.getStatusCode();
    }

    /**
     * Block until a job has completed, polling for status.
     *
     * @param jobId ID of job to wait for completion
     * @return final statusCode
     * @throws IOException if unable to contact API for status
     * @throws InterruptedException if polling interrupted
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
     * Information about an error with a Job.
     */
    public static class JobError {
        
        /**
         * Error message.
         */
        @Key
        public String message;
        
        /**
         * Type of error that occurred.
         */
        @Key("type")
        public String errorType;
        
        /**
         * Line number for error, if available.
         */
        @Key("line_number")
        String lineNumber;
        
        /**
         * Column number for error, if available.
         */
        @Key("column_number")
        String columnNumber;

        @Override
        public String toString() {
            return "JobError [message=" + message + ", errorType=" + errorType
                    + ", lineNumber=" + lineNumber + ", columnNumber="
                    + columnNumber + "]";
        }
        
    }
    
    /**
     * List of Job.
     */
    public static class JobsList {


        /**
         * List of Job.
         */
        @Key
        public List<Jobs.Job> jobs;

        @Override
        public String toString() {
            return "JobsList [jobs=" + jobs + "]";
        }
        
        
    }

    /**
     * A Mortar Job.
     */
    public static class Job {

        @Key("status_code")
        private String statusCode;

        @Key("status_description")
        private String statusDescription;

        @Key("script_name")
        private String scriptName;

        @Key("pigscript_name")
        private String pigscriptName;

        @Key("cluster_id")
        private String clusterId;

        @Key("error")
        private JobError error;

        @Key
        private String note;

        @Key
        private Integer progress;

        @Key("script_type")
        private String scriptType;

        @Key("project_name")
        private String projectName;

        @Key("script_parameters")
        private Map<String, String> scriptParameters;

        @Key("git_ref")
        private String gitRef;

        @Key("start_timestamp")
        private String startTimestamp;

        @Key("stop_timestamp")
        private String stopTimestamp;


        /**
         * Job status code.
         */
        public JobStatus getStatusCode() {
            return JobStatus.getEnum(statusCode);
        }

        /**
         * Job status code original string.
         */
        public String getStatusCodeString() {
            return statusCode;
        }

        /**
         * Full description of job status.
         */
        public String getStatusDescription() {
            return statusDescription;
        }

        /**
         * Name of the script that was run.
         */
        public String getScriptName() {
            return scriptName;
        }

        /**
         * Name of the script that was run, if a pigscript.
         */
        public String getPigscriptName() {
            return pigscriptName;
        }

        /**
         * Cluster on which this Job is running, or null if not yet assigned.
         */
        public String getClusterId() {
            return clusterId;
        }

        /**
         * Information about an error, if one occurs for this Job.
         */
        public JobError getError() {
            return error;
        }

        /**
         * Note added to the job.
         */
        public String getNote() {
            return note;
        }

        /**
         * Overall job progress.
         */
        public Integer getProgress() {
            return progress;
        }

        /**
         * Type of script that was run: cli_pig, cli_control, 'web'.
         */
        public String getScriptType() {
            return scriptType;
        }

        /**
         * Name of the Mortar project for the Job.
         */
        public String getProjectName() {
            return projectName;
        }

        /**
         * Parameters used for the Job.
         */
        public Map<String, String> getScriptParameters() {
            return scriptParameters;
        }

        /**
         * Git hash or branch at which Job was run.
         */
        public String getGitRef() {
            return gitRef;
        }

        /**
         * Timestamp when the job started running.
         * Example: 2012-02-28T03:35:42.831000+00:00
         */
        public String getStartTimestamp() {
            return startTimestamp;
        }

        /**
         * Timestamp when the job stopped running.
         * Example: 2012-02-28T03:41:52.613000+00:00"
         */
        public String getStopTimestamp() {
            return stopTimestamp;
        }

        @Override
        public String toString() {
            return "Job [statusCode=" + statusCode + ", statusDescription="
                    + statusDescription + ", scriptName=" + scriptName
                    + ", pigscriptName=" + pigscriptName + ", clusterId="
                    + clusterId + ", error=" + error + ", note=" + note
                    + ", progress=" + progress + ", scriptType=" + scriptType
                    + ", projectName=" + projectName + ", scriptParameters="
                    + scriptParameters + ", gitRef=" + gitRef
                    + ", startTimestamp=" + startTimestamp + ", stopTimestamp="
                    + stopTimestamp + "]";
        }
        
    }

    /**
     * Job status.
     */
    public enum JobStatus {
        
        /**
         * Job received and queued, not yet validated.
         */
        STARTING("starting"),

        /**
         * Pig server starting (happens on first request in session)
         */
        GATEWAY_STARTING("GATEWAY_STARTING"),
        
        /**
         * Checking the script for syntax and S3 data storage errors.
         */
        VALIDATING_SCRIPT("validating_script"),
        
        /**
         * An error was detected in the script before running Job.
         */
        SCRIPT_ERROR("script_error"),
        
        /**
         * An error was detected in the script before running Job.
         */
        PLAN_ERROR("plan_error"),
        
        /**
         * Starting a Hadoop cluster for the Job.
         */
        STARTING_CLUSTER("starting_cluster"),
        
        /**
         * Running the Job.
         */
        RUNNING("running"),
        
        /**
         * Job completed successfully.
         */
        SUCCESS("success"),
        
        /**
         * An error occurred during the Job run.
         */
        EXECUTION_ERROR("execution_error"),
        
        /**
         * An internal error occurred while attempting to run the Job.
         */
        SERVICE_ERROR("service_error"),
        
        /**
         * Job is has been requested to be stopped by user.
         */
        STOPPING("stopping"),
        
        /**
         * Job has been stopped by user.
         */
        STOPPED("stopped"),

        /**
         * Unrecognized status code.
         */
        UNKNOWN("UNKNOWN_STATUS");


        private String stringValue;

        /**
         * @param stringValue Mortar API compatible string value
         */
        JobStatus(String stringValue) {
            this.stringValue = stringValue;
        }

        /**
         * Override toString to return stringValue
         *
         * @return Mortar API compatible string value
         */
        public String toString() {
            return stringValue;
        }

        /**
         * Get TaskStatus enum from stringValue
         *
         * @param value String value generated by the toString method
         * @return TaskStatus enum for the typeString value
         */
        public static JobStatus getEnum(String value) {
            for (JobStatus t : values()) {
                if (t.stringValue.equalsIgnoreCase(value)) {
                    return t;
                }
            }
            return UNKNOWN;
        }

    }

}
