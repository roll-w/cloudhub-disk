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

package tech.rollw.disk.web.controller;

import tech.rollw.disk.web.common.ParameterFailedException;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageOwner;

/**
 * @author RollW
 */
public final class ParameterHelper {

    public static StorageOwner buildStorageOwner(long ownerId, String ownerType) {
        LegalUserType legalUserType = LegalUserType.from(ownerType);
        if (legalUserType == null) {
            throw new ParameterFailedException("{0}", "ownerType is not valid");
        }
        return new SimpleStorageOwner(ownerId, legalUserType);
    }

    public static StorageIdentity buildStorageIdentity(long storageId, String storageType) {
        StorageType type = StorageType.from(storageType);
        if (type == null) {
            throw new ParameterFailedException("{0}", "storageType is not valid");
        }
        return new SimpleStorageIdentity(storageId, type);
    }

    private ParameterHelper() {
    }
}
