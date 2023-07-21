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

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.common.ParamValidate;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.BuiltinOperationType;
import tech.rollw.disk.web.domain.operatelog.context.BuiltinOperate;
import tech.rollw.disk.web.domain.systembased.*;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.common.StorageErrorCode;
import tech.rollw.disk.web.domain.userstorage.common.StorageException;
import tech.rollw.disk.web.domain.userstorage.dto.FolderStructureInfo;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FolderController {
    private final UserFolderService userFolderService;
    private final StorageActionService storageActionService;
    private final UserStorageSearchService userStorageSearchService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;
    private final SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory;

    public FolderController(UserFolderService userFolderService,
                            StorageActionService storageActionService,
                            UserStorageSearchService userStorageSearchService,
                            ContextThreadAware<PageableContext> pageableContextThreadAware,
                            SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory) {
        this.userFolderService = userFolderService;
        this.storageActionService = storageActionService;
        this.userStorageSearchService = userStorageSearchService;
        this.pageableContextThreadAware = pageableContextThreadAware;
        this.systemResourceAuthenticationProviderFactory = systemResourceAuthenticationProviderFactory;
    }

    @SystemResourceAuthenticate(idParam = "folderId")
    @BuiltinOperate(BuiltinOperationType.CREATE_FOLDER)
    @PostMapping("/{ownerType}/{ownerId}/disk/folder/{folderId}")
    public HttpResponseEntity<StorageVo> createFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("folderId") Long folderId,
            @RequestBody FolderCreateRequest folderCreateRequest) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        ParamValidate.notEmpty(folderCreateRequest.name(), "folder name");
        AttributedStorage storage = userFolderService.createFolder(
                folderCreateRequest.name(),
                folderId, storageOwner
        );
        return HttpResponseEntity.success(
                StorageVo.from(storage)
        );
    }

    @SystemResourceAuthenticate(idParam = "storageId")
    @BuiltinOperate(BuiltinOperationType.DELETE_FOLDER)
    @DeleteMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<Void> deleteFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("storageId") Long storageId) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = new SimpleStorageIdentity(storageId, StorageType.FOLDER);

        AttributedStorage storage = userStorageSearchService.findStorage(storageIdentity, storageOwner);
        if (storage.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_ALREADY_DELETED);
        }
        StorageAction storageAction =
                storageActionService.openStorageAction(storage);
        storageAction.delete();
        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(
            idParam = "directory",
            kind = SystemResourceKind.FOLDER, inferredKind = false,
            action = Action.ACCESS, inferredAction = false
    )
    @GetMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<List<StorageVo>> listFiles(
            @PathVariable("storageId") Long directory,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);

        List<AttributedStorage> storages = userStorageSearchService.listFiles(
                directory,
                storageOwner
        );
        SystemResourceAuthenticationProvider systemAuthenticationProvider =
                systemResourceAuthenticationProviderFactory.getSystemResourceAuthenticationProvider(SystemResourceKind.FILE);

        List<SystemAuthentication> systemAuthentications =
                systemAuthenticationProvider.authenticate(storages, userIdentity, Action.ACCESS);
        List<AttributedStorage> authenticatedStorages =
                systemAuthentications.stream()
                        .filter(SystemAuthentication::isAllowAccess)
                        .map(SystemAuthentication::getSystemResource)
                        .map(resource -> resource.cast(AttributedStorage.class))
                        .toList();

        return HttpResponseEntity.success(
                authenticatedStorages.stream()
                        .filter(storage -> !storage.isDeleted())
                        .map(StorageVo::from)
                        .toList()
        );
    }

    @SystemResourceAuthenticate(
            idParam = "directory",
            kind = SystemResourceKind.FOLDER, inferredKind = false,
            action = Action.ACCESS, inferredAction = false
    )
    @GetMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/folders")
    public HttpResponseEntity<List<StorageVo>> listFolders(
            @PathVariable("storageId") Long directory,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);

        List<AttributedStorage> storages = userStorageSearchService.listFolders(
                directory,
                storageOwner
        );

        return HttpResponseEntity.success(
                storages.stream()
                        .filter(storage -> !storage.isDeleted())
                        .map(StorageVo::from)
                        .toList()
        );
    }

    @SystemResourceAuthenticate(
            idParam = "storageId",
            kind = SystemResourceKind.FOLDER, inferredKind = false,
            action = Action.ACCESS, inferredAction = false
    )
    @GetMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/info")
    public HttpResponseEntity<FolderStructureInfo> getFolderInfo(
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type
    ) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = new SimpleStorageIdentity(storageId, StorageType.FOLDER);
        FolderStructureInfo folderStructureInfo =
                userStorageSearchService.findFolder(storageIdentity.getStorageId(), storageOwner);
        return HttpResponseEntity.success(
                folderStructureInfo
        );
    }
}
