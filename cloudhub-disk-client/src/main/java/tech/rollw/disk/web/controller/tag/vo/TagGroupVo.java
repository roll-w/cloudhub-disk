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

package tech.rollw.disk.web.controller.tag.vo;

import tech.rollw.disk.web.domain.tag.KeywordSearchScope;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;

/**
 * @author RollW
 */
public record TagGroupVo(
        long id,
        long parent,
        String name,
        String description,
        KeywordSearchScope keywordSearchScope,
        long createTime,
        long updateTime
) {

    public static TagGroupVo from(TagGroupDto tagGroupDto) {
        return new TagGroupVo(
                tagGroupDto.id(),
                tagGroupDto.parent(),
                tagGroupDto.name(),
                tagGroupDto.description(),
                tagGroupDto.keywordSearchScope(),
                tagGroupDto.createTime(),
                tagGroupDto.updateTime()
        );
    }

}
