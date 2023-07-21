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

import tech.rollw.disk.web.domain.favorites.FavoriteGroup;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface FavoriteGroupDao extends AutoPrimaryBaseDao<FavoriteGroup> {
    @Override
    @Query("SELECT * FROM favorite_group WHERE deleted = 0")
    List<FavoriteGroup> getActives();

    @Override
    @Query("SELECT * FROM favorite_group WHERE deleted = 1")
    List<FavoriteGroup> getInactives();

    @Override
    @Query("SELECT * FROM favorite_group WHERE id = {id}")
    FavoriteGroup getById(long id);

    @Override
    @Query("SELECT * FROM favorite_group WHERE id IN ({ids})")
    List<FavoriteGroup> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM favorite_group WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_group WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM favorite_group ORDER BY id DESC")
    List<FavoriteGroup> get();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_group")
    int count();

    @Override
    @Query("SELECT * FROM favorite_group ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<FavoriteGroup> get(Offset offset);

    @Override
    default String getTableName() {
        return "favorite_group";
    }

    @Query("SELECT * FROM favorite_group WHERE name = {name} AND user_id = {operator.getOperatorId()} ")
    FavoriteGroup getByName(String name, Operator operator);

    @Query("SELECT * FROM favorite_group WHERE user_id = {operator.getOperatorId()}")
    List<FavoriteGroup> getGroupsOf(Operator operator);
}
