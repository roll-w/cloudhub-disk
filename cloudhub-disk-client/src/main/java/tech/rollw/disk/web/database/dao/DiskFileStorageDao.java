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

import tech.rollw.disk.web.domain.storage.DiskFileStorage;
import tech.rollw.disk.web.domain.storage.dto.StorageAsSize;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface DiskFileStorageDao extends BaseDao<DiskFileStorage> {
    @Override
    @Query("SELECT * FROM disk_file_storage")
    List<DiskFileStorage> get();

    @Override
    @Query("SELECT * FROM disk_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<DiskFileStorage> get(Offset offset);

    @Query("SELECT * FROM disk_file_storage WHERE file_id = {fileId}")
    DiskFileStorage getById(String fileId);

    @Query("SELECT `file_id`, `size` FROM disk_file_storage WHERE file_id IN {fileIds}")
    List<StorageAsSize> getSizesByIds(List<String> fileIds);

    @Query("SELECT `size` FROM disk_file_storage WHERE file_id = {fileId}")
    long getSizeById(String fileId);

    @Override
    @Query("SELECT COUNT(*) FROM disk_file_storage")
    int count();

    @Override
    default String getTableName() {
        return "disk_file_storage";
    }

    @Query("SELECT * FROM disk_file_storage WHERE file_id IN ({ids})")
    List<DiskFileStorage> getByIds(List<String> ids);
}
