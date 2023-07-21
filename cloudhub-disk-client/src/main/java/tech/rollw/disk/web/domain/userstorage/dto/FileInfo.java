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

package tech.rollw.disk.web.domain.userstorage.dto;

import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.FileType;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.UserFileStorage;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record FileInfo(
        long storageId,
        String name,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long parentId,
        String fileId,
        FileType fileType,
        String mimeType,
        long createTime,
        long updateTime,
        boolean deleted
) implements AttributedStorage {
    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return StorageType.FILE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public long getOwnerId() {
        return ownerId;
    }

    @NonNull
    @Override
    public LegalUserType getOwnerType() {
        return ownerType;
    }

    public String getFileId() {
        return fileId;
    }

    public static FileInfo from(UserFileStorage userFileStorage) {
        return new FileInfo(
                userFileStorage.getStorageId(),
                userFileStorage.getName(),
                userFileStorage.getOwnerId(),
                userFileStorage.getOwnerType(),
                userFileStorage.getParentId(),
                userFileStorage.getFileId(),
                userFileStorage.getFileType(),
                userFileStorage.getMimeType(),
                userFileStorage.getCreateTime(),
                userFileStorage.getUpdateTime(),
                userFileStorage.isDeleted()
        );
    }

}
