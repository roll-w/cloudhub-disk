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

import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author RollW
 */
public record SimpleTaggedValue(
        long groupId,
        long tagId,
        String name,
        String value
) implements TaggedValue {
    public static SimpleTaggedValue of(long groupId, long tagId,
                                       String name, String value) {
        return new SimpleTaggedValue(groupId, tagId, name, value);
    }

    public static List<TaggedValue> pairWithTags(List<TagGroupInfo> tagGroupInfos,
                                                 List<ContentTagInfo> tags) {
        List<TaggedValue> tagValues = new ArrayList<>();
        List<ContentTagInfo> sortedTags = tags.stream()
                .sorted(Comparator.comparingLong(ContentTagInfo::id))
                .toList();
        for (TagGroupInfo tagGroupInfo : tagGroupInfos) {
            ContentTagInfo tag = findInTags(sortedTags, tagGroupInfo.tags());
            tagValues.add(new SimpleTaggedValue(
                    tagGroupInfo.id(),
                    tag.id(),
                    tagGroupInfo.name(),
                    tag.name()
            ));
        }
        return tagValues;
    }

    private static ContentTagInfo findInTags(List<ContentTagInfo> tags,
                                             long[] tagIds) {
        return tags.stream()
                .filter(tag -> Arrays.stream(tagIds).anyMatch(tagId -> tag.id() == tagId))
                .findFirst()
                .orElse(null);
    }


}
