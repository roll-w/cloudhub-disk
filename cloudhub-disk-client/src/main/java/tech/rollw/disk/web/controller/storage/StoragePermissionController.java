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

package tech.rollw.disk.web.controller.storage;

import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.OneParameterRequest;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.operatelog.BuiltinOperationType;
import tech.rollw.disk.web.domain.operatelog.context.BuiltinOperate;
import tech.rollw.disk.web.domain.storagepermission.PermissionType;
import tech.rollw.disk.web.domain.storagepermission.PublicPermissionType;
import tech.rollw.disk.web.domain.storagepermission.StoragePermissionAction;
import tech.rollw.disk.web.domain.storagepermission.StoragePermissionService;
import tech.rollw.disk.web.domain.storagepermission.dto.StoragePermissionsInfo;
import tech.rollw.disk.web.domain.systembased.SystemResourceAuthenticate;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorProvider;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.service.UserProvider;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class StoragePermissionController {
    private final StoragePermissionService storagePermissionService;
    private final UserStorageSearchService userStorageSearchService;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final UserProvider userProvider;

    public StoragePermissionController(StoragePermissionService storagePermissionService,
                                       UserStorageSearchService userStorageSearchService,
                                       SystemResourceOperatorProvider systemResourceOperatorProvider,
                                       UserProvider userProvider) {
        this.storagePermissionService = storagePermissionService;
        this.userStorageSearchService = userStorageSearchService;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.userProvider = userProvider;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions")
    public HttpResponseEntity<StoragePermissionsInfo> getPermissionOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity =  ParameterHelper.buildStorageIdentity(storageId, storageType);

        StoragePermissionsInfo storagePermissionsInfo =
                storagePermissionService.getPermissionOf(storageIdentity, storageOwner, false);
        return HttpResponseEntity.success(storagePermissionsInfo);
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @SystemResourceAuthenticate(
            idParam = "storageId",
            kindParam = "storageType",
            inferredKind = false
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/public")
    public HttpResponseEntity<Void> setPublicPermissionOf(
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody OneParameterRequest<PublicPermissionType> request) {
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        StoragePermissionAction storagePermissionAction = systemResourceOperatorProvider.getSystemResourceOperator(
                storage,
                SystemResourceKind.STORAGE_PERMISSION,
                true
        );
        storagePermissionAction.setPermission(request.value());

        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @SystemResourceAuthenticate(
            idParam = "storageId",
            kindParam = "storageType",
            inferredKind = false
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/user/{userId}")
    public HttpResponseEntity<Void> setUserPermissionOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @PathVariable("userId") String userIdentity,
            @RequestBody OneParameterRequest<List<PermissionType>> permissionTypes) {
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        AttributedUser user = userProvider.tryFindUser(userIdentity);
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);

        StoragePermissionAction storagePermissionAction = systemResourceOperatorProvider.getSystemResourceOperator(
                storage,
                SystemResourceKind.STORAGE_PERMISSION,
                true
        );
        storagePermissionAction.setUserPermission(user, permissionTypes.value());
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/user/{userId}")
    public HttpResponseEntity<Void> deleteUserPermissionOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @PathVariable("userId") String userIdentity) {
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        AttributedUser user = userProvider.tryFindUser(userIdentity);
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        StoragePermissionAction storagePermissionAction = systemResourceOperatorProvider.getSystemResourceOperator(
                storage,
                SystemResourceKind.STORAGE_PERMISSION,
                true
        );
        storagePermissionAction.removeUserPermission(user);
        return HttpResponseEntity.success();
    }

}
