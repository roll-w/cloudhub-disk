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

package tech.rollw.disk.web.database.dao;

import tech.rollw.disk.web.domain.statistics.DatedStatistics;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface DatedStatisticsDao extends AutoPrimaryBaseDao<DatedStatistics> {
    @Override
    @Query("SELECT * FROM dated_statistics WHERE deleted = 0")
    List<DatedStatistics> getActives();

    @Override
    @Query("SELECT * FROM dated_statistics WHERE deleted = 1")
    List<DatedStatistics> getInactives();

    @Override
    @Query("SELECT * FROM dated_statistics WHERE id = {id}")
    DatedStatistics getById(long id);

    @Override
    @Query("SELECT * FROM dated_statistics WHERE id IN ({ids})")
    List<DatedStatistics> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM dated_statistics WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM dated_statistics WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM dated_statistics ORDER BY id DESC")
    List<DatedStatistics> get();

    @Override
    @Query("SELECT COUNT(*) FROM dated_statistics")
    int count();

    @Override
    @Query("SELECT * FROM dated_statistics ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<DatedStatistics> get(Offset offset);

    @Override
    default String getTableName() {
        return "dated_statistics";
    }

    @Query("SELECT * FROM dated_statistics WHERE `key` = {statisticsKey} ORDER BY id DESC LIMIT 1")
    DatedStatistics getLatestOfKey(String statisticsKey);

    @Query("SELECT * FROM dated_statistics WHERE `key` = {statisticsKey} AND `date` <= {date} ORDER BY id DESC LIMIT 1")
    DatedStatistics getLatestOfKey(String statisticsKey, LocalDate date);

    @Query("SELECT * FROM dated_statistics WHERE `key` = {statisticsKey} AND `date` = {date}")
    DatedStatistics getByKeyAndDate(String statisticsKey,
                                    LocalDate date);

    @Query("SELECT * FROM dated_statistics WHERE `key` = {statisticsKey} AND `date` >= {startDate} AND `date` <= {endDate}")
    List<DatedStatistics> getBetweenDate(String statisticsKey,
                                         LocalDate startDate,
                                         LocalDate endDate);
}

