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
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.web.domain.storage.StorageService;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageOwner;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserStorageManageController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageService storageService;

    public UserStorageManageController(UserStorageSearchService userStorageSearchService,
                                       StorageService storageService) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageService = storageService;
    }

    @GetMapping("/users/{userId}/files")
    public HttpResponseEntity<List<StorageVo>> getFiles(
            @PathVariable("userId") @NonNull Long userId) {
        StorageOwner storageOwner =
                new SimpleStorageOwner(userId, LegalUserType.USER);
        List<AttributedStorage> storages =
                userStorageSearchService.listOf(storageOwner, StorageType.FILE);
        return HttpResponseEntity.success(
                storages.stream()
                        .map(StorageVo::from)
                        .toList()
        );
    }

    @GetMapping("/users/{userId}/folders")
    public HttpResponseEntity<List<StorageVo>> getFolders(
            @PathVariable("userId") @NonNull Long userId) {
        StorageOwner storageOwner =
                new SimpleStorageOwner(userId, LegalUserType.USER);
        List<AttributedStorage> storages =
                userStorageSearchService.listOf(storageOwner, StorageType.FOLDER);

        return HttpResponseEntity.success(
                storages.stream()
                        .map(StorageVo::from)
                        .toList()
        );
    }

}
