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

import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.storage.StorageService;
import tech.rollw.disk.web.domain.systembased.ContextThread;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.dto.FileInfo;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class StorageManageController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageService storageService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;

    public StorageManageController(UserStorageSearchService userStorageSearchService,
                                   StorageService storageService,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageService = storageService;
        this.pageableContextThreadAware = pageableContextThreadAware;
    }


    @GetMapping("/disk/storages")
    public HttpResponseEntity<List<StorageVo>> getStorages() {
        List<AttributedStorage> storages =
                userStorageSearchService.listStorages();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        List<StorageVo> storageVos = storages.stream()
                .map(StorageVo::from)
                .toList();
        return HttpResponseEntity.success(
                pageableContext.toPage(storageVos)
        );
    }

    @GetMapping("/disk/files")
    public HttpResponseEntity<List<StorageVo>> getFiles() {
        return getStorageByType(StorageType.FILE);
    }

    @GetMapping("/disk/folders")
    public HttpResponseEntity<List<StorageVo>> getFolders() {
        return getStorageByType(StorageType.FOLDER);
    }

    @NonNull
    private HttpResponseEntity<List<StorageVo>> getStorageByType(StorageType storageType) {
        List<AttributedStorage> storages =
                userStorageSearchService.listOf(storageType);

        List<StorageVo> storageVos = storages.stream()
                .map(StorageVo::from)
                .toList();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        return HttpResponseEntity.success(
                pageableContext.toPage(storageVos)
        );
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info")
    public HttpResponseEntity<StorageVo> getStorageInfo(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type
    ) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        if (storageIdentity.getStorageType().isFile()) {
            FileInfo fileInfo = userStorageSearchService.findFile(
                    storageIdentity.getStorageId(), storageOwner
            );
            long size = storageService.getFileSize(fileInfo.getFileId());
            return HttpResponseEntity.success(
                    StorageVo.from(fileInfo, size, fileInfo.getFileId())
            );
        }
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        return HttpResponseEntity.success(
                StorageVo.from(attributedStorage)
        );
    }

}
