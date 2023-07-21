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
import tech.rollw.disk.web.database.dao.TagGroupDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.tag.InternalTagGroupRepository;
import tech.rollw.disk.web.domain.tag.TagGroup;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class TagGroupRepository extends BaseRepository<TagGroup>
        implements InternalTagGroupRepository {
    private final TagGroupDao tagGroupDao;

    public TagGroupRepository(DiskDatabase database,
                              ContextThreadAware<PageableContext> pageableContextThreadAware,
                              CacheManager cacheManager) {
        super(database.getTagGroupDao(), pageableContextThreadAware, cacheManager);
        tagGroupDao = database.getTagGroupDao();
    }

    public TagGroup getTagGroupByName(String name) {
        return cacheResult(tagGroupDao.getTagGroupByName(name));
    }

    @Override
    protected Class<TagGroup> getEntityClass() {
        return TagGroup.class;
    }

    @Override
    public List<TagGroup> findAll() {
        return get();
    }

    public List<TagGroup> getByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        return cacheResult(tagGroupDao.getByNames(names));
    }
}
