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
import tech.rollw.disk.web.domain.userstorage.StorageAttributesService;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.dto.StorageTagValue;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class FileTagManageController {
    private final StorageAttributesService storageAttributesService;

    public FileTagManageController(StorageAttributesService storageAttributesService) {
        this.storageAttributesService = storageAttributesService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public HttpResponseEntity<List<StorageTagValue>> getTags(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerType) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        List<StorageTagValue> storageTagValues =
                storageAttributesService.getStorageTags(storageIdentity, storageOwner);
        return HttpResponseEntity.success(storageTagValues);
    }


    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public HttpResponseEntity<Void> resetTags(@PathVariable("storageType") String storageType,
                                              @PathVariable("storageId") Long storageId,
                                              @PathVariable("ownerId") Long ownerId,
                                              @PathVariable("ownerType") String ownerType) {
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public HttpResponseEntity<Void> deleteTag(@PathVariable("storageType") String storageType,
                                              @PathVariable("storageId") Long storageId,
                                              @PathVariable("ownerId") Long ownerId,
                                              @PathVariable("ownerType") String ownerType) {
        return HttpResponseEntity.success();
    }

    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public HttpResponseEntity<Void> createTag(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerType) {
        return HttpResponseEntity.success();
    }

}
