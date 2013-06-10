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

    @Value
    QUEUED,
    @Value
    GATEWAY_STARTING,
    @Value
    PROGRESS,
    @Value
    FAILURE,
    @Value
    SUCCESS,
    @Value
    KILLED,
    @Value("BUILDING_PLAN")
    ILLUSTRATE_BUILDING_PLAN,
    @Value("READING_DATA")
    ILLUSTRATE_READING_DATA,
    @Value("PRUNING_DATA")
    ILLUSTRATE_PRUNE_DATA,
    @Value("FINALIZE_RESULTS")
    ILLUSTRATE_FINALIZE_RESULTS;
}
