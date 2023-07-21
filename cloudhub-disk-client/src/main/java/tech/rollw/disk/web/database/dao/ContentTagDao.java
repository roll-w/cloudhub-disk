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

package tech.rollw.disk.web.database.dao;

import tech.rollw.disk.web.domain.tag.ContentTag;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface ContentTagDao extends AutoPrimaryBaseDao<ContentTag> {

    @Query("SELECT * FROM content_tag")
    List<ContentTag> getTags();

    @Query("SELECT * FROM content_tag LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<ContentTag> getTags(Offset offset);

    @Query("SELECT * FROM content_tag WHERE id = {id}")
    ContentTag getTagById(long id);

    @Override
    @Query("SELECT * FROM content_tag WHERE deleted = 0")
    List<ContentTag> getActives();

    @Override
    @Query("SELECT * FROM content_tag WHERE deleted = 1")
    List<ContentTag> getInactives();

    @Override
    @Query("SELECT * FROM content_tag WHERE id = {id}")
    ContentTag getById(long id);

    @Override
    @Query("SELECT * FROM content_tag WHERE id IN ({ids})")
    List<ContentTag> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM content_tag WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM content_tag WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM content_tag")
    List<ContentTag> get();

    @Override
    @Query("SELECT COUNT(*) FROM content_tag")
    int count();

    @Override
    @Query("SELECT * FROM content_tag LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<ContentTag> get(Offset offset);

    @Override
    default String getTableName() {
        return "content_tag";
    }

    @Query("SELECT * FROM content_tag WHERE name = {name}")
    ContentTag getByName(String name);

    @Query("SELECT * FROM content_tag WHERE name IN ({names})")
    List<ContentTag> getByNames(List<String> names);
}
