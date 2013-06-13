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

import com.google.api.client.util.Value;

/**
 * TODO doc.
 *
 */
public enum TaskStatus {

    /**
     * Submitted, pending execution.
     */
    @Value
    QUEUED,

    /**
     * Pig server starting (happens on first request in session).
     */
    @Value
    GATEWAY_STARTING,

    /**
     * Operation in progress.
     */
    @Value
    PROGRESS,

    /**
     * Syntax error in pigscript (details in error_message field)
     */
    @Value
    FAILURE,

    /**
     * Operation complete; results available if applicable
     */
    @Value
    SUCCESS,

    /**
     * Operation terminated.
     */
    @Value
    KILLED,

    /**
     * Illustrate compiling plan for Pig script.
     */
    @Value("BUILDING_PLAN")
    ILLUSTRATE_BUILDING_PLAN,

    /**
     * Illustrate reading source data.
     */
    @Value("READING_DATA")
    ILLUSTRATE_READING_DATA,

    /**
     * Illustrate pruning data to minimal result set.
     */
    @Value("PRUNING_DATA")
    ILLUSTRATE_PRUNE_DATA,

    /**
     * Illustrate post-processing of result data.
     */
    @Value("FINALIZE_RESULTS")
    ILLUSTRATE_FINALIZE_RESULTS;
}
