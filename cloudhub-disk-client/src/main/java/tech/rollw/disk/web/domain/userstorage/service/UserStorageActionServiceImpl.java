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

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.common.StorageErrorCode;
import tech.rollw.disk.web.domain.userstorage.common.StorageException;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import tech.rollw.disk.web.domain.userstorage.repository.UserFileStorageRepository;
import tech.rollw.disk.web.domain.userstorage.repository.UserFolderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserStorageActionServiceImpl implements StorageActionService,
        FolderActionDelegate, FileActionDelegate {
    private final UserFileStorageRepository userFileStorageRepository;
    private final UserFolderRepository userFolderRepository;
    private final StorageEventListener storageEventListener;

    public UserStorageActionServiceImpl(UserFileStorageRepository userFileStorageRepository,
                                        UserFolderRepository userFolderRepository,
                                        List<StorageEventListener> storageEventListeners) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.userFolderRepository = userFolderRepository;
        this.storageEventListener = new CompositeStorageEventListener(storageEventListeners);
    }

    @Override
    public StorageAction openStorageAction(StorageIdentity storage) {
        return createStorageAction(storage, false);
    }

    @Override
    public StorageAction openStorageAction(StorageIdentity storageIdentity, StorageOwner storageOwner) throws StorageException {
        AttributedStorage storage = switch (storageIdentity.getStorageType()) {
            case FOLDER -> findDirectory(storageIdentity.getStorageId());
            case FILE -> findFile(storageIdentity.getStorageId());
            case LINK -> null;
        };
        if (storage == null || !checkStorageOwner(storage,
                storageOwner.getOwnerId(), storageOwner.getOwnerType())) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return createStorageAction(storage, false);
    }

    private boolean checkStorageOwner(Storage storage, long ownerId,
                                      LegalUserType userType) {
        return storage.getOwnerId() == ownerId &&
                storage.getOwnerType() == userType;
    }

    private StorageAction createStorageAction(StorageIdentity storage, boolean checkDeleted) {
        StorageAction storageAction = switch (storage.getStorageType()) {
            case FOLDER -> createDirectoryAction(storage, checkDeleted);
            case FILE -> createFileAction(storage, checkDeleted);
            default -> null;
        };
        return new StorageActionWrapper(storageAction, storageEventListener);
    }

    private StorageAction createDirectoryAction(StorageIdentity storage, boolean checkDeleted) {
        if (storage.getStorageType() != StorageType.FOLDER) {
            throw new IllegalArgumentException("storage type must be folder");
        }
        if (!(storage instanceof UserFolder userFolder)) {
            return createDirectoryAction(
                    findDirectory(storage.getStorageId()),
                    checkDeleted
            );
        }

        return new FolderAction(userFolder, this, checkDeleted);
    }

    private StorageAction createFileAction(StorageIdentity storage, boolean checkDeleted) {
        if (storage.getStorageType() != StorageType.FILE) {
            throw new IllegalArgumentException("storage type must be file");
        }
        if (!(storage instanceof UserFileStorage userFileStorage)) {
            return createFileAction(
                    findFile(storage.getStorageId()),
                    checkDeleted
            );
        }

        return new FileAction(userFileStorage, this, checkDeleted);
    }

    private UserFileStorage findFile(long fileId) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(fileId);
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        if (userFileStorage.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return userFileStorage;
    }

    private UserFolder findDirectory(long directoryId) throws StorageException {
        UserFolder userFolder = userFolderRepository.getById(directoryId);
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userFolder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userFolder;
    }

    @Override
    public Long createDirectory(UserFolder userFolder) {
        return userFolderRepository.insert(userFolder);
    }

    @Override
    public void onDeleteDirectory(UserFolder userFolder) {

    }

    @Override
    public void updateDirectory(UserFolder userFolder) {
        userFolderRepository.update(userFolder);
    }

    @Override
    public void checkExistsFolder(String name, long parentId) {
        UserFolder userFolder =
                userFolderRepository.getByName(name, parentId);
        if (userFolder != null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }
    }

    @Override
    public Long createFile(UserFileStorage userFileStorage) {
        return userFileStorageRepository.insert(userFileStorage);
    }

    @Override
    public void onDeleteFile(UserFileStorage userFileStorage) {

    }

    @Override
    public void updateFile(UserFileStorage userFileStorage) {
        userFileStorageRepository.update(userFileStorage);
    }

    @Override
    public void checkExistsFile(String name, long parentId) {
        UserFileStorage userFileStorage =
                userFileStorageRepository.getByName(name, parentId);
        if (userFileStorage != null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_EXISTED);
        }
    }

    @Override
    public AttributedStorage checkParentExists(long parentId) {
        if (parentId < 0) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (parentId == 0) {
            return UserFolder.ROOT_FOLDER;
        }
        UserFolder userFolder = userFolderRepository.getById(parentId);
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userFolder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userFolder;
    }

    @Override
    public boolean checkFolderHasActiveChildren(long id) {
        List<UserFolder> userFolders =
                userFolderRepository.getByParentId(id);
        if (checkHasNotDeleted(userFolders)) {
            return true;
        }
        List<UserFileStorage> userFileStorages =
                userFileStorageRepository.getByDirectoryId(id);
        return checkHasNotDeleted(userFileStorages);
    }

    private boolean checkHasNotDeleted(List<? extends AttributedStorage> attributedStorages) {
        if (attributedStorages.isEmpty()) {
            return false;
        }
        return attributedStorages.stream().noneMatch(AttributedStorage::isDeleted);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FILE ||
                systemResourceKind == SystemResourceKind.FOLDER;
    }

    private static final Class<StorageAction> storageActionClass = StorageAction.class;

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return storageActionClass.isAssignableFrom(clazz);
    }

    @Override
    public StorageAction createResourceOperator(SystemResource systemResource,
                                                boolean checkDelete) {
        StorageIdentity storageIdentity = new SimpleStorageIdentity(
                systemResource.getResourceId(),
                StorageType.from(systemResource.getSystemResourceKind()));
        return createStorageAction(storageIdentity, checkDelete);
    }
}
