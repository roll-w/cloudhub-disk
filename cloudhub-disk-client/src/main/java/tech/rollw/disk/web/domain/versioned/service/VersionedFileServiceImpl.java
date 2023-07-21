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

package tech.rollw.disk.web.domain.versioned.service;

import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageEventListener;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.dto.StorageAttr;
import tech.rollw.disk.web.domain.versioned.VersionedFileService;
import tech.rollw.disk.web.domain.versioned.VersionedFileStorage;
import tech.rollw.disk.web.domain.versioned.repository.VersionedFileRepository;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class VersionedFileServiceImpl implements VersionedFileService,
        StorageEventListener {
    private final VersionedFileRepository versionedFileRepository;

    public VersionedFileServiceImpl(VersionedFileRepository versionedFileRepository) {
        this.versionedFileRepository = versionedFileRepository;
    }

    @Override
    public void onStorageCreated(@NonNull AttributedStorage storage, StorageAttr storageAttr) {
        if (storage.getStorageType() != StorageType.FILE) {
            return;
        }
        VersionedFileStorage versionedFileStorage =
                versionedFileRepository.getLatestFileVersion(storage.getStorageId());
        if (versionedFileStorage == null) {
            VersionedFileStorage newVersionedFileStorage = VersionedFileStorage.builder()
                    .setFileId(storageAttr.fileId())
                    .setVersion(1)
                    .setOperator(storageAttr.operator().getOperatorId())
                    .setStorageId(storage.getStorageId())
                    .setStorageType(storage.getStorageType())
                    .setCreateTime(System.currentTimeMillis())
                    .setDeleted(false)
                    .build();
            versionedFileRepository.insert(newVersionedFileStorage);
            return;
        }
        long version = versionedFileStorage.getVersion() + 1;
        VersionedFileStorage newVersionedFileStorage = VersionedFileStorage.builder()
                .setFileId(versionedFileStorage.getFileId())
                .setVersion(version)
                .setStorageId(storage.getStorageId())
                .setStorageType(storage.getStorageType())
                .setOperator(storageAttr.operator().getOperatorId())
                .setCreateTime(System.currentTimeMillis())
                .setDeleted(false)
                .build();
        versionedFileRepository.insert(newVersionedFileStorage);
    }

    @Override
    public VersionedFileStorage getVersionedFileStorage(
            long versionedFileStorageId) {
        VersionedFileStorage storage =
                versionedFileRepository.getById(versionedFileStorageId);
        if (storage == null || storage.isDeleted()) {
            return null;
        }
        return storage;
    }

    @Override
    public List<VersionedFileStorage> getVersionedFileStorages(
            long fileStorageId) {
        return versionedFileRepository.getFileVersions(fileStorageId);
    }

    @Override
    public void deleteVersionedFileStorage(long versionedFileStorageId) {
        VersionedFileStorage versionedFileStorage =
                versionedFileRepository.getById(versionedFileStorageId);
        if (versionedFileStorage == null || versionedFileStorage.isDeleted()) {
            return;
        }
        VersionedFileStorage updated = versionedFileStorage
                .toBuilder()
                .setDeleted(true)
                .build();
        versionedFileRepository.update(updated);
    }

    @Override
    public void deleteVersionedFileStorage(long storageId, long version) {
        VersionedFileStorage versionedFileStorage =
                versionedFileRepository.getFileVersion(storageId, version);
        if (versionedFileStorage == null || versionedFileStorage.isDeleted()) {
            return;
        }
        VersionedFileStorage updated = versionedFileStorage
                .toBuilder()
                .setDeleted(true)
                .build();
        versionedFileRepository.update(updated);
    }
}
