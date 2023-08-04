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

import tech.rollw.disk.web.jobs.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class JavaTimerTimeJobTrigger implements JobTrigger, JobEvent {
    private final TimeTriggerType timeTriggerType;

    private JobTask jobTask;
    private JobExecutor jobExecutor;
    private JobStatus jobStatus;

    private final AtomicLong nextTime = new AtomicLong();
    private final AtomicLong lastTime = new AtomicLong();

    private Timer timer;

    public JavaTimerTimeJobTrigger(TimeTriggerType timeTriggerType) {
        this.timeTriggerType = timeTriggerType;
        setJobStatus(JobStatus.NOT_STARTED);
    }

    private void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public void start() {
        if (jobStatus == JobStatus.RUNNING) {
            return;
        }
        jobStatus = JobStatus.RUNNING;
        resetTimer();
    }

    private void resetTimer() {
        Long calcedNextTime = nextTime();
        if (calcedNextTime == null) {
            setJobStatus(JobStatus.FINISHED);
            return;
        }
        nextTime.set(calcedNextTime);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new Task(), delayOfNextTime(nextTime.get()));
    }

    private long delayOfNextTime(long nextTime) {
        long now = System.currentTimeMillis();
        if (nextTime < now) {
            return 0;
        }
        return nextTime - now;
    }


    @Override
    public void stop() {
        if (jobStatus == JobStatus.NOT_STARTED) {
            return;
        }
        setJobStatus(JobStatus.STOPPED);
        if (timer != null) {
            timer.cancel();
        }
    }

    private void execute() {
        jobExecutor.execute(jobTask, this);
    }

    private class Task extends TimerTask {
        @Override
        public void run() {
            lastTime.set(nextTime.get());
            execute();
            resetTimer();
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
        return lastTime.get();
    }

    @Override
    public long nextExecuteTime() {
        return nextTime.get();
    }

    @Override
    public String getName() {
        return "JavaTimeTrigger[" + timeTriggerType.getKind() +
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

    /**
     * Get next time in timestamp
     *
     * @return next time in timestamp
     */
    private Long nextTime() {
        LocalDateTime now = LocalDateTime.now();
        return switch (timeTriggerType.getKind()) {
            case PERIODICALLY -> {
                LocalDateTime next = timeTriggerType.getCronExpression()
                        .next(now);
                if (next == null) {
                    yield null;
                }
                yield next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            case SPECIFIC_TIME -> {
                long nowInMilli =
                        now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if (nowInMilli >= timeTriggerType.getTime()) {
                    yield null;
                }
                yield timeTriggerType.getTime();
            }
        };
    }

    @Override
    public JobTrigger getJobTrigger() {
        return this;
    }

    public static JavaTimerTimeJobTrigger of(long time) {
        return new JavaTimerTimeJobTrigger(TimeTriggerType.of(time));
    }

    public static JavaTimerTimeJobTrigger of(String cron) {
        return new JavaTimerTimeJobTrigger(TimeTriggerType.of(cron));
    }
}
