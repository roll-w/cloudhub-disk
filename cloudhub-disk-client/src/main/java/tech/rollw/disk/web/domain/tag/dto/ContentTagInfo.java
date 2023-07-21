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

import tech.rollw.disk.web.domain.tag.ContentTag;
import tech.rollw.disk.web.domain.tag.TagKeyword;

import java.util.List;

/**
 * @author RollW
 */
public record ContentTagInfo(
        long id,
        String name,
        List<TagKeyword> keywords,
        String description,
        long createTime,
        long updateTime
) {
    public TagKeyword findKeywordByName(String name) {
        return keywords.stream()
                .filter(keyword -> keyword.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static ContentTagInfo of(ContentTag contentTag) {
        if (contentTag == null) {
            return null;
        }

        return new ContentTagInfo(
                contentTag.getId(),
                contentTag.getName(),
                contentTag.getKeywords(),
                contentTag.getDescription(),
                contentTag.getCreateTime(),
                contentTag.getUpdateTime()
        );
    }
}
