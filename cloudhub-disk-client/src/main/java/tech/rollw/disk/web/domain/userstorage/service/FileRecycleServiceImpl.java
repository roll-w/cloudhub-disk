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

package tech.rollw.disk.web.domain.userstorage.service;

import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.FileRecycleService;
import tech.rollw.disk.web.domain.userstorage.StorageAction;
import tech.rollw.disk.web.domain.userstorage.StorageActionService;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.repository.UserFileStorageRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileRecycleServiceImpl implements FileRecycleService {
    private final UserFileStorageRepository userFileStorageRepository;
    private final StorageActionService storageActionService;

    public FileRecycleServiceImpl(UserFileStorageRepository userFileStorageRepository,
                                  StorageActionService storageActionService) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.storageActionService = storageActionService;
    }

    @Override
    public List<AttributedStorage> listRecycle(StorageOwner storageOwner) {
        return Collections.unmodifiableList(
                userFileStorageRepository.getDeletedByOwner(
                        storageOwner.getOwnerId(),
                        storageOwner.getOwnerType()
                )
        );
    }

    @Override
    public void revertRecycle(StorageIdentity storageIdentity,
                              StorageOwner storageOwner) {
        if (!storageIdentity.isFile()) {
            return;
        }
        StorageAction storageAction =
                storageActionService.openStorageAction(storageIdentity, storageOwner);
        storageAction.restore();
    }
}
