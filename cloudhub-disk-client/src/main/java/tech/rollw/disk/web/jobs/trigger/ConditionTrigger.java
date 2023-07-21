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

package tech.rollw.disk.web.jobs.trigger;

import tech.rollw.disk.web.event.EventCallback;
import tech.rollw.disk.web.event.EventRegistry;
import tech.rollw.disk.web.jobs.JobExecutor;
import tech.rollw.disk.web.jobs.JobStatus;
import tech.rollw.disk.web.jobs.JobTask;
import tech.rollw.disk.web.jobs.JobTrigger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class ConditionTrigger<R, M> implements JobTrigger,
        EventCallback<R> {

    private final AtomicLong lastTime = new AtomicLong();
    private final EventRegistry<R, M> eventRegistry;
    private final M condition;

    private String eventId;

    private JobTask jobTask;
    private JobExecutor jobExecutor;

    private JobStatus jobStatus;

    public ConditionTrigger(EventRegistry<R, M> eventRegistry,
                            M condition) {

        this.eventRegistry = eventRegistry;
        this.condition = condition;
    }

    @Override
    public void start() {
        if (jobStatus == JobStatus.RUNNING) {
            return;
        }
        jobStatus = JobStatus.RUNNING;
        eventId = eventRegistry.register(this, condition);
    }

    @Override
    public void stop() {
        if (jobStatus != JobStatus.RUNNING) {
            return;
        }
        jobStatus = JobStatus.STOPPED;
        eventRegistry.unregister(eventId);
    }

    @Override
    public JobTask getJobTask() {
        return jobTask;
    }

    @Override
    public void setJobTask(JobTask jobTask) {
        this.jobTask = jobTask;
    }

    @Override
    public JobExecutor getJobExecutor() {
        return jobExecutor;
    }

    @Override
    public void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    @Override
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    @Override
    public long lastExecuteTime() {
        return lastTime.get();
    }

    @Override
    public long nextExecuteTime() {
        // cannot calculate next execute time
        return 0;
    }

    @Override
    public String getName() {
        return "ConditionTrigger[Condition=" + condition +
                ";Registry=" + eventRegistry.getClass().getSimpleName() +
                ";EventId=" + eventId + "]";
    }

    @Override
    public void onEvent(R event) {
        lastTime.set(System.currentTimeMillis());
        execute(event);
    }

    private void execute(R event) {
        jobExecutor.execute(
                jobTask,
                new ConditionTriggerEvent<>(this, event)
        );
    }
}
