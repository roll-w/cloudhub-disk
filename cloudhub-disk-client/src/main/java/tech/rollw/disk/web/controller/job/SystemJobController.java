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

import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.jobs.JobRegistry;
import tech.rollw.disk.web.jobs.JobRegistryPoint;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class SystemJobController {
    private final JobRegistry jobRegistry;

    public SystemJobController(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    @GetMapping("/jobs")
    public HttpResponseEntity<List<JobVo>> getJobList() {
        List<JobRegistryPoint> registryPoints =
                jobRegistry.getJobRegistryPoints();

        return HttpResponseEntity.success(
                registryPoints.stream()
                        .map(JobVo::of)
                        .toList()
        );
    }
}
