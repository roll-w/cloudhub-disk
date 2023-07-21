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

import tech.rollw.disk.web.domain.storagepermission.StoragePermission;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StoragePermissionDao extends AutoPrimaryBaseDao<StoragePermission> {
    @Query("SELECT * FROM storage_permission WHERE storage_id = {storageId} AND storage_type = {storageType}")
    StoragePermission getStoragePermission(long storageId, StorageType storageType);

    @Override
    @Query("SELECT * FROM storage_permission WHERE deleted = 0")
    List<StoragePermission> getActives();

    @Override
    @Query("SELECT * FROM storage_permission WHERE deleted = 1")
    List<StoragePermission> getInactives();

    @Override
    @Query("SELECT * FROM storage_permission WHERE id = {id}")
    StoragePermission getById(long id);

    @Override
    @Query("SELECT * FROM storage_permission WHERE id IN ({ids})")
    List<StoragePermission> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM storage_permission WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM storage_permission WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM storage_permission")
    List<StoragePermission> get();

    @Override
    @Query("SELECT COUNT(*) FROM storage_permission")
    int count();

    @Override
    @Query("SELECT * FROM storage_permission LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<StoragePermission> get(Offset offset);

    @Override
    default String getTableName() {
        return "storage_permission";
    }

}
