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

package tech.rollw.disk.web.controller.job;

import tech.rollw.disk.web.jobs.JobRegistryPoint;
import tech.rollw.disk.web.jobs.JobStatus;

/**
 * @author RollW
 */
public record JobVo(
        String id,
        long lastExecuteTime,
        long nextExecuteTime,
        String taskType,
        String triggerType,
        JobStatus status
) {

    public static JobVo of(JobRegistryPoint registryPoint) {
        return new JobVo(
                registryPoint.getJobId(),
                registryPoint.getJobTrigger().lastExecuteTime(),
                registryPoint.getJobTrigger().nextExecuteTime(),
                registryPoint.getJobTask().getClass().getCanonicalName(),
                registryPoint.getJobTrigger().getName(),
                registryPoint.getJobTrigger().getJobStatus()
        );
    }
}
