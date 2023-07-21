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

import tech.rollw.disk.web.domain.usergroup.UserGroup;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserGroupDao extends AutoPrimaryBaseDao<UserGroup> {
    @Override
    @Query("SELECT * FROM user_group WHERE deleted = 0")
    List<UserGroup> getActives();

    @Override
    @Query("SELECT * FROM user_group WHERE deleted = 1")
    List<UserGroup> getInactives();

    @Override
    @Query("SELECT * FROM user_group WHERE id = {id}")
    UserGroup getById(long id);

    @Override
    @Query("SELECT * FROM user_group WHERE id IN ({ids})")
    List<UserGroup> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_group WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_group WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_group")
    List<UserGroup> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_group")
    int count();

    @Override
    @Query("SELECT * FROM user_group LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserGroup> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_group";
    }

    @Query("SELECT * FROM user_group WHERE name = {name} LIMIT 1")
    UserGroup getByName(String name);
}
