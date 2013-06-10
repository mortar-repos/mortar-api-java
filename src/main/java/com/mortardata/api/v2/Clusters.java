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
import com.google.api.client.util.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * TODO doc.
 */
public class Clusters {

    private API api;

    public Clusters(API api) {
        this.api = api;
    }

    /**
     * TODO doc.
     *
     * @return
     * @throws IOException
     */
    public ClustersList getClusters() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("clusters");
        return request.execute().parseAs(Clusters.ClustersList.class);
    }

    /**
     * TODO doc.
     *
     * @return
     * @throws IOException
     */
    public void stopCluster(String clusterId) throws IOException {
        HttpRequest request = this.api.buildHttpDeleteRequest("clusters/" + clusterId);
        request.execute();
    }

    /**
     * TODO doc.
     */
    public static class ClustersList {
        @Key
        public List<Clusters.Cluster> clusters;
    }

    /**
     * TODO doc.
     */
    public static class Cluster {
        @Key("cluster_id")
        public String clusterId;

        @Key("status_code")
        public ClusterStatus statusCode;

        @Key("status_description")
        public String statusDescription;

        @Key("task_trackers")
        public List<Map<String, String>> taskTrackers;

        @Key("start_timestamp")
        public String startTimestamp;

        @Key("running_timestamp")
        public String runningTimestamp;

        @Key("stop_timestamp")
        public String stopTimestamp;

        @Key("job_tracker_url")
        public String jobTrackerUrl;

        @Key("name_node_url")
        public String nameNodeUrl;

        @Key("duration")
        public String duration;

        @Key("cluster_type_code")
        public String clusterTypeCode;

        @Key("cluster_type_description")
        public String clusterTypeDescription;

        @Key("size")
        public int size;
    }

    public enum ClusterStatus {

        @Value("pending")
        PENDING,
        @Value("starting")
        STARTING,
        @Value("starting_requested_stop")
        STARTING_REQUESTED_STOP,
        @Value("mortar_bootstrapping")
        MORTAR_BOOTSTRAPPING,
        @Value("running")
        RUNNING,
        @Value("stopping")
        STOPPING,
        @Value("destroyed")
        DESTROYED,
        @Value("failed")
        FAILED;
    }
}
