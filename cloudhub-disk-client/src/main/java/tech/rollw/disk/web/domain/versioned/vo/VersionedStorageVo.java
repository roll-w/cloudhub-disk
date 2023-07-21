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

package tech.rollw.disk.web.domain.versioned.vo;

import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.versioned.VersionedFileStorage;

/**
 * @author RollW
 */
public record VersionedStorageVo(
        long id,
        long version,
        long storageId,
        StorageType storageType,
        long operatorId,
        String username,
        long createTime
) {

    public static VersionedStorageVo of(VersionedFileStorage versionedFileStorage) {
        if (versionedFileStorage == null) {
            return null;
        }
        return new VersionedStorageVo(
                versionedFileStorage.getId(),
                versionedFileStorage.getVersion(),
                versionedFileStorage.getStorageId(),
                versionedFileStorage.getStorageType(),
                versionedFileStorage.getOperator(),
                null,
                versionedFileStorage.getCreateTime()
        );
    }

    public static VersionedStorageVo of(VersionedFileStorage versionedFileStorage,
                                        AttributedUser attributedUser) {
        if (versionedFileStorage == null) {
            return null;
        }
        return new VersionedStorageVo(
                versionedFileStorage.getId(),
                versionedFileStorage.getVersion(),
                versionedFileStorage.getStorageId(),
                versionedFileStorage.getStorageType(),
                versionedFileStorage.getOperator(),
                attributedUser.getUsername(),
                versionedFileStorage.getCreateTime()
        );
    }
}
