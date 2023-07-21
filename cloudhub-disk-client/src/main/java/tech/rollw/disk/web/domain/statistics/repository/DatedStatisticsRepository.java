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

package tech.rollw.disk.web.domain.statistics.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.DatedStatisticsDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.statistics.DatedStatistics;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author RollW
 */
@Repository
public class DatedStatisticsRepository extends BaseRepository<DatedStatistics> {
    private final DatedStatisticsDao datedStatisticsDao;

    public DatedStatisticsRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getDatedStatisticsDao(), pageableContextThreadAware, cacheManager);
        datedStatisticsDao = database.getDatedStatisticsDao();
    }

    @Override
    protected Class<DatedStatistics> getEntityClass() {
        return DatedStatistics.class;
    }

    public DatedStatistics getByKeyAndDate(String statisticsKey,
                                           LocalDate date) {
        return cacheResult(
                datedStatisticsDao.getByKeyAndDate(statisticsKey, date)
        );
    }

    public DatedStatistics getLatestOfKey(String statisticsKey) {
        return cacheResult(
                datedStatisticsDao.getLatestOfKey(statisticsKey)
        );
    }

    public DatedStatistics getLatestOfKey(String statisticsKey, LocalDate date) {
        return cacheResult(
                datedStatisticsDao.getLatestOfKey(statisticsKey, date)
        );
    }

    public List<DatedStatistics> getByKeyAndDateBetween(String statisticsKey,
                                                        LocalDate from,
                                                        LocalDate to) {
        return cacheResult(
                datedStatisticsDao.getBetweenDate(statisticsKey, from, to)
        );
    }
}
