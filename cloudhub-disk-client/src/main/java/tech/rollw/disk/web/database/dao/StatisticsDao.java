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

import tech.rollw.disk.web.domain.statistics.Statistics;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StatisticsDao extends AutoPrimaryBaseDao<Statistics> {
    @Override
    @Query("SELECT * FROM statistics WHERE deleted = 0")
    List<Statistics> getActives();

    @Override
    @Query("SELECT * FROM statistics WHERE deleted = 1")
    List<Statistics> getInactives();

    @Override
    @Query("SELECT * FROM statistics WHERE id = {id}")
    Statistics getById(long id);

    @Override
    @Query("SELECT * FROM statistics WHERE id IN ({ids})")
    List<Statistics> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM statistics WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM statistics WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM statistics ORDER BY id DESC")
    List<Statistics> get();

    @Override
    @Query("SELECT COUNT(*) FROM statistics")
    int count();

    @Override
    @Query("SELECT * FROM statistics ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<Statistics> get(Offset offset);

    @Override
    default String getTableName() {
        return "statistics";
    }

    @Query("SELECT * FROM statistics WHERE `key` = {statisticsKey}")
    Statistics getByKey(String statisticsKey);
}
