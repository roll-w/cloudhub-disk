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

package tech.rollw.disk.web.controller.storage.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.FileType;
import tech.rollw.disk.web.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record StorageVo(
        long storageId,
        String name,
        StorageType storageType,
        long ownerId,
        LegalUserType ownerType,
        Long parentId,
        FileType fileType,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long size,
        long createTime,
        long updateTime,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String fileId
) {
    public static StorageVo from(AttributedStorage storage) {
        return from(storage, null, null);
    }

    public static StorageVo from(AttributedStorage storage, Long size) {
        return from(storage, size, null);
    }

    public static StorageVo from(AttributedStorage storage,
                                 Long size,
                                 String fileId) {
        if (storage == null) {
            return null;
        }

        return new StorageVo(
                storage.getStorageId(),
                storage.getName(),
                storage.getStorageType(),
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getParentId(),
                storage.getFileType(),
                size,
                storage.getCreateTime(),
                storage.getUpdateTime(),
                fileId
        );
    }
}
