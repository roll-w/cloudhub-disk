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

import tech.rollw.disk.web.jobs.JobEvent;

/**
 * @author RollW
 */
public class ConditionTriggerEvent<E, M> implements JobEvent {
    private final ConditionTrigger<E, M> conditionTrigger;
    private final E event;

    public ConditionTriggerEvent(ConditionTrigger<E, M> conditionTrigger,
                                 E event) {
        this.conditionTrigger = conditionTrigger;
        this.event = event;
    }

    public E getEvent() {
        return event;
    }

    @Override
    public ConditionTrigger<E, M> getJobTrigger() {
        return conditionTrigger;
    }
}
