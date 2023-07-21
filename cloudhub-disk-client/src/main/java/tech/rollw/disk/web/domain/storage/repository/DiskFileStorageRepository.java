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

package tech.rollw.disk.web.domain.storage.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.DiskFileStorageDao;
import tech.rollw.disk.web.domain.storage.DiskFileStorage;
import tech.rollw.disk.web.domain.storage.dto.StorageAsSize;
import tech.rollw.disk.common.data.page.Offset;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Repository
public class DiskFileStorageRepository {
    private final DiskFileStorageDao diskFileStorageDao;
    private final Cache cache;

    public DiskFileStorageRepository(DiskDatabase diskDatabase,
                                     CacheManager cacheManager) {
        diskFileStorageDao = diskDatabase.getDiskFileStorageDao();
        cache = cacheManager.getCache("TB-disk_file_storage");
    }

    public void insert(DiskFileStorage diskFileStorages) {
        diskFileStorageDao.insert(diskFileStorages);
        cacheResult(diskFileStorages);
    }

    public void update(DiskFileStorage diskFileStorages) {
        diskFileStorageDao.update(diskFileStorages);
        cacheResult(diskFileStorages);
    }

    public void delete(DiskFileStorage diskFileStorage) {
        diskFileStorageDao.delete(diskFileStorage);
        cache.evict(diskFileStorage.getFileId());
    }

    public List<DiskFileStorage> get() {
        return cacheResult(diskFileStorageDao.get());
    }

    public List<DiskFileStorage> get(Offset offset) {
        return cacheResult(diskFileStorageDao.get(offset));
    }

    public List<DiskFileStorage> getByIds(List<String> ids) {
        CacheResult cacheResult = findCache(ids);
        if (cacheResult.isAllHit()) {
            return cacheResult.diskFileStorages();
        }
        List<String> missFileIds = cacheResult.missFileIds();
        List<DiskFileStorage> diskFileStorages = diskFileStorageDao.getByIds(missFileIds);
        return cacheResult(diskFileStorages);
    }

    public DiskFileStorage getById(String fileId) {
        DiskFileStorage diskFileStorage = cache.get(fileId, DiskFileStorage.class);
        if (diskFileStorage != null) {
            return diskFileStorage;
        }
        return cacheResult(diskFileStorageDao.getById(fileId));
    }

    public List<StorageAsSize> getSizesByIds(List<String> fileIds) {
        CacheResult cacheResult = findCache(fileIds);
        if (cacheResult.isAllHit()) {
            return cacheResult.diskFileStorages()
                    .stream()
                    .map(StorageAsSize::from)
                    .toList();
        }
        List<String> missFileIds = cacheResult.missFileIds();
        List<DiskFileStorage> diskFileStorages = diskFileStorageDao.getByIds(missFileIds);
        cacheResult(diskFileStorages);

        List<DiskFileStorage> allDiskFileStorages =
                new ArrayList<>(cacheResult.diskFileStorages());
        allDiskFileStorages.addAll(diskFileStorages);
        return allDiskFileStorages
                .stream()
                .map(StorageAsSize::from)
                .toList();
    }

    public long getSizeById(String fileId) {
        final DiskFileStorage diskFileStorage = getById(fileId);
        if (diskFileStorage != null) {
            return diskFileStorage.getFileSize();
        }
        return getById(fileId).getFileSize();
    }

    private DiskFileStorage cacheResult(DiskFileStorage diskFileStorage) {
        if (diskFileStorage == null) {
            return null;
        }

        cache.put(diskFileStorage.getFileId(), diskFileStorage);
        return diskFileStorage;
    }

    private List<DiskFileStorage> cacheResult(List<DiskFileStorage> diskFileStorages) {
        for (DiskFileStorage diskFileStorage : diskFileStorages) {
            cacheResult(diskFileStorage);
        }
        return diskFileStorages;
    }

    private DiskFileStorage findCache(String fileId) {
        return cache.get(fileId, DiskFileStorage.class);
    }

    private CacheResult findCache(List<String> fileIds) {
        List<DiskFileStorage> diskFileStorages = new ArrayList<>();
        List<String> missFileIds = new ArrayList<>();
        for (String fileId : fileIds) {
            DiskFileStorage diskFileStorage = findCache(fileId);
            if (diskFileStorage != null) {
                diskFileStorages.add(diskFileStorage);
            } else {
                missFileIds.add(fileId);
            }
        }
        return new CacheResult(diskFileStorages, missFileIds);
    }

    private record CacheResult(
            List<DiskFileStorage> diskFileStorages,
            List<String> missFileIds) {

        public boolean isAllHit() {
            return missFileIds.isEmpty();
        }
    }
}
