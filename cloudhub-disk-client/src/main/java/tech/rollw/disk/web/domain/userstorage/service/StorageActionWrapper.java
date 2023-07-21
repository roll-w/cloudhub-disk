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

import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.FileType;
import tech.rollw.disk.web.domain.userstorage.StorageAction;
import tech.rollw.disk.web.domain.userstorage.StorageEventListener;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.common.StorageException;
import tech.rollw.disk.common.BusinessRuntimeException;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class StorageActionWrapper implements StorageAction {
    private final StorageAction wrapped;
    private final StorageEventListener storageEventListener;

    public StorageActionWrapper(StorageAction storageAction,
                                StorageEventListener storageEventListener) {
        this.wrapped = storageAction;
        this.storageEventListener = storageEventListener;
    }

    public StorageAction getWrapped() {
        return wrapped;
    }

    @Override
    public long getStorageId() {
        return wrapped.getStorageId();
    }

    @Override
    @NonNull
    public StorageType getStorageType() {
        return wrapped.getStorageType();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Long getParentId() {
        return wrapped.getParentId();
    }

    @Override
    public long getOwnerId() {
        return wrapped.getOwnerId();
    }

    @Override
    @NonNull
    public LegalUserType getOwnerType() {
        return wrapped.getOwnerType();
    }

    @Override
    public FileType getFileType() {
        return wrapped.getFileType();
    }

    @Override
    public long getCreateTime() {
        return wrapped.getCreateTime();
    }

    @Override
    public long getUpdateTime() {
        return wrapped.getUpdateTime();
    }

    @Override
    public boolean isDeleted() {
        return wrapped.isDeleted();
    }

    @Override
    public StorageAction update() throws BusinessRuntimeException {
        return wrapped.update();
    }

    @Override
    public StorageAction delete() throws StorageException {
        return wrapped.delete();
    }

    @Override
    public void restore() throws StorageException {
        wrapped.restore();
    }

    @Override
    public void create() throws StorageException {
        wrapped.create();
    }

    @Override
    public StorageAction rename(String newName) throws StorageException {
        return wrapped.rename(newName);
    }

    @Override
    public StorageAction getSystemResource() {
        return wrapped.getSystemResource();
    }

    @Override
    public void move(long newParentId) throws StorageException {
        wrapped.move(newParentId);
    }

    @Override
    public StorageAction copy(long newParentId) throws StorageException {
        return wrapped.copy(newParentId);
    }

    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        wrapped.setCheckDeleted(checkDeleted);
    }

    @Override
    public boolean isCheckDeleted() {
        return wrapped.isCheckDeleted();
    }
}
