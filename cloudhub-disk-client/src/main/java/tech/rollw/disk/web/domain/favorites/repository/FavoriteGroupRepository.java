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
import tech.rollw.disk.web.database.dao.FavoriteGroupDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.favorites.FavoriteGroup;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.operatelog.SimpleOperator;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class FavoriteGroupRepository extends BaseRepository<FavoriteGroup> {
    private final FavoriteGroupDao favoriteGroupDao;

    protected FavoriteGroupRepository(DiskDatabase database,
                                      ContextThreadAware<PageableContext> pageableContextThreadAware,
                                      CacheManager cacheManager) {
        super(database.getFavoriteGroupDao(),
                pageableContextThreadAware, cacheManager);
        this.favoriteGroupDao = database.getFavoriteGroupDao();
    }

    @Override
    protected Class<FavoriteGroup> getEntityClass() {
        return FavoriteGroup.class;
    }

    public FavoriteGroup getByName(String name, Operator operator) {
        return cacheResult(
                favoriteGroupDao.getByName(name, operator)
        );
    }

    public List<FavoriteGroup> getGroupsOf(Operator operator) {
        return cacheResult(
                favoriteGroupDao.getGroupsOf(operator)
        );
    }

    public List<FavoriteGroup> getGroupsOf(StorageOwner storageOwner) {
        if (storageOwner.getOwnerType() != LegalUserType.USER) {
            return List.of();
        }
        return cacheResult(
                favoriteGroupDao.getGroupsOf(
                        new SimpleOperator(storageOwner.getOwnerId()))
        );
    }

}
