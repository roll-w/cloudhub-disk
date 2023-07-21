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

package tech.rollw.disk.web.domain.storagepermission.dto;

import org.checkerframework.checker.nullness.qual.Nullable;
import tech.rollw.disk.web.domain.storagepermission.PermissionType;
import tech.rollw.disk.web.domain.storagepermission.PublicPermissionType;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.Storage;
import tech.rollw.disk.web.domain.userstorage.StorageType;

import java.util.List;

/**
 * @author RollW
 */
public record StoragePermissionDto(
        long ownerId,
        LegalUserType ownerType,
        long storageId,
        StorageType storageType,
        long operator,
        List<PermissionType> permissions,

        @Nullable
        PublicPermissionType publicPermissionType
) {
    public boolean allowRead() {
        if (operator == ownerId) {
            return true;
        }

        if (denied()) {
            return false;
        }

        boolean userRead = permissions.contains(PermissionType.READ);
        if (userRead) {
            return true;
        }
        return publicPermissionType != null && publicPermissionType.isRead();
    }

    public boolean allowWrite() {
        if (operator == ownerId) {
            return true;
        }
        if (denied()) {
            return false;
        }

        boolean userWrite = permissions.contains(PermissionType.WRITE);
        if (userWrite) {
            return true;
        }
        return publicPermissionType != null && publicPermissionType.isWrite();
    }

    public boolean denied() {
        if (operator == ownerId) {
            return false;
        }

        return permissions.contains(PermissionType.DENIED);
    }

    public static StoragePermissionDto of(Storage storage, long operator,
                                          List<PermissionType> permissions) {
        return new StoragePermissionDto(
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getStorageId(),
                storage.getStorageType(),
                operator,
                permissions,
                null
        );
    }

    public static StoragePermissionDto of(Storage storage, long operator,
                                          List<PermissionType> permissions,
                                          PublicPermissionType publicPermissionType) {
        return new StoragePermissionDto(
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getStorageId(),
                storage.getStorageType(),
                operator,
                permissions,
                publicPermissionType
        );
    }
}
