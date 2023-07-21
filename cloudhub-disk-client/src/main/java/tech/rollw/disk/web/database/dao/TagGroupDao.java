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

import tech.rollw.disk.web.domain.tag.TagGroup;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface TagGroupDao extends AutoPrimaryBaseDao<TagGroup> {

    @Query("SELECT * FROM tag_group")
    List<TagGroup> getTagGroups();

    @Query("SELECT * FROM tag_group LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<TagGroup> getTagGroups(Offset offset);

    @Query("SELECT * FROM tag_group WHERE id = {id}")
    TagGroup getTagGroupById(long id);

    @Query("SELECT * FROM tag_group WHERE name = {name}")
    TagGroup getTagGroupByName(String name);

    @Query("SELECT * FROM tag_group WHERE name LIKE {name}")
    List<TagGroup> getTagGroupsByName(String name);

    @Override
    @Query("SELECT * FROM tag_group WHERE deleted = 0")
    List<TagGroup> getActives();

    @Override
    @Query("SELECT * FROM tag_group WHERE deleted = 1")
    List<TagGroup> getInactives();

    @Override
    @Query("SELECT * FROM tag_group WHERE id = {id}")
    TagGroup getById(long id);

    @Override
    @Query("SELECT * FROM tag_group WHERE id IN ({ids})")
    List<TagGroup> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM tag_group WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM tag_group WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM tag_group")
    List<TagGroup> get();

    @Override
    @Query("SELECT COUNT(*) FROM tag_group")
    int count();

    @Override
    @Query("SELECT * FROM tag_group LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<TagGroup> get(Offset offset);

    @Override
    default String getTableName() {
        return "tag_group";
    }

    @Query("SELECT * FROM tag_group WHERE name IN ({names})")
    List<TagGroup> getByNames(List<String> names);
}
