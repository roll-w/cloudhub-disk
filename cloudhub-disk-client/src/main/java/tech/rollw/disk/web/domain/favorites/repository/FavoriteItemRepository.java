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

package tech.rollw.disk.web.domain.favorites.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.FavoriteItemDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.favorites.FavoriteItem;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class FavoriteItemRepository extends BaseRepository<FavoriteItem> {
    private final FavoriteItemDao favoriteItemDao;

    protected FavoriteItemRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getFavoriteItemDao(),
                pageableContextThreadAware, cacheManager);
        this.favoriteItemDao = database.getFavoriteItemDao();
    }

    @Override
    protected Class<FavoriteItem> getEntityClass() {
        return FavoriteItem.class;
    }

    public List<FavoriteItem> getByGroup(long groupId) {
        return cacheResult(favoriteItemDao.getByGroup(groupId));
    }

    public FavoriteItem getByGroupAndIdentity(long groupId, StorageIdentity storageIdentity) {
        return cacheResult(favoriteItemDao.getByGroupAndIdentity(groupId, storageIdentity));
    }
}
