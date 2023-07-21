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
import org.apache.commons.lang3.ArrayUtils;
import tech.rollw.disk.web.domain.operatelog.context.OperationContextHolder;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.validate.FieldType;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.systembased.validate.ValidatorProvider;
import tech.rollw.disk.web.domain.tag.*;
import tech.rollw.disk.web.domain.tag.common.ContentTagErrorCode;
import tech.rollw.disk.web.domain.tag.common.ContentTagException;
import tech.rollw.disk.web.domain.tag.repository.ContentTagRepository;
import tech.rollw.disk.web.domain.tag.repository.TagGroupRepository;
import tech.rollw.disk.web.util.Keywords;
import tech.rollw.disk.web.common.ParamValidate;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

/**
 * @author RollW
 */
@Service
public class ContentTagServiceImpl implements ContentTagService {
    private final ContentTagRepository contentTagRepository;
    private final TagGroupRepository tagGroupRepository;
    private final List<TagEventListener> tagEventListeners;
    private final Validator contentTagValidator;

    public ContentTagServiceImpl(ContentTagRepository contentTagRepository,
                                 TagGroupRepository tagGroupRepository,
                                 List<TagEventListener> tagEventListeners,
                                 ValidatorProvider validatorProvider) {
        this.contentTagRepository = contentTagRepository;
        this.tagGroupRepository = tagGroupRepository;
        this.tagEventListeners = tagEventListeners;
        this.contentTagValidator = validatorProvider.getValidator(SystemResourceKind.TAG);
    }

    private List<TagGroupDto> matchWith(List<TagGroup> tagGroups,
                                        List<ContentTag> contentTags) {
        List<TagGroupDto> tagGroupDtos = new ArrayList<>();
        for (TagGroup tagGroup : tagGroups) {
            tagGroupDtos.add(pairWith(tagGroup, contentTags));
        }
        return tagGroupDtos;
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
    public List<ContentTagInfo> getTags(Pageable pageable) {
        return contentTagRepository.get(pageable.toOffset())
                .stream()
                .map(ContentTagInfo::of)
                .toList();
    }

    @Override
    public List<TagGroupDto> getTagGroups(Pageable pageable) {
        List<TagGroup> tagGroups = tagGroupRepository.get(pageable.toOffset());
        if (tagGroups == null || tagGroups.isEmpty()) {
            return List.of();
        }
        List<Long> ids = tagGroups.stream()
                .map(TagGroup::getTags)
                .flatMapToLong(LongStream::of)
                .distinct()
                .boxed()
                .toList();

        List<ContentTag> contentTags = contentTagRepository.getByIds(ids);

        return matchWith(tagGroups, contentTags);
    }

    @Override
    public void createContentTagGroup(String name,
                                      String description,
                                      KeywordSearchScope searchScope) {
        ParamValidate.notEmpty(name, "name");
        ParamValidate.notNull(searchScope, "keywordSearchScope");

        TagGroup exist = tagGroupRepository.getTagGroupByName(name);
        if (exist != null) {
            throw new ContentTagException(
                    ContentTagErrorCode.ERROR_TAG_GROUP_NAME_EXIST);
        }

        long now = System.currentTimeMillis();
        TagGroup tagGroup = TagGroup.builder()
                .setTags(new long[0])
                .setName(name)
                .setDescription(description)
                .setKeywordSearchScope(searchScope)
                .setParentId(null)
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        long id = tagGroupRepository.insert(tagGroup);
        TagGroup inserted = tagGroup
                .toBuilder()
                .setId(id)
                .build();
        notifyListeners(TagGroupDto.of(inserted));

        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .setChangedContent(inserted.getName());
    }

    @Override
    public void createContentTag(String name,
                                 String description,
                                 List<TagKeyword> keywords) {
        contentTagValidator.validateThrows(name, FieldType.NAME);
        contentTagValidator.validateThrows(description, FieldType.DESCRIPTION);

        ContentTag exist = contentTagRepository.getByName(name);
        if (exist != null) {
            throw new ContentTagException(
                    ContentTagErrorCode.ERROR_TAG_EXIST);
        }

        long now = System.currentTimeMillis();
        ContentTag contentTag = ContentTag.builder()
                .setName(name)
                .setKeywords(keywords)
                .setDescription(description)
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        long id = contentTagRepository.insert(contentTag);
        ContentTag inserted = contentTag
                .toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .setChangedContent(inserted.getName());
    }

    @Override
    public void importFromKeywordsFile(InputStream stream, long tagGroupId) {
        TagGroup tagGroup = tagGroupRepository.getById(tagGroupId);
        if (tagGroup == null) {
            throw new ContentTagException(
                    ContentTagErrorCode.ERROR_TAG_GROUP_NOT_EXIST);
        }

        Keywords keywords = new Keywords(stream);
        List<Keywords.KeywordsGroup> keywordsGroups =
                keywords.listGroups();
        if (keywordsGroups == null || keywordsGroups.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        List<ContentTag> contentTags = new ArrayList<>();
        for (Keywords.KeywordsGroup keywordsGroup : keywordsGroups) {
            ContentTag contentTag = createFrom(keywordsGroup, now);
            contentTags.add(contentTag);
        }

        List<ContentTag> inserted = inserts(contentTags);
        long[] ids = inserted.stream()
                .mapToLong(ContentTag::getId)
                .toArray();
        long[] nowTags = tagGroup.getTags();
        List<ContentTag> nowContentTags =
                contentTagRepository.getByIds(Longs.asList(nowTags));

        List<ContentTag> allContentTags = new ArrayList<>(nowContentTags);
        allContentTags.addAll(inserted);

        long[] newTags = ArrayUtils.addAll(nowTags, ids);

        TagGroup updated = tagGroup.toBuilder()
                .setTags(newTags)
                .setUpdateTime(now)
                .build();
        tagGroupRepository.update(updated);
        TagGroupDto tagGroupDto =
                pairWith(tagGroup, allContentTags);
        notifyListeners(tagGroupDto);

        OperationContextHolder.getContext()
                .addSystemResource(updated)
                .setChangedContent(updated.getName())
                .addSystemResources(inserted);
    }

    private List<ContentTag> inserts(List<ContentTag> contentTags) {
        long[] ids = contentTagRepository.insert(contentTags);
        List<ContentTag> inserted = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            inserted.add(contentTags.get(i)
                    .toBuilder()
                    .setId(ids[i])
                    .build());
        }
        return inserted;
    }

    @Override
    public void exportToKeywordsFile(OutputStream stream, long tagGroupId) {
    }

    private ContentTag createFrom(Keywords.KeywordsGroup keywordsGroup, long time) {
        String name = keywordsGroup.name();
        return ContentTag
                .builder()
                .setName(name)
                .setKeywords(convertsTo(keywordsGroup.keywords()))
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
    }

    private List<TagKeyword> convertsTo(List<Keywords.Keyword> keywords) {
        List<TagKeyword> tagKeywords = new ArrayList<>();
        for (Keywords.Keyword keyword : keywords) {
            tagKeywords.add(new TagKeyword(
                    keyword.word(),
                    keyword.weight()
            ));
        }
        return tagKeywords;
    }

    @Async
    void notifyListeners(TagGroupDto tagGroup) {
        for (TagEventListener tagEventListener : tagEventListeners) {
            tagEventListener.onTagGroupChanged(tagGroup);
        }
    }
}
