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
import java.util.Map;

/**
 * View and stop clusters from the Mortar API.
 *
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class Clusters {

    private API api;

    /**
     * Construct a Clusters V2 API.
     *
     * @param api API client
     */
    public Clusters(API api) {
        this.api = api;
    }

    /**
     * Get all recent or running clusters from the API.
     *
     * @return list of all recent or running Clusters
     * @throws IOException if unable to fetch data from the API
     */
    public ClustersList getClusters() throws IOException {
        HttpRequest request = this.api.buildHttpGetRequest("clusters");
        return request.execute().parseAs(Clusters.ClustersList.class);
    }

    /**
     * Stop a running cluster.
     *
     * @throws IOException if unable to stop Cluster
     */
    public void stopCluster(String clusterId) throws IOException {
        HttpRequest request = this.api.buildHttpDeleteRequest("clusters/" + clusterId);
        request.execute();
    }

    /**
     * List of Clusters.
     */
    public static class ClustersList {

        /**
         * List of Clusters.
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
     * A Mortar Cluster.
     */
    public static class Cluster {

        @Key("cluster_id")
        private String clusterId;

        @Key("status_code")
        private String statusCode;

        @Key("status_description")
        private String statusDescription;

        @Key("task_trackers")
        private List<Map<String, String>> taskTrackers;

        @Key("start_timestamp")
        private String startTimestamp;

        @Key("running_timestamp")
        private String runningTimestamp;

        @Key("stop_timestamp")
        public String stopTimestamp;

        @Key("job_tracker_url")
        private String jobTrackerUrl;

        @Key("name_node_url")
        private String nameNodeUrl;

        @Key("duration")
        private String duration;

        @Key("cluster_type_code")
        private String clusterTypeCode;

        @Key("cluster_type_description")
        private String clusterTypeDescription;

        @Key("size")
        private int size;


        /**
         * Id of this cluster.
         */
        public String getClusterId() {
            return clusterId;
        }

        /**
         * Cluster status code enum.
         */
        public ClusterStatus getStatusCode() {
            return ClusterStatus.getEnum(statusCode);
        }

        /**
         * Cluster status code original string.
         */
        public String getStatusCodeString() {
            return statusCode;
        }

        /**
         * Full description of cluster status.
         */
        public String getStatusDescription() {
            return statusDescription;
        }

        /**
         * Address and url for task trackers for this cluster.
         */
        public List<Map<String, String>> getTaskTrackers() {
            return taskTrackers;
        }

        /**
         * Timestamp when the cluster started.
         * Example: 2012-02-28T03:35:42.831000+00:00
         */
        public String getStartTimestamp() {
            return startTimestamp;
        }

        /**
         * Timestamp when the cluster reached running state, or null if still starting.
         * Example: 2012-02-28T03:35:42.831000+00:00
         */
        public String getRunningTimestamp() {
            return runningTimestamp;
        }

        /**
         * Timestamp when the cluster stopped, or null if still running.
         * Example: 2012-02-28T03:35:42.831000+00:00
         */
        public String getStopTimestamp() {
            return stopTimestamp;
        }

        /**
         * Url for job tracker.
         */
        public String getJobTrackerUrl() {
            return jobTrackerUrl;
        }

        /**
         * Url for node.
         */
        public String getNameNodeUrl() {
            return nameNodeUrl;
        }

        /**
         * Length of time the cluster has existed.
         */
        public String getDuration() {
            return duration;
        }

        /**
         * Type of this cluster.
         */
        public ClusterType getClusterTypeCode() {
            return ClusterType.getEnum(clusterTypeCode);
        }

        /**
         * Full description of cluster type.
         */
        public String getClusterTypeDescription() {
            return clusterTypeDescription;
        }

        /**
         * Number of nodes in cluster.
         */
        public int getSize() {
            return size;
        }
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
        PENDING("pending"),

        /**
         * Cluster hardware is being started.
         */
        STARTING("starting"),

        /**
         * Cluster is starting, but has been requested to be stopped as soon as possible.
         */
        STARTING_REQUESTED_STOP("starting_requested_stop"),

        /**
         * Cluster software and packages being installed and started.
         */
        MORTAR_BOOTSTRAPPING("mortar_bootstrapping"),

        /**
         * Cluster is ready for use.
         */
        RUNNING("running"),

        /**
         * Cluster is in the process of shutting down.
         */
        STOPPING("stopping"),

        /**
         * Cluster logs are being copied to user bucket.
         */
        STOPPING_COPYING_LOGS("stopping_copying_logs"),

        /**
         * Cluster has been shut down.
         */
        DESTROYED("destroyed"),

        /**
         * Cluster failed to start.
         */
        FAILED("failed"),

        /**
         * Unrecognized status code.
         */
        UNKNOWN("UNKNOWN_STATUS");

        private String stringValue;

        /**
         * @param stringValue Mortar API compatible string value
         */
        ClusterStatus(String stringValue) {
            this.stringValue = stringValue;
        }

        /**
         * Override toString to return stringValue.
         *
         * @return Mortar API compatible string value
         */
        public String toString() {
            return stringValue;
        }

        /**
         * Get TaskStatus enum from stringValue.
         *
         * @param value String value generated by the toString method
         * @return TaskStatus enum for the typeString value
         */
        public static ClusterStatus getEnum(String value) {
            for (ClusterStatus t : values()) {
                if (t.stringValue.equalsIgnoreCase(value)) {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Type of a Cluster.
     */
    public enum ClusterType {

        /**
         * Cluster will last for the duration of a single job and then shut down immediately
         * upon job completion.
         */
        SINGLE_JOB("single_job"),

        /**
         * Cluster will remain running until it has been idle for over an hour.
         */
        PERSISTENT("persistent"),

        /**
         * Cluster will remain running until explicitly shut down.
         */
        PERMANENT("permanent"),

        /**
         * Unrecognized cluster type.
         */
        UNKNOWN("UNKNOWN_CLUSTER_TYPE");


        private String typeString;

        /**
         * @param typeString Mortar API compatible string value
         */
        ClusterType(String typeString) {
            this.typeString = typeString;
        }

        /**
         * Override toString to return typeString.
         *
         * @return Mortar API compatible string value typeString
         */
        public String toString() {
            return typeString;
        }

        /**
         * Get ClusterType enum from typeString.
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
            return UNKNOWN;
        }
    }
}
