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

import tech.rollw.disk.web.domain.share.UserShare;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserShareDao extends AutoPrimaryBaseDao<UserShare> {

    @Override
    @Query("SELECT * FROM user_share WHERE deleted = 0")
    List<UserShare> getActives();

    @Override
    @Query("SELECT * FROM user_share WHERE deleted = 1")
    List<UserShare> getInactives();

    @Override
    @Query("SELECT * FROM user_share WHERE id = {id}")
    UserShare getById(long id);

    @Override
    @Query("SELECT * FROM user_share WHERE id IN ({ids})")
    List<UserShare> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_share WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_share WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_share")
    List<UserShare> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_share")
    int count();

    @Override
    @Query("SELECT * FROM user_share ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserShare> get(Offset offset);

    @Query("SELECT * FROM user_share WHERE share_id = {shareId}")
    UserShare getByShareId(String shareId);

    @Query("SELECT * FROM user_share WHERE user_id = {userId} ORDER BY id DESC")
    List<UserShare> getByUserId(long userId);

    @Query("SELECT * FROM user_share WHERE user_id = {userId} ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()} ")
    List<UserShare> getByUserId(long userId, Offset offset);

    @Query("SELECT * FROM user_share WHERE storage_id = {storageId} AND storage_type = {storageType} ORDER BY id DESC")
    List<UserShare> getByStorage(long storageId, StorageType storageType);

    @Query("SELECT * FROM user_share WHERE storage_id = {storageId} AND storage_type = {storageType} ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()} ")
    List<UserShare> getByStorage(long storageId, StorageType storageType, Offset offset);

    @Override
    default String getTableName() {
        return "user_share";
    }
}
