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

package tech.rollw.disk.web.domain.userstorage.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageEventListener;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.dto.FileAttributesInfo;
import tech.rollw.disk.web.domain.userstorage.dto.StorageAttr;
import tech.rollw.disk.common.CommonErrorCode;
import tech.rollw.disk.common.ErrorCode;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public class CompositeStorageEventListener implements StorageEventListener {
    private final List<StorageEventListener> storageEventListeners;

    public CompositeStorageEventListener(List<StorageEventListener> storageEventListeners) {
        this.storageEventListeners = storageEventListeners;
    }

    @Override
    public ErrorCode onBeforeStorageCreated(@NonNull StorageOwner storageOwner, @NonNull Operator operator, FileAttributesInfo fileAttributesInfo) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            ErrorCode errorCode = storageEventListener.onBeforeStorageCreated(storageOwner, operator, fileAttributesInfo);
            if (errorCode.failed()) {
                return errorCode;
            }
        }
        return CommonErrorCode.SUCCESS;
    }

    @Override
    public void onStorageCreated(@NonNull AttributedStorage storage, StorageAttr storageAttr) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            storageEventListener.onStorageCreated(storage, storageAttr);
        }
    }

    @Override
    public void onStorageProcess(AttributedStorage storage, @Nullable StorageAttr storageAttr) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            storageEventListener.onStorageProcess(storage, storageAttr);
        }
    }

    @Override
    public void onStorageDeleted(@NonNull AttributedStorage storage, @Nullable FileAttributesInfo fileAttributesInfo) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            storageEventListener.onStorageDeleted(storage, fileAttributesInfo);
        }
    }
}
