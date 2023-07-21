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

import org.springframework.expression.ExpressionException;
import org.springframework.scheduling.support.CronExpression;

/**
 * @author RollW
 */
public class TimeTriggerType {
    private final Kind kind;
    private final long time;
    private final String cron;
    private final CronExpression cronExpression;

    public TimeTriggerType(long time) {
        this.kind = Kind.SPECIFIC_TIME;
        this.time = time;
        this.cron = null;
        this.cronExpression = null;
    }

    public TimeTriggerType(String cron) {
        this.kind = Kind.PERIODICALLY;
        this.time = 0;
        this.cron = cron;
        try {
            this.cronExpression = CronExpression.parse(cron);
        } catch (IllegalArgumentException e) {
            throw new ExpressionException("Cron expression is invalid [" + cron + "].");
        }
    }

    public Kind getKind() {
        return kind;
    }

    public long getTime() {
        return time;
    }

    public String getCron() {
        return cron;
    }

    public CronExpression getCronExpression() {
        return cronExpression;
    }

    public enum Kind {
        PERIODICALLY,
        SPECIFIC_TIME,
    }

    public static TimeTriggerType of(long time) {
        return new TimeTriggerType(time);
    }

    public static TimeTriggerType of(String cron) {
        return new TimeTriggerType(cron);
    }
}
