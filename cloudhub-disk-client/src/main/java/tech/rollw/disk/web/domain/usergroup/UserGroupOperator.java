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

package tech.rollw.disk.web.domain.usergroup;

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.common.BusinessRuntimeException;
import space.lingu.NonNull;

import java.util.Map;

/**
 * @author RollW
 */
public interface UserGroupOperator extends SystemResource, SystemResourceOperator {

    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER_GROUP;
    }

    @Override
    UserGroupOperator update() throws BusinessRuntimeException;

    @Override
    UserGroupOperator delete() throws BusinessRuntimeException;

    @Override
    UserGroupOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException;

    @Override
    UserGroupOperator disableAutoUpdate();

    @Override
    UserGroupOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    default SystemResource getSystemResource() {
        return this;
    }

    UserGroupOperator setName(String name);

    UserGroupOperator setDescription(String description);

    UserGroupOperator setSettings(Map<String, String> settings);

    UserGroupOperator setSetting(String key, String value);

    UserGroupOperator addMember(@NonNull StorageOwner storageOwner);

    UserGroupOperator removeMember(@NonNull StorageOwner storageOwner);

    UserGroup getUserGroup();
}
