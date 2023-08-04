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

package tech.rollw.disk.web.domain.statistics.service;

import org.quartz.Scheduler;
import tech.rollw.disk.web.domain.statistics.DatedStatistics;
import tech.rollw.disk.web.domain.statistics.Statistics;
import tech.rollw.disk.web.domain.statistics.repository.DatedStatisticsRepository;
import tech.rollw.disk.web.domain.statistics.repository.StatisticsRepository;
import tech.rollw.disk.web.jobs.JobEvent;
import tech.rollw.disk.web.jobs.JobRegistry;
import tech.rollw.disk.web.jobs.JobTask;
import tech.rollw.disk.web.jobs.trigger.JavaTimerTimeJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.rollw.disk.web.jobs.trigger.QuartzTimeJobTrigger;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class DataArchivingTask implements JobTask {
    private static final Logger logger = LoggerFactory.getLogger(DataArchivingTask.class);
    // 5 minutes
    /**
     * Delay to execute the task. Because of the
     * task executed at 4:00 am every day, but
     * the task has to record the data of the
     * day the application started, so the task
     * will be executed after the delay time
     * after starting the application.
     */
    private static final long DELAY = 1000 * 60 * 5;

    private final DatedStatisticsRepository datedStatisticsRepository;
    private final StatisticsRepository statisticsRepository;

    private final Map<String, DatedStatistics> latestStatisticsByKey =
            new HashMap<>();

    public DataArchivingTask(JobRegistry jobRegistry,
                             Scheduler scheduler,
                             DatedStatisticsRepository datedStatisticsRepository,
                             StatisticsRepository statisticsRepository) {
        this.datedStatisticsRepository = datedStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
        jobRegistry.register(
                this,
                // execute at 4:00 am every day
                QuartzTimeJobTrigger.of("0 0 4 * * ?", scheduler)
        );
        jobRegistry.register(
                this,
                JavaTimerTimeJobTrigger.of(System.currentTimeMillis() + DELAY)
        );
    }


    @Override
    public void execute(JobEvent jobEvent) {
        LocalDate localDate = LocalDate.now();

        logger.debug("Execute statistics data archiving task at {}",
                localDate
        );

        List<Statistics> statistics =
                statisticsRepository.get();
        statistics.forEach(stat -> persistOf(stat, localDate));
    }

    private void persistOf(Statistics statistics,
                           LocalDate localDate) {
        String statisticsKey = statistics.getKey();
        Map<String, String> value = statistics.getValue();
        if (value == null || value.isEmpty()) {
            return;
        }
        DatedStatistics datedStatistics = findLatestDatedStatistics(
                statistics, localDate
        );
        Map<String, String> latestValue = datedStatistics.getValue();
        if (StatHelper.equalsStatistics(value, latestValue)) {
            return;
        }
        DatedStatistics updated = datedStatistics.toBuilder()
                .setValue(new HashMap<>(value))
                .build();
        datedStatisticsRepository.update(updated);
        latestStatisticsByKey.put(statisticsKey, updated);
    }

    private DatedStatistics findLatestDatedStatistics(
            Statistics statistics, LocalDate localDate) {
        final String statisticsKey = statistics.getKey();
        DatedStatistics datedStatistics =
                latestStatisticsByKey.get(statisticsKey);
        if (datedStatistics != null) {
            return datedStatistics;
        }
        DatedStatistics queried =
                datedStatisticsRepository.getLatestOfKey(statisticsKey);

        if (queried == null) {
            Map<String, String> copiedValue =
                    new HashMap<>(statistics.getValue());
            DatedStatistics buildNewOne = DatedStatistics.builder()
                    .setKey(statisticsKey)
                    .setValue(copiedValue)
                    .setDate(localDate)
                    .build();
            long id = datedStatisticsRepository.insert(buildNewOne);
            DatedStatistics inserted = buildNewOne
                    .toBuilder()
                    .setId(id)
                    .build();
            latestStatisticsByKey.put(statisticsKey, inserted);
            return inserted;
        }
        return queried;
    }
}
