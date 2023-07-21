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
import tech.rollw.disk.web.domain.userstorage.FileType;
import tech.rollw.disk.web.domain.userstorage.StorageCategoryService;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileCategoryController {
    private final StorageCategoryService storageCategoryService;

    public FileCategoryController(StorageCategoryService storageCategoryService) {
        this.storageCategoryService = storageCategoryService;
    }

    @GetMapping("{ownerType}/{ownerId}/disk/file/category/{type}")
    public HttpResponseEntity<List<StorageVo>> getByType(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        FileType fileType = FileType.from(type);

        List<? extends AttributedStorage> attributedStorages =
                storageCategoryService.getByType(storageOwner, fileType);

        return HttpResponseEntity.success(
                attributedStorages
                        .stream()
                        .filter(attributedStorage -> !attributedStorage.isDeleted())
                        .map(StorageVo::from)
                        .toList()
        );
    }

}
