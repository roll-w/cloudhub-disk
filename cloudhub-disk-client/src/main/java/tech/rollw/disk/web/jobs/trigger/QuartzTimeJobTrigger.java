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

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.lingu.NonNull;
import tech.rollw.disk.web.jobs.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author RollW
 */
public class QuartzTimeJobTrigger implements JobTrigger, JobEvent {
    private static final Logger logger = LoggerFactory.getLogger(QuartzTimeJobTrigger.class);

    private static final String JOB_TASK = "job-task";
    private static final String JOB_EVENT = "job-event";
    private static final String JOB_EXECUTOR = "job-executor";

    private static final String DEFAULT_JOB_GROUP = "cloudhub-jobs";

    private final TimeTriggerType timeTriggerType;
    private final String jobGroup;

    private JobTask jobTask;
    private JobExecutor jobExecutor;
    private JobStatus jobStatus;

    private final Scheduler scheduler;
    private Trigger trigger;


    public QuartzTimeJobTrigger(TimeTriggerType timeTriggerType,
                                Scheduler scheduler) {
        this(timeTriggerType, scheduler, DEFAULT_JOB_GROUP);
    }

    public QuartzTimeJobTrigger(TimeTriggerType timeTriggerType,
                                Scheduler scheduler,
                                String jobGroup) {
        this.timeTriggerType = timeTriggerType;
        setJobStatus(JobStatus.NOT_STARTED);
        this.scheduler = scheduler;
        this.jobGroup = jobGroup;
    }

    private Trigger fromTimeTriggerType(TimeTriggerType timeTriggerType) {
        JobDetail jobDetail = buildJobDetail();
        return switch (timeTriggerType.getKind()) {
            case PERIODICALLY -> TriggerBuilder.newTrigger()
                    .withIdentity(getName())
                    .withSchedule(CronScheduleBuilder.cronSchedule(timeTriggerType.getCron()))
                    .withIdentity(getTaskName(), jobGroup)
                    .forJob(jobDetail)
                    .build();
            case SPECIFIC_TIME -> TriggerBuilder.newTrigger()
                    .withIdentity(getName())
                    .startAt(new Date(timeTriggerType.getTime()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                    .withIdentity(getTaskName(), jobGroup)
                    .forJob(jobDetail)
                    .build();
        };

    }

    private JobDetail buildJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_EVENT, this);
        jobDataMap.put(JOB_TASK, jobTask);
        jobDataMap.put(JOB_EXECUTOR, jobExecutor);
        return JobBuilder.newJob(QuartzJob.class)
                .usingJobData(jobDataMap)
                .withIdentity(getTaskName(), jobGroup)
                .build();
    }

    private String getTaskName() {
        return jobTask.getClass().getName();
    }

    private void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public void start() {
        if (jobStatus == JobStatus.RUNNING) {
            return;
        }
        setJobStatus(JobStatus.RUNNING);
        try {
            trigger = fromTimeTriggerType(timeTriggerType);
            scheduler.scheduleJob(buildJobDetail(), trigger);
        } catch (SchedulerException e) {
            setJobStatus(JobStatus.FAILED);
            logger.error("Failed to start job.", e);
        }
    }

    @Override
    public void stop() {
        if (jobStatus == JobStatus.STOPPED) {
            return;
        }
        setJobStatus(JobStatus.STOPPED);
        try {
            scheduler.unscheduleJob(trigger.getKey());
        } catch (SchedulerException e) {
            setJobStatus(JobStatus.FAILED);
            logger.error("Failed to stop job.", e);
        }
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
        if (trigger == null || trigger.getPreviousFireTime() == null) {
            return 0;
        }

        return trigger.getPreviousFireTime().getTime();
    }

    @Override
    public long nextExecuteTime() {
        if (trigger == null || trigger.getNextFireTime() == null) {
            return 0;
        }
        return trigger.getNextFireTime().getTime();
    }

    @Override
    public String getName() {
        return "QuartzTimeTrigger[" + timeTriggerType.getKind() +
                ", " + formatTimeOrCron()
                + "]";
    }

    private String formatTimeOrCron() {
        return switch (timeTriggerType.getKind()) {
            case PERIODICALLY -> timeTriggerType.getCron();
            case SPECIFIC_TIME -> LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timeTriggerType.getTime()),
                    TimeZone.getDefault().toZoneId()
            ).toString();
        };
    }

    @Override
    public JobTrigger getJobTrigger() {
        return this;
    }

    public static QuartzTimeJobTrigger of(long time, Scheduler scheduler) {
        return new QuartzTimeJobTrigger(TimeTriggerType.of(time), scheduler);
    }

    public static QuartzTimeJobTrigger of(String cron, Scheduler scheduler) {
        return new QuartzTimeJobTrigger(TimeTriggerType.of(cron), scheduler);
    }

    private static class QuartzJob implements Job {

        QuartzJob() {
        }

        @Override
        public void execute(@NonNull JobExecutionContext context) {
            JobTask jobTask = (JobTask) context.getJobDetail()
                    .getJobDataMap()
                    .get(JOB_TASK);
            JobEvent jobEvent = (JobEvent) context.getJobDetail()
                    .getJobDataMap()
                    .get(JOB_EVENT);
            JobExecutor jobExecutor = (JobExecutor) context.getJobDetail()
                    .getJobDataMap()
                    .get(JOB_EXECUTOR);
            if (jobExecutor == null) {
                jobTask.execute(jobEvent);
                return;
            }
            jobExecutor.execute(jobTask, jobEvent);
        }
    }
}
