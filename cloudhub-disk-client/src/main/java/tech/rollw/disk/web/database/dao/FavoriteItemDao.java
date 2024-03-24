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

import tech.rollw.disk.web.domain.favorites.FavoriteItem;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface FavoriteItemDao extends AutoPrimaryBaseDao<FavoriteItem> {
    @Override
    @Query("SELECT * FROM favorite_item WHERE deleted = 0")
    List<FavoriteItem> getActives();

    @Override
    @Query("SELECT * FROM favorite_item WHERE deleted = 1")
    List<FavoriteItem> getInactives();

    @Override
    @Query("SELECT * FROM favorite_item WHERE id = {id}")
    FavoriteItem getById(long id);

    @Override
    @Query("SELECT * FROM favorite_item WHERE id IN ({ids})")
    List<FavoriteItem> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM favorite_item WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_item WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM favorite_item ORDER BY id DESC")
    List<FavoriteItem> get();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_item")
    int count();

    @Override
    @Query("SELECT * FROM favorite_item ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<FavoriteItem> get(Offset offset);

    @Override
    default String getTableName() {
        return "favorite_item";
    }

    @Query("SELECT * FROM favorite_item WHERE group_id = {groupId} AND user_id = {userId}")
    List<FavoriteItem> getByGroup(long groupId, long userId);

    @Query("SELECT * FROM favorite_item WHERE group_id = {groupId}")
    List<FavoriteItem> getByGroup(long groupId);

    @Query("SELECT * FROM favorite_item WHERE group_id = {groupId} " +
            "AND storage_type = {storageIdentity.getStorageType()} " +
            "AND storage_id = {storageIdentity.getStorageId()}")
    FavoriteItem getByGroupAndIdentity(long groupId, StorageIdentity storageIdentity);
}

