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

package tech.rollw.disk.web.domain.userstorage;

import tech.rollw.disk.web.BaseAbility;
import tech.rollw.disk.web.domain.userstorage.common.StorageException;
import tech.rollw.disk.web.domain.userstorage.dto.FileInfo;
import tech.rollw.disk.web.domain.userstorage.dto.FileStorageInfo;
import tech.rollw.disk.web.domain.userstorage.dto.FolderStructureInfo;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@BaseAbility
public interface UserStorageSearchService {
    AttributedStorage findStorage(StorageIdentity storageIdentity) throws StorageException;

    List<? extends AttributedStorage> findStorages(List<? extends StorageIdentity> storageIdentity)
            throws StorageException;

    AttributedStorage findStorage(StorageIdentity storageIdentity,
                                  StorageOwner storageOwner) throws StorageException;

    @NonNull
    FolderStructureInfo findFolder(long folderId) throws StorageException;

    FolderStructureInfo findFolder(long folderId, StorageOwner storageOwner) throws StorageException;

    @NonNull
    FolderStructureInfo findFolder(FileStorageInfo fileStorageInfo) throws StorageException;

    @NonNull
    FileInfo findFile(long fileId) throws StorageException;

    FileInfo findFile(long fileId, StorageOwner storageOwner)
            throws StorageException;

    @NonNull
    FileInfo findFile(FileStorageInfo fileStorageInfo) throws StorageException;

    List<AttributedStorage> listFolders(long folderId, StorageOwner storageOwner);

    // include directories
    List<AttributedStorage> listFiles(long folderId, StorageOwner storageOwner);

    // don't care about the owner
    List<AttributedStorage> listFiles(long folderId);

    List<AttributedStorage> listStorages(StorageOwner storageOwner);

    List<AttributedStorage> listStorages();

    List<AttributedStorage> listOf(StorageType storageType);

    List<AttributedStorage> listOf(StorageOwner storageOwner, StorageType storageType);

}
