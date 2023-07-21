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

package tech.rollw.disk.web.domain.tag.service;

import com.google.common.primitives.Longs;
import tech.rollw.disk.web.domain.tag.ContentTag;
import tech.rollw.disk.web.domain.tag.ContentTagProvider;
import tech.rollw.disk.web.domain.tag.TagGroup;
import tech.rollw.disk.web.domain.tag.common.ContentTagErrorCode;
import tech.rollw.disk.web.domain.tag.common.ContentTagException;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;
import tech.rollw.disk.web.domain.tag.dto.TagGroupInfo;
import tech.rollw.disk.web.domain.tag.repository.ContentTagRepository;
import tech.rollw.disk.web.domain.tag.repository.TagGroupRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class TagProviderImpl implements ContentTagProvider {
    private final ContentTagRepository contentTagRepository;
    private final TagGroupRepository tagGroupRepository;

    public TagProviderImpl(ContentTagRepository contentTagRepository,
                           TagGroupRepository tagGroupRepository) {
        this.contentTagRepository = contentTagRepository;
        this.tagGroupRepository = tagGroupRepository;
    }


    // TODO: impl this method
    @Override
    public List<ContentTagInfo> getTags() {
        return List.of();
    }

    @Override
    public List<TagGroupInfo> getTagGroups() {
        return List.of();
    }

    @Override
    public ContentTagInfo getTagById(long id) {
        ContentTag contentTag =
                contentTagRepository.getById(id);
        if (contentTag == null) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_NOT_EXIST);
        }

        return ContentTagInfo.of(contentTag);
    }

    @Override
    public TagGroupDto getTagGroupById(long id) {
        TagGroup tagGroup =
                tagGroupRepository.getById(id);
        if (tagGroup == null) {
            throw new ContentTagException(
                    ContentTagErrorCode.ERROR_TAG_GROUP_NOT_EXIST);
        }
        List<ContentTag> contentTags = contentTagRepository.getByIds(
                Longs.asList(tagGroup.getTags())
        );
        return pairWith(tagGroup, contentTags);
    }

    @Override
    public TagGroupInfo getTagGroupInfoById(long id) {
        TagGroup tagGroup =
                tagGroupRepository.getById(id);
        if (tagGroup == null) {
            throw new ContentTagException(
                    ContentTagErrorCode.ERROR_TAG_GROUP_NOT_EXIST);
        }
        return TagGroupInfo.of(tagGroup);
    }

    private TagGroupDto pairWith(TagGroup tagGroup,
                                 List<ContentTag> contentTags) {
        long[] tagIds = tagGroup.getTags();
        List<ContentTagInfo> tags = new ArrayList<>();
        for (long tagId : tagIds) {
            for (ContentTag contentTag : contentTags) {
                if (contentTag.getId() == tagId) {
                    tags.add(ContentTagInfo.of(contentTag));
                }
            }
        }
        return TagGroupDto.of(tagGroup, tags);
    }

    @Override
    public ContentTagInfo getByName(String name) {
        ContentTag contentTag = contentTagRepository.getByName(name);
        if (contentTag == null) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_NOT_EXIST);
        }
        return ContentTagInfo.of(contentTag);
    }

    @Override
    public List<TagGroupInfo> getTagGroupInfos(List<Long> tagGroupIds) {
        return tagGroupRepository.getByIds(tagGroupIds)
                .stream()
                .map(TagGroupInfo::of)
                .toList();
    }

    @Override
    public List<ContentTagInfo> getTags(List<Long> tagIds) {
        return contentTagRepository.getByIds(tagIds)
                .stream()
                .map(ContentTagInfo::of)
                .toList();
    }

    @Override
    public List<ContentTagInfo> getTagsByNames(List<String> names) {
        return contentTagRepository.getByNames(names)
                .stream()
                .map(ContentTagInfo::of)
                .toList();
    }

    @Override
    public List<TagGroupInfo> getTagGroupInfosByNames(List<String> names) {
        return tagGroupRepository.getByNames(names)
                .stream()
                .map(TagGroupInfo::of)
                .toList();
    }


}
