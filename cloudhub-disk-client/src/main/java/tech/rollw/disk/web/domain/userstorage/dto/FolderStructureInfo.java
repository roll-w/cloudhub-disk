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
import tech.rollw.disk.web.domain.userstorage.UserFolder;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public record FolderStructureInfo(
        long storageId,
        String name,
        Long parentId,
        List<FolderInfo> parents,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long createTime,
        long updateTime,
        boolean deleted
) implements AttributedStorage {
    public static final FolderStructureInfo ROOT_FOLDER =
            FolderStructureInfo.of(UserFolder.ROOT_FOLDER, List.of());

    public List<FolderInfo> getParents() {
        return parents;
    }

    @Override
    public FileType getFileType() {
        return FileType.OTHER;
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
        return StorageType.FOLDER;
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

    public static FolderStructureInfo of(UserFolder userFolder,
                                         List<FolderInfo> parents) {
        return new FolderStructureInfo(
                userFolder.getStorageId(),
                userFolder.getName(),
                userFolder.getParentId(),
                parents,
                userFolder.getOwnerId(),
                userFolder.getOwnerType(),
                userFolder.getCreateTime(),
                userFolder.getUpdateTime(),
                userFolder.isDeleted()
        );
    }
}
