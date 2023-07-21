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

import tech.rollw.disk.web.domain.statistics.*;
import tech.rollw.disk.web.domain.statistics.repository.DatedStatisticsRepository;
import tech.rollw.disk.web.domain.statistics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author RollW
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final List<StatisticJobTask> statisticJobTasks;

    private final DatedStatisticsRepository datedStatisticsRepository;
    private final StatisticsRepository statisticsRepository;

    public StatisticsServiceImpl(List<StatisticJobTask> statisticJobTasks,
                                 DatedStatisticsRepository datedStatisticsRepository, StatisticsRepository statisticsRepository) {
        this.statisticJobTasks = statisticJobTasks;
        this.datedStatisticsRepository = datedStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
    }

    private StatisticJobTask findByStatisticsKey(String statisticsKey) {
        return statisticJobTasks.stream()
                .filter(statisticJobTask -> statisticJobTask.getStatisticsKeys().contains(statisticsKey))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No such statistics key: " + statisticsKey));
    }

    @Override
    public Map<String, Object> getStatistics(String statisticsKey) {
        StatisticJobTask statisticJobTask =
                findByStatisticsKey(statisticsKey);
        Statistics statistics = statisticsRepository
                .getByKey(statisticsKey);
        if (statistics == null) {
            return null;
        }
        return statisticJobTask.getStatistics(statistics.getKey(), statistics.getValue());
    }

    @Override
    public DatedData getStatistics(String statisticsKey,
                                   LocalDate date) {
        StatisticJobTask statisticJobTask =
                findByStatisticsKey(statisticsKey);
        DatedStatistics datedStatistics =
                datedStatisticsRepository.getByKeyAndDate(statisticsKey, date);
        if (datedStatistics != null) {
            return new DatedData(
                    statisticJobTask.getStatistics(statisticsKey, datedStatistics.getValue()),
                    datedStatistics.getDate()
            );
        }
        DatedStatistics latestDatedStatistics =
                datedStatisticsRepository.getLatestOfKey(statisticsKey, date);
        if (latestDatedStatistics != null) {
            return new DatedData(
                    statisticJobTask.getStatistics(statisticsKey, latestDatedStatistics.getValue()),
                    date
            );
        }
        return null;
    }

    @Override
    public List<DatedData> getStatistics(String statisticsKey,
                                         LocalDate from,
                                         LocalDate to) {
        List<DatedStatistics> datedStatisticsList =
                datedStatisticsRepository.getByKeyAndDateBetween(statisticsKey, from, to);
        if (!datedStatisticsList.isEmpty()) {
            return datedStatisticsList.stream()
                    .map(datedStatistics ->
                            getDatedData(statisticsKey, datedStatistics))
                    .toList();
        }
        DatedStatistics latestDatedStatistics =
                datedStatisticsRepository.getLatestOfKey(statisticsKey);

        return Stream.of(latestDatedStatistics)
                .map(datedStatistics ->
                        getDatedData(statisticsKey, datedStatistics))
                .toList();
    }

    @NonNull
    private DatedData getDatedData(String statisticsKey, DatedStatistics datedStatistics) {
        StatisticJobTask statisticJobTask =
                findByStatisticsKey(statisticsKey);
        return new DatedData(
                statisticJobTask.getStatistics(statisticsKey, datedStatistics.getValue()),
                datedStatistics.getDate()
        );
    }
}
