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
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.FileRecycleService;
import tech.rollw.disk.web.domain.userstorage.StorageActionService;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileRecycleController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageActionService storageActionService;
    private final FileRecycleService fileRecycleService;

    public FileRecycleController(UserStorageSearchService userStorageSearchService,
                                 StorageActionService storageActionService,
                                 FileRecycleService fileRecycleService) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageActionService = storageActionService;
        this.fileRecycleService = fileRecycleService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/recycles")
    public HttpResponseEntity<List<StorageVo>> getRecycleBinFiles(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        List<AttributedStorage> attributedStorages =
                fileRecycleService.listRecycle(storageOwner);
        List<StorageVo> storageVos = attributedStorages.stream().map(
                StorageVo::from
        ).toList();
        return HttpResponseEntity.success(storageVos);
    }
}
