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

package tech.rollw.disk.web.domain.storagepermission.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.StoragePermissionDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.storagepermission.StoragePermission;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class StoragePermissionRepository extends BaseRepository<StoragePermission> {
    private final StoragePermissionDao storagePermissionDao;

    public StoragePermissionRepository(DiskDatabase database,
                                       ContextThreadAware<PageableContext> pageableContextThreadAware,
                                       CacheManager cacheManager) {
        super(database.getStoragePermissionDao(), pageableContextThreadAware, cacheManager);
        storagePermissionDao = database.getStoragePermissionDao();
    }

    public StoragePermission getStoragePermission(long storageId,
                                                  StorageType storageType) {
        StoragePermission storagePermission =
                storagePermissionDao.getStoragePermission(storageId, storageType);
        return cacheResult(storagePermission);
    }

    @Override
    protected Class<StoragePermission> getEntityClass() {
        return StoragePermission.class;
    }
}
