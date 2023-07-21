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

package tech.rollw.disk.web.domain.usergroup.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.UserGroupMemberDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.usergroup.UserGroupMember;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserGroupMemberRepository extends BaseRepository<UserGroupMember> {
    private final UserGroupMemberDao userGroupMemberDao;

    public UserGroupMemberRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getUserGroupMemberDao(), pageableContextThreadAware, cacheManager);
        this.userGroupMemberDao = database.getUserGroupMemberDao();
    }

    @Override
    protected Class<UserGroupMember> getEntityClass() {
        return UserGroupMember.class;
    }

    public UserGroupMember getByUser(StorageOwner storageOwner) {
        UserGroupMember userGroupMember = searchCacheByUser(storageOwner);
        if (userGroupMember != null) {
            return userGroupMember;
        }

        return cacheResult(userGroupMemberDao.getByUser(
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType()
        ));
    }

    public List<UserGroupMember> getByGroup(long id) {
        return cacheResult(userGroupMemberDao.getByGroup(id));
    }

    private String toKey(StorageOwner storageOwner) {
        return storageOwner.getOwnerType() + ":" + storageOwner.getOwnerId();
    }

    @Override
    protected UserGroupMember cacheResult(UserGroupMember userGroupMember) {
        if (userGroupMember == null) {
            return null;
        }
        String userKey = toKey(userGroupMember);
        cache.put(userKey, userGroupMember);
        return super.cacheResult(userGroupMember);
    }

    private UserGroupMember searchCacheByUser(StorageOwner storageOwner) {
        String userKey = toKey(storageOwner);
        return cache.get(userKey, UserGroupMember.class);
    }
}
