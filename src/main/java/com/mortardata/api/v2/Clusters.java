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
 * View and stop clusters from the Mortar API
 *
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class Clusters {

    private API api;

    /**
     * Construct a Clusters V2 API
     *
     * @param api API client
     */
    public Clusters(API api) {
        this.api = api;
    }

    /**
     * Get all recent or running clusters from the API
     *
     * @return list of all recent or running Clusters
     * @throws IOException if unable to fetch data from the API
     */
    public ClustersList getClusters() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("clusters");
        return request.execute().parseAs(Clusters.ClustersList.class);
    }

    /**
     * Stop a running cluster
     *
     * @throws IOException if unable to stop Cluster
     */
    public void stopCluster(String clusterId) throws IOException {
        HttpRequest request = this.api.buildHttpDeleteRequest("clusters/" + clusterId);
        request.execute();
    }

    /**
     * List of Clusters
     */
    public static class ClustersList {

        /**
         * List of Clusters
         */
        @Key
        public List<Clusters.Cluster> clusters;

        @Override
        public String toString() {
            return "ClustersList [" +
                    "clusters=" + clusters +
                    ']';
        }
    }


    /**
     * A Mortar Cluster
     */
    public static class Cluster {

        /**
         * Id of this cluster.
         */
        @Key("cluster_id")
        public String clusterId;

        /**
         * Cluster status code.
         */
        @Key("status_code")
        public ClusterStatus statusCode;

        /**
         * Full description of cluster status.
         */
        @Key("status_description")
        public String statusDescription;

        /**
         * Address and url for task trackers for this cluster.
         */
        @Key("task_trackers")
        public List<Map<String, String>> taskTrackers;

        /**
         * Time cluster started.
         */
        @Key("start_timestamp")
        public String startTimestamp;

        /**
         * Time cluster reached running state, or null if still starting.
         */
        @Key("running_timestamp")
        public String runningTimestamp;

        /**
         * Time cluster stopped, or null if still running.
         */
        @Key("stop_timestamp")
        public String stopTimestamp;

        /**
         * Url for job tracker.
         */
        @Key("job_tracker_url")
        public String jobTrackerUrl;

        /**
         * Url for node.
         */
        @Key("name_node_url")
        public String nameNodeUrl;

        /**
         * Length of time the cluster has existed.
         */
        @Key("duration")
        public String duration;

        /**
         * Type of this cluster.
         */
        @Key("cluster_type_code")
        public ClusterType clusterTypeCode;

        /**
         * Full description of cluster type.
         */
        @Key("cluster_type_description")
        public String clusterTypeDescription;

        /**
         * Number of nodes in cluster.
         */
        @Key("size")
        public int size;

        @Override
        public String toString() {
            return "Cluster [" +
                    "clusterId='" + clusterId + '\'' +
                    ", statusCode=" + statusCode +
                    ", statusDescription='" + statusDescription + '\'' +
                    ", taskTrackers=" + taskTrackers +
                    ", startTimestamp='" + startTimestamp + '\'' +
                    ", runningTimestamp='" + runningTimestamp + '\'' +
                    ", stopTimestamp='" + stopTimestamp + '\'' +
                    ", jobTrackerUrl='" + jobTrackerUrl + '\'' +
                    ", nameNodeUrl='" + nameNodeUrl + '\'' +
                    ", duration='" + duration + '\'' +
                    ", clusterTypeCode=" + clusterTypeCode +
                    ", clusterTypeDescription='" + clusterTypeDescription + '\'' +
                    ", size=" + size +
                    ']';
        }
    }

    /**
     * Cluster status.
     */
    public enum ClusterStatus {

        /**
         * Initial state, pending launch.
         */
        @Value("pending")
        PENDING,

        /**
         * Cluster hardware is being started.
         */
        @Value("starting")
        STARTING,

        /**
         * Cluster is starting, but has been requested to be stopped as soon as possible.
         */
        @Value("starting_requested_stop")
        STARTING_REQUESTED_STOP,

        /**
         * Cluster software and packages being installed and started.
         */
        @Value("mortar_bootstrapping")
        MORTAR_BOOTSTRAPPING,

        /**
         * Cluster is ready for use.
         */
        @Value("running")
        RUNNING,

        /**
         * Cluster is in the process of shutting down.
         */
        @Value("stopping")
        STOPPING,

        /**
         * Cluster logs are being copied to user bucket.
         */
        @Value("stopping_copying_logs")
        STOPPING_COPYING_LOGS,

        /**
         * Cluster has been shut down.
         */
        @Value("destroyed")
        DESTROYED,

        /**
         * Cluster failed to start.
         */
        @Value("failed")
        FAILED
    }

    /**
     * Type of a Cluster
     */
    public enum ClusterType {

        /**
         * Cluster will last for the duration of a single job and then shut down immediately
         * upon job completion.
         */
        @Value("single_job")
        SINGLE_JOB("single_job"),

        /**
         * Cluster will remain running until it has been idle for over an hour.
         */
        @Value("persistent")
        PERSISTENT("persistent"),

        /**
         * Cluster will remain running until explicitly shut down.
         */
        @Value("permanent")
        PERMANENT("permanent");

        private String typeString;

        /**
         * @param typeString Mortar API compatible string value
         */
        ClusterType(String typeString) {
            this.typeString = typeString;
        }

        /**
         * Override toString to return typeString
         *
         * @return Mortar API compatible string value typeString
         */
        public String toString() {
            return typeString;
        }

        /**
         * Get ClusterType enum from typeString
         *
         * @param typeString String value generated by the toString method
         * @return ClusterType enum for the typeString value
         */
        public static ClusterType getEnum(String typeString) {
            for (ClusterType t : values()) {
                if (t.typeString.equalsIgnoreCase(typeString)) {
                    return t;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
