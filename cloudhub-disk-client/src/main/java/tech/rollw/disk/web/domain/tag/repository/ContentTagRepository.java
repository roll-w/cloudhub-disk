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

package tech.rollw.disk.web.domain.tag.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.ContentTagDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.tag.ContentTag;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class ContentTagRepository extends BaseRepository<ContentTag> {
    private final ContentTagDao contentTagDao;

    protected ContentTagRepository(DiskDatabase database,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware,
                                   CacheManager cacheManager) {
        super(database.getContentTagDao(), pageableContextThreadAware, cacheManager);
        contentTagDao = database.getContentTagDao();
    }

    @Override
    protected Class<ContentTag> getEntityClass() {
        return ContentTag.class;
    }

    public ContentTag getByName(String name) {
        return cacheResult(contentTagDao.getByName(name));
    }

    public List<ContentTag> getByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }

        return cacheResult(contentTagDao.getByNames(names));
    }
}
