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

import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.storagepermission.common.StoragePermissionException;
import tech.rollw.disk.web.domain.storagepermission.dto.StoragePermissionDto;
import tech.rollw.disk.web.domain.storagepermission.dto.StoragePermissionsInfo;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public interface StoragePermissionService {
    // TODO: refactor, remove the ignoreDelete parameter

    boolean checkPermissionOf(StorageIdentity storage,
                              Operator operator,
                              Action action, boolean ignoreDelete);

    void checkPermissionOrThrows(StorageIdentity storage,
                                 Operator operator,
                                 Action action, boolean ignoreDelete) throws StoragePermissionException;

    StoragePermissionDto getPermissionOf(StorageIdentity storage,
                                         Operator operator, boolean ignoreDelete);

    StoragePermissionsInfo getPermissionOf(StorageIdentity storageIdentity, boolean ignoreDelete);

    StoragePermissionsInfo getPermissionOf(StorageIdentity storageIdentity,
                                           StorageOwner storageOwner, boolean ignoreDelete);
}
