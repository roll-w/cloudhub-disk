/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.rollw.disk.web.jobs;

import java.util.List;

/**
 * @author RollW
 */
public interface JobRegistry {
    /**
     * Register a job task and a job trigger.
     *
     * @param jobTask    job task
     * @param jobTrigger job trigger
     * @return job id
     */
    String register(JobTask jobTask, JobTrigger jobTrigger);

    /**
     * Unregister a job by job id.
     *
     * @param jobId job id
     */
    void unregister(String jobId);

    JobRegistryPoint getJobRegistryPoint(String jobId);

    List<JobRegistryPoint> getJobRegistryPoints();
}
