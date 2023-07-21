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

package tech.rollw.disk.web.domain.storagepermission.service;

import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.storagepermission.StoragePermissionService;
import tech.rollw.disk.web.domain.systembased.*;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Service
public class UserStorageProviderServiceImpl
        implements SystemResourceActionProvider {
    private final StoragePermissionService storagePermissionService;
    private final UserStorageSearchService userStorageSearchService;

    public UserStorageProviderServiceImpl(StoragePermissionService storagePermissionService,
                                          UserStorageSearchService userStorageSearchService) {
        this.storagePermissionService = storagePermissionService;
        this.userStorageSearchService = userStorageSearchService;
    }

    @NonNull
    @Override
    public SystemAuthentication authenticate(SystemResource systemResource,
                                             Operator operator,
                                             Action action) {
        StorageIdentity storageIdentity = tryGetStorageIdentity(systemResource);

        boolean allow = storagePermissionService.checkPermissionOf(
                storageIdentity, operator,
                action, true
        );

        return new SimpleSystemAuthentication(systemResource, operator, allow);
    }

    private StorageIdentity tryGetStorageIdentity(SystemResource systemResource) {
        if (systemResource instanceof StorageIdentity storageIdentity) {
            return storageIdentity;
        }
        StorageType storageType = StorageType.from(systemResource.getSystemResourceKind());
        if (storageType == null) {
            return null;
        }
        return new SimpleStorageIdentity(systemResource.getResourceId(), storageType);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FILE ||
                systemResourceKind == SystemResourceKind.FOLDER;
    }

    @Override
    public SystemResource provide(long resourceId,
                                  SystemResourceKind systemResourceKind) {
        StorageType storageType = StorageType.from(systemResourceKind);
        if (storageType == null) {
            throw new UnsupportedKindException(systemResourceKind);
        }
        return userStorageSearchService.findStorage(new SimpleStorageIdentity(resourceId, storageType));
    }
}
