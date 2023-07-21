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

package tech.rollw.disk.web.domain.userstorage.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.StorageMetadataDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.tag.TaggedValue;
import tech.rollw.disk.web.domain.userstorage.StorageMetadata;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class StorageMetadataRepository extends BaseRepository<StorageMetadata> {
    private final StorageMetadataDao storageMetadataDao;

    public StorageMetadataRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getStorageMetadataDao(), pageableContextThreadAware, cacheManager);
        storageMetadataDao = database.getStorageMetadataDao();
    }

    public List<StorageMetadata> getByStorageId(long storageId) {
        List<StorageMetadata> storageMetadata =
                storageMetadataDao.getByStorageId(storageId);
        return cacheResult(storageMetadata);
    }

    public StorageMetadata getByStorageIdAndTagGroupId(long storageId, long tagGroupId) {
        StorageMetadata storageMetadata =
                storageMetadataDao.getByStorageIdAndTagGroupId(storageId, tagGroupId);
        return cacheResult(storageMetadata);
    }

    public List<StorageMetadata> getByTagId(long tagId) {
        List<StorageMetadata> storageMetadata =
                storageMetadataDao.getByTagId(tagId);
        return cacheResult(storageMetadata);
    }

    public List<StorageMetadata> getByTagValues(List<? extends TaggedValue> taggedValues) {
        List<StorageMetadata> storageMetadata =
                storageMetadataDao.getByTagValues(taggedValues);
        return cacheResult(storageMetadata);
    }

    public StorageMetadata getByStorageIdAndName(long storageId, String name) {
        StorageMetadata storageMetadata =
                storageMetadataDao.getByStorageIdAndName(storageId, name);
        return cacheResult(storageMetadata);
    }

    @Override
    protected Class<StorageMetadata> getEntityClass() {
        return StorageMetadata.class;
    }
}
