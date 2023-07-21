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
import tech.rollw.disk.web.domain.userstorage.Storage;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.UserFolder;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record FolderInfo(
        long id,
        String name,
        long parentId,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long createTime,
        long updateTime,
        boolean deleted
) implements Storage {
    public static final FolderInfo ROOT =
            FolderInfo.of(UserFolder.ROOT_FOLDER);

    @Override
    public long getStorageId() {
        return id();
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return StorageType.FOLDER;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Long getParentId() {
        return parentId();
    }

    @Override
    public long getOwnerId() {
        return ownerId();
    }

    @NonNull
    @Override
    public LegalUserType getOwnerType() {
        return ownerType();
    }

    public static FolderInfo of(UserFolder userFolder) {
        return new FolderInfo(
                userFolder.getId(),
                userFolder.getName(),
                userFolder.getParentId(),
                userFolder.getOwnerId(),
                userFolder.getOwnerType(),
                userFolder.getCreateTime(),
                userFolder.getUpdateTime(),
                userFolder.isDeleted()
        );
    }

    public static FolderInfo of(FolderStructureInfo folderStructureInfo) {
        return new FolderInfo(
                folderStructureInfo.storageId(),
                folderStructureInfo.name(),
                folderStructureInfo.parentId(),
                folderStructureInfo.ownerId(),
                folderStructureInfo.ownerType(),
                folderStructureInfo.createTime(),
                folderStructureInfo.updateTime(),
                folderStructureInfo.deleted()
        );
    }
}
