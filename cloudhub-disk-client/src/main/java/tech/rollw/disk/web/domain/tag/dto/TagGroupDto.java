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

package tech.rollw.disk.web.domain.tag.dto;

import tech.rollw.disk.web.domain.tag.KeywordSearchScope;
import tech.rollw.disk.web.domain.tag.TagGroup;

import java.util.List;

/**
 * @author RollW
 */
public record TagGroupDto(
        long id,
        long parent,
        String name,
        String description,
        List<ContentTagInfo> tags,
        KeywordSearchScope keywordSearchScope,
        List<TagGroupDto> children,
        long createTime,
        long updateTime
) {

    public static TagGroupDto of(TagGroup tagGroup, List<ContentTagInfo> tags) {
        return new TagGroupDto(
                tagGroup.getId(),
                tagGroup.getParentId() == null ? 0 : tagGroup.getParentId(),
                tagGroup.getName(),
                tagGroup.getDescription(),
                tags,
                tagGroup.getKeywordSearchScope(),
                List.of(),
                tagGroup.getCreateTime(),
                tagGroup.getUpdateTime()
        );
    }

    public static TagGroupDto of(TagGroup tagGroup) {
        return TagGroupDto.of(tagGroup, List.of());
    }

    public ContentTagInfo findByName(String name) {
        return tags.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
