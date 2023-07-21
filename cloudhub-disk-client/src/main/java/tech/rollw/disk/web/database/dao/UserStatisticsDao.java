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

import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstats.UserStatistics;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserStatisticsDao extends AutoPrimaryBaseDao<UserStatistics> {
    @Override
    @Query("SELECT * FROM user_statistics")
    List<UserStatistics> getActives();

    @Override
    @Query("SELECT * FROM user_statistics")
    List<UserStatistics> getInactives();

    @Override
    @Query("SELECT * FROM user_statistics WHERE id = {id}")
    UserStatistics getById(long id);

    @Override
    @Query("SELECT * FROM user_statistics WHERE id IN ({ids})")
    List<UserStatistics> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_statistics")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_statistics")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_statistics ORDER BY id DESC")
    List<UserStatistics> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_statistics")
    int count();

    @Override
    @Query("SELECT * FROM user_statistics ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserStatistics> get(Offset offset);

    @Query("SELECT * FROM user_statistics WHERE user_id = {userId} AND user_type = {userType}")
    UserStatistics getByUserId(long userId, LegalUserType userType);

    @Override
    default String getTableName() {
        return "user_statistics";
    }

    @Query("SELECT * FROM user_statistics WHERE user_id = {ownerId} AND user_type = {ownerType}")
    UserStatistics getByOwnerIdAndOwnerType(long ownerId, LegalUserType ownerType);
}
