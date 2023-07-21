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
import tech.rollw.disk.web.database.dao.StatisticsDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.statistics.Statistics;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author RollW
 */
@Repository
public class StatisticsRepository extends BaseRepository<Statistics> {
    private static final Statistics DUMMY =
            new Statistics(-1L, null, Map.of());

    private final StatisticsDao statisticsDao;

    protected StatisticsRepository(DiskDatabase database,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware,
                                   CacheManager cacheManager) {
        super(database.getStatisticsDao(), pageableContextThreadAware, cacheManager);
        statisticsDao = database.getStatisticsDao();
    }

    @Override
    protected Class<Statistics> getEntityClass() {
        return Statistics.class;
    }

    public Statistics getByKey(String statisticsKey) {
        if (statisticsKey == null) {
            return null;
        }
        Statistics cached = cache.get(statisticsKey, Statistics.class);
        if (cached == DUMMY) {
            return null;
        }
        if (cached != null) {
            return cached;
        }
        Statistics statistics = statisticsDao.getByKey(statisticsKey);
        if (statistics == null) {
            cache.put(statisticsKey, DUMMY);
            return null;
        }
        return cacheResult(statistics);
    }

    @Override
    protected Statistics cacheResult(Statistics statistics) {
        if (statistics == null) {
            return null;
        }
        cache.put(statistics.getKey(), statistics);
        return super.cacheResult(statistics);
    }

    @Override
    protected void invalidateCache(Statistics statistics) {
        super.invalidateCache(statistics);
        if (statistics == null || statistics.getKey() == null) {
            return;
        }
        cache.evict(statistics.getKey());
    }
}
