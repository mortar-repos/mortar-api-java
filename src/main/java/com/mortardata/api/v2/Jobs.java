package com.mortardata.api.v2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Key;

public class Jobs {
    
    private API api;

    public Jobs(API api) {
        this.api = api;
    }
    
    public JobsList getJobs() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("jobs?limit=10");
        return request.execute().parseAs(Jobs.JobsList.class);
    }
    
    public static class JobsList {
        @Key("job_count")
        public Integer jobCount;
        
        @Key
        public List<Jobs.Job> jobs;
    }

    public static class Job {
        @Key("status_code")
        public String statusCode;
    
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
    
}
