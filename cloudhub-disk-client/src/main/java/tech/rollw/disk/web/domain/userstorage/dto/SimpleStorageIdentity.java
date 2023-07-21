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

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record SimpleStorageIdentity(
        long storageId,
        StorageType storageType
) implements StorageIdentity {
    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    public static SimpleStorageIdentity of(long storageId, StorageType storageType) {
        return new SimpleStorageIdentity(storageId, storageType);
    }

    public static SimpleStorageIdentity of(SystemResource systemResource) {
        return new SimpleStorageIdentity(
                systemResource.getResourceId(),
                StorageType.from(systemResource.getSystemResourceKind())
        );
    }
}
