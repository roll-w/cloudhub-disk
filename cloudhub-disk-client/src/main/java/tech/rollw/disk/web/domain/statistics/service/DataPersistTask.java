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

import tech.rollw.disk.web.domain.statistics.Statistics;
import tech.rollw.disk.web.domain.statistics.repository.StatisticsRepository;
import tech.rollw.disk.web.jobs.JobEvent;
import tech.rollw.disk.web.jobs.JobRegistry;
import tech.rollw.disk.web.jobs.JobTask;
import tech.rollw.disk.web.jobs.trigger.TimeJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
@Service
public class DataPersistTask implements JobTask {
    private static final Logger logger = LoggerFactory.getLogger(DataPersistTask.class);

    private final StatisticsRepository statisticsRepository;
    private final List<StatisticsPersistable> statisticsPersistables;

    private final Map<String, Statistics> statisticsByKey =
            new ConcurrentHashMap<>();
    private final Map<String, Long> lastVersionByKey =
            new ConcurrentHashMap<>();

    public DataPersistTask(JobRegistry jobRegistry,
                           StatisticsRepository statisticsRepository,
                           List<StatisticsPersistable> statisticsPersistables) {
        this.statisticsRepository = statisticsRepository;
        this.statisticsPersistables = statisticsPersistables;
        jobRegistry.register(
                this,
                TimeJobTrigger.of("0 0/5 * * * ?")
        );
        jobRegistry.register(
                this,
                TimeJobTrigger.of(System.currentTimeMillis() + 1000 * 10)
        );
    }


    @Override
    public void execute(JobEvent jobEvent) {
        statisticsPersistables.forEach(this::tryPersist);
    }

    private void tryPersist(
            StatisticsPersistable statisticsPersistable) {
        List<String> keys =
                statisticsPersistable.getStatisticsKeys();
        if (keys.isEmpty()) {
            return;
        }
        keys.forEach(key -> persistOfKey(statisticsPersistable, key));
    }

    private void persistOfKey(StatisticsPersistable statisticsPersistable,
                              String statisticsKey) {
        long version = statisticsPersistable.getStatisticsVersion();
        if (lastVersionByKey.getOrDefault(statisticsKey, -1L)
                == version) {
            return;
        }
        Map<String, String> statisticsMap =
                statisticsPersistable.getStatistics(statisticsKey);
        if (statisticsMap == null || statisticsMap.isEmpty()) {
            logger.debug("StatisticsPersistable {}(key={}) return empty statistics, rescan.",
                    statisticsPersistable.getClass().getName(), statisticsKey);
            statisticsPersistable.rescanStatistics();
            return;
        }

        lastVersionByKey.put(statisticsKey, version);
        Statistics statistics = getByKey(statisticsKey);
        if (StatHelper.equalsStatistics(statistics.getValue(),
                statisticsMap)) {
            return;
        }
        Statistics updated = statistics.toBuilder()
                .setValue(statisticsMap)
                .build();
        statisticsByKey.put(statisticsKey, updated);
        statisticsRepository.update(updated);

        logger.debug("Persisted statistics: {}, version: {}",
                statisticsKey, version);
    }

    private Statistics getByKey(String statisticsKey) {
        Statistics statistics = statisticsByKey.get(statisticsKey);
        if (statistics != null) {
            return statistics;
        }
        Statistics queried =
                statisticsRepository.getByKey(statisticsKey);
        if (queried == null) {
            queried = buildNewOne(statisticsKey);
        }
        statisticsByKey.put(statisticsKey, queried);
        return queried;
    }

    private Statistics buildNewOne(String key) {
        Statistics.Builder builder = Statistics.builder()
                .setKey(key)
                .setValue(new ConcurrentHashMap<>());
        long id = statisticsRepository.insert(builder.build());
        return builder.setId(id).build();
    }
}
