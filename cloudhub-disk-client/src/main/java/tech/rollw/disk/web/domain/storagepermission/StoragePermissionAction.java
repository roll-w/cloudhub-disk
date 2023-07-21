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

package tech.rollw.disk.web.domain.storagepermission;

import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.common.BusinessRuntimeException;

import java.util.List;

/**
 * @author RollW
 */
public interface StoragePermissionAction extends SystemResourceOperator, SystemResource {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    StoragePermissionAction update() throws BusinessRuntimeException;

    /**
     * Remove permission settings or reset to default.
     */
    @Override
    StoragePermissionAction delete() throws BusinessRuntimeException;

    StoragePermissionAction setUserPermission(
            Operator operator,
            List<PermissionType> permissionTypes) throws BusinessRuntimeException;

    StoragePermissionAction removeUserPermission(Operator operator)
            throws BusinessRuntimeException;

    StoragePermissionAction setPermission(PublicPermissionType publicPermissionType)
            throws BusinessRuntimeException;

    AttributedStorage getRelatedStorage();

    @Override
    default StoragePermissionAction rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    StoragePermissionAction getSystemResource();

    StoragePermission getStoragePermission();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.STORAGE_PERMISSION;
    }
}
