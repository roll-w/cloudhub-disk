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
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.service.UserSearchService;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.web.domain.versioned.VersionedFileService;
import tech.rollw.disk.web.domain.versioned.VersionedFileStorage;
import tech.rollw.disk.web.domain.versioned.vo.VersionedStorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class VersionedFileManageController {
    private final UserStorageSearchService userStorageSearchService;
    private final UserSearchService userSearchService;
    private final VersionedFileService versionedFileService;

    public VersionedFileManageController(UserStorageSearchService userStorageSearchService,
                                         UserSearchService userSearchService,
                                         VersionedFileService versionedFileService) {
        this.userStorageSearchService = userStorageSearchService;
        this.userSearchService = userSearchService;
        this.versionedFileService = versionedFileService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/versions")
    public HttpResponseEntity<List<VersionedStorageVo>> getVersionsOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerType) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        if (!storageIdentity.getStorageType().isFile()) {
            return HttpResponseEntity.success(List.of());
        }
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        List<VersionedFileStorage> fileStorages =
                versionedFileService.getVersionedFileStorages(storage.getStorageId());
        if (fileStorages.isEmpty()) {
            return HttpResponseEntity.success();
        }

        List<Long> userIds = fileStorages.stream()
                .map(VersionedFileStorage::getOperator)
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);

        return HttpResponseEntity.success(
                VersionedFileController.buildVersionedStorageVos(fileStorages, attributedUsers)
        );
    }
}
