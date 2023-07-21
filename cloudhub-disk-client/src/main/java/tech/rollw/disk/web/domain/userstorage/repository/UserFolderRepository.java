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

package tech.rollw.disk.web.domain.userstorage.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.UserFolderDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.UserFolder;
import tech.rollw.disk.common.data.page.Offset;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserFolderRepository extends BaseRepository<UserFolder> {
    private final UserFolderDao userFolderDao;

    public UserFolderRepository(DiskDatabase diskDatabase,
                                ContextThreadAware<PageableContext> pageableContextThreadAware,
                                CacheManager cacheManager) {
        super(diskDatabase.getUserDirectoryDao(), pageableContextThreadAware, cacheManager);
        this.userFolderDao = diskDatabase.getUserDirectoryDao();
    }

    @Override
    protected Class<UserFolder> getEntityClass() {
        return UserFolder.class;
    }

    public List<UserFolder> getByParentId(long parentId) {
        return cacheResult(
                userFolderDao.getByParentId(parentId)
        );
    }

    public List<UserFolder> getByParentId(long parentId, Offset offset) {
        return cacheResult(
                userFolderDao.getByParentId(parentId, offset)
        );
    }

    public List<UserFolder> getByParentId(long parentId, long owner,
                                          LegalUserType legalUserType) {
        return cacheResult(
                userFolderDao.getByParentId(parentId, owner, legalUserType)
        );
    }

    public UserFolder getByName(String name, long parentId,
                                long owner, LegalUserType legalUserType) {
        return cacheResult(
                userFolderDao.getByName(name, parentId, owner, legalUserType)
        );
    }

    public UserFolder getByName(String name, long parentId) {
        return cacheResult(
                userFolderDao.getByName(name, parentId)
        );
    }

    public UserFolder getById(long folderId, long ownerId, LegalUserType ownerType) {
        UserFolder userFolder = getById(folderId);
        if (userFolder == null ||
                userFolder.getOwner() != ownerId ||
                userFolder.getOwnerType() != ownerType) {
            return null;
        }
        return userFolder;
    }

    public List<UserFolder> getParents(long folderId) {
        UserFolder userFolder = getById(folderId);
        if (userFolder == null || userFolder.getParentId() <= 0) {
            return List.of();
        }
        List<Long> parentFolderIds =
                userFolderDao.getParentFolderIds(userFolder.getParentId());
        return getByIds(parentFolderIds);
    }

    public List<UserFolder> getFoldersLike(String name, StorageOwner storageOwner) {
        return cacheResult(userFolderDao.findFoldersLike(name, storageOwner));
    }

    public List<UserFolder> findFoldersByCondition(StorageOwner storageOwner,
                                                   String name,
                                                   Long before,
                                                   Long after) {
        return cacheResult(
                userFolderDao.findFoldersByCondition(storageOwner, name, before, after)
        );
    }

    public List<UserFolder> getActiveByOwner(StorageOwner storageOwner, Offset offset) {
        if (offset == null) {
            return cacheResult(
                    userFolderDao.getActiveByOwner(storageOwner)
            );
        }

        return cacheResult(
                userFolderDao.getActiveByOwner(storageOwner, offset)
        );
    }

    public List<UserFolder> getByOwner(StorageOwner storageOwner, Offset offset) {
        if (offset == null) {
            return cacheResult(
                    userFolderDao.getByOwner(storageOwner)
            );
        }

        return cacheResult(
                userFolderDao.getByOwner(storageOwner, offset)
        );
    }

    public int countActiveByOwner(StorageOwner storageOwner) {
        return userFolderDao.countActiveByOwner(storageOwner);
    }

    public int countByOwner(StorageOwner storageOwner) {
        return userFolderDao.countByOwner(storageOwner);
    }
}
