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

package tech.rollw.disk.web.domain.tag;

import tech.rollw.disk.web.BaseAbility;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;

import java.util.List;

/**
 * @author RollW
 */
@BaseAbility
public interface ContentTagProvider {
    List<ContentTagInfo> getTags();

    List<TagGroupInfo> getTagGroups();

    ContentTagInfo getTagById(long id);

    TagGroupDto getTagGroupById(long id);

    TagGroupInfo getTagGroupInfoById(long id);

    ContentTagInfo getByName(String name);

    List<TagGroupInfo> getTagGroupInfos(List<Long> tagGroupIds);

    List<ContentTagInfo> getTags(List<Long> tagIds);

    List<ContentTagInfo> getTagsByNames(List<String> names);

    List<TagGroupInfo> getTagGroupInfosByNames(List<String> names);
}
