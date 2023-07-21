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

package tech.rollw.disk.web.domain.userstats.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.UserStatisticsDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstats.UserStatistics;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class UserStatisticsRepository extends BaseRepository<UserStatistics> {
    private final UserStatisticsDao userStatisticsDao;

    public UserStatisticsRepository(DiskDatabase database,
                                    ContextThreadAware<PageableContext> pageableContextThreadAware,
                                    CacheManager cacheManager) {
        super(database.getUserStatisticsDao(), pageableContextThreadAware, cacheManager);
        userStatisticsDao = database.getUserStatisticsDao();
    }

    public UserStatistics getByUserId(long userId, LegalUserType userType) {
        return cacheResult(userStatisticsDao.getByUserId(userId, userType));
    }

    @Override
    protected Class<UserStatistics> getEntityClass() {
        return UserStatistics.class;
    }

    public UserStatistics getByOwnerIdAndOwnerType(long ownerId, LegalUserType ownerType) {
        return cacheResult(userStatisticsDao.getByOwnerIdAndOwnerType(ownerId, ownerType));
    }
}
