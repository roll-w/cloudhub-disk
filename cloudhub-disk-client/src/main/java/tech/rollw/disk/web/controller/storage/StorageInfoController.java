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

import tech.rollw.disk.web.common.ParamValidate;
import tech.rollw.disk.web.common.ParameterFailedException;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.LongActionRequest;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.controller.StringActionRequest;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.BuiltinOperationType;
import tech.rollw.disk.web.domain.operatelog.context.OperationContextHolder;
import tech.rollw.disk.web.domain.storage.StorageService;
import tech.rollw.disk.web.domain.systembased.SystemResourceAuthenticate;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.dto.FileInfo;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Api
public class StorageInfoController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageService storageService;
    private final StorageActionService storageActionService;

    public StorageInfoController(UserStorageSearchService userStorageSearchService,
                                 StorageService storageService,
                                 StorageActionService storageActionService) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageService = storageService;
        this.storageActionService = storageActionService;
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.ACCESS,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info")
    public HttpResponseEntity<StorageVo> getStorageInfo(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type
    ) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        return HttpResponseEntity.success(
                getStorageVo(storageOwner, storageIdentity)
        );
    }

    @NonNull
    private StorageVo getStorageVo(StorageOwner storageOwner,
                                   StorageIdentity storageIdentity) {
        if (storageIdentity.getStorageType().isFile()) {
            FileInfo fileInfo = userStorageSearchService.findFile(
                    storageIdentity.getStorageId(), storageOwner
            );
            long size = storageService.getFileSize(fileInfo.getFileId());
            return StorageVo.from(fileInfo, size);
        }
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        return StorageVo.from(attributedStorage);
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.RENAME,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info/name")
    public HttpResponseEntity<Void> renameStorage(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody StringActionRequest actionRequest
    ) {
        ParamValidate.notEmpty(actionRequest.value(), "名称不能为空");

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        BuiltinOperationType builtinOperationType = switch (storageIdentity.getStorageType()) {
            case FOLDER -> BuiltinOperationType.RENAME_FOLDER;
            case FILE -> BuiltinOperationType.RENAME_FILE;
            default -> throw new ParameterFailedException("Unexpected value: " + storageIdentity.getStorageType());
        };
        OperationContextHolder
                .getContext()
                .setOperateType(builtinOperationType);
        StorageAction storageAction = storageActionService
                .openStorageAction(storageIdentity, storageOwner);
        storageAction.rename(actionRequest.value());

        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.MOVE,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info/parent")
    public HttpResponseEntity<Void> moveStorage(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody LongActionRequest actionRequest

    ) {
        ParamValidate.notNull(actionRequest.value(), "目标文件夹不能为空");

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        BuiltinOperationType builtinOperationType = switch (storageIdentity.getStorageType()) {
            case FOLDER -> BuiltinOperationType.MOVE_FOLDER;
            case FILE -> BuiltinOperationType.MOVE_FILE;
            default -> throw new ParameterFailedException(
                    "Unexpected value: " + storageIdentity.getStorageType());
        };
        OperationContextHolder
                .getContext()
                .setOperateType(builtinOperationType);
        StorageAction storageAction = storageActionService
                .openStorageAction(storageIdentity, storageOwner);
        storageAction.move(actionRequest.value());
        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.COPY,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info/copy")
    public HttpResponseEntity<Void> copyStorageTo(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody LongActionRequest actionRequest
    ) {
        ParamValidate.notNull(actionRequest.value(), "目标文件夹不能为空");

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        BuiltinOperationType builtinOperationType = switch (storageIdentity.getStorageType()) {
            case FOLDER -> BuiltinOperationType.COPY_FOLDER;
            case FILE -> BuiltinOperationType.COPY_FILE;
            default -> throw new ParameterFailedException(
                    "Unexpected value: " + storageIdentity.getStorageType());
        };
        OperationContextHolder
                .getContext()
                .setOperateType(builtinOperationType);
        StorageAction storageAction = storageActionService
                .openStorageAction(storageIdentity, storageOwner);
        storageAction.copy(actionRequest.value());
        return HttpResponseEntity.success();
    }

}
