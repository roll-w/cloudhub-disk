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

import tech.rollw.disk.web.domain.versioned.VersionedFileStorage;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface VersionedFileStorageDao extends AutoPrimaryBaseDao<VersionedFileStorage> {

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} ORDER BY version DESC LIMIT 1")
    VersionedFileStorage getLatestFileVersion(long storageId);

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} AND deleted = 0 ORDER BY version DESC")
    List<VersionedFileStorage> getFileVersions(long storageId);

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} ORDER BY version DESC")
    List<VersionedFileStorage> getFileVersionsIncludeDelete(long storageId);

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} AND version = {version}")
    VersionedFileStorage getFileVersion(long storageId, long version);

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE deleted = 0")
    List<VersionedFileStorage> getActives();

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE deleted = 1")
    List<VersionedFileStorage> getInactives();

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE id = {id}")
    VersionedFileStorage getById(long id);

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE id IN ({ids})")
    List<VersionedFileStorage> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM versioned_file_storage WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM versioned_file_storage WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM versioned_file_storage")
    List<VersionedFileStorage> get();

    @Override
    @Query("SELECT COUNT(*) FROM versioned_file_storage")
    int count();

    @Override
    @Query("SELECT * FROM versioned_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<VersionedFileStorage> get(Offset offset);

    @Override
    default String getTableName() {
        return "versioned_file_storage";
    }
}
