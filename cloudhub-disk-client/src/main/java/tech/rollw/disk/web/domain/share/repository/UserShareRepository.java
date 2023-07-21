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

package tech.rollw.disk.web.domain.share.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.UserShareDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.share.UserShare;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.common.data.page.Offset;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserShareRepository extends BaseRepository<UserShare> {
    private final UserShareDao userShareDao;

    protected UserShareRepository(DiskDatabase database,
                                  ContextThreadAware<PageableContext> pageableContextThreadAware,
                                  CacheManager cacheManager) {
        super(database.getUserShareDao(), pageableContextThreadAware, cacheManager);
        userShareDao = database.getUserShareDao();
    }

    public UserShare getByShareId(String shareId) {
        return cacheResult(userShareDao.getByShareId(shareId));
    }

    public List<UserShare> getByUserId(long userId) {
        return cacheResult(userShareDao.getByUserId(userId));
    }

    public List<UserShare> getByUserId(long userId, Offset offset) {
        return cacheResult(userShareDao.getByUserId(userId, offset));
    }

    public List<UserShare> getByStorage(long storageId, StorageType storageType) {
        return cacheResult(
                userShareDao.getByStorage(storageId, storageType)
        );
    }

    public List<UserShare> getByStorage(long storageId, StorageType storageType, Offset offset) {
        return cacheResult(
                userShareDao.getByStorage(storageId, storageType, offset)
        );
    }

    @Override
    protected Class<UserShare> getEntityClass() {
        return UserShare.class;
    }
}
