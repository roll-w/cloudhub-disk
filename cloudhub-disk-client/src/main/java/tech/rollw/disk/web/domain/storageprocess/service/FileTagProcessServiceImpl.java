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

package tech.rollw.disk.web.domain.storageprocess.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import tech.rollw.disk.web.domain.tag.*;
import tech.rollw.disk.web.util.Keywords;
import tech.rollw.disk.web.util.KeywordsScorer;
import tech.rollw.disk.web.domain.storageprocess.StorageProcessingEventType;
import tech.rollw.disk.web.domain.storageprocess.StorageProcessingEvent;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;
import tech.rollw.disk.web.domain.tag.repository.ContentTagRepository;
import tech.rollw.disk.web.domain.tag.repository.TagGroupRepository;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.Storage;
import tech.rollw.disk.web.domain.userstorage.StorageEventListener;
import tech.rollw.disk.web.domain.userstorage.StorageMetadata;
import tech.rollw.disk.web.domain.userstorage.dto.FileAttributesInfo;
import tech.rollw.disk.web.domain.userstorage.dto.StorageAttr;
import tech.rollw.disk.web.domain.userstorage.repository.StorageMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author RollW
 */
@Service
public class FileTagProcessServiceImpl implements
        StorageEventListener, TagEventListener {
    private static final Logger logger = LoggerFactory.getLogger(FileTagProcessServiceImpl.class);

    private final StorageMetadataRepository storageMetadataRepository;
    private final ContentTagRepository contentTagRepository;
    private final TagGroupRepository tagGroupRepository;

    private final List<StorageProcessingCallback> storageProcessingCallbacks;

    private final List<KeywordProcessor> keywordProcessors = new CopyOnWriteArrayList<>();

    public FileTagProcessServiceImpl(StorageMetadataRepository storageMetadataRepository,
                                     ContentTagRepository contentTagRepository,
                                     TagGroupRepository tagGroupRepository,
                                     List<StorageProcessingCallback> storageProcessingCallbacks) {
        this.storageMetadataRepository = storageMetadataRepository;
        this.contentTagRepository = contentTagRepository;
        this.tagGroupRepository = tagGroupRepository;
        this.storageProcessingCallbacks = storageProcessingCallbacks;

        loadKeywordsGroup();
    }

    private void loadKeywordsGroup() {
        List<TagGroup> tagGroups = tagGroupRepository.get();
        List<ContentTag> contentTags = contentTagRepository.get();

        List<TagGroupDto> tagGroupDtos = matchWith(tagGroups, contentTags);
        for (TagGroupDto tagGroupDto : tagGroupDtos) {
            addKeywordProcessor(tagGroupDto);
        }
    }

    private void addKeywordProcessor(TagGroupDto tagGroupDto) {
        Keywords keywords = new Keywords(mapToKeywordMap(tagGroupDto));
        KeywordsScorer keywordsScorer = new KeywordsScorer(keywords);
        KeywordProcessor keywordProcessor = new KeywordProcessor(
                keywords,
                keywordsScorer,
                tagGroupDto.keywordSearchScope(),
                tagGroupDto
        );
        keywordProcessors.add(keywordProcessor);
    }

    private Map<String, List<Keywords.Keyword>> mapToKeywordMap(
            TagGroupDto tagGroupDto) {
        List<ContentTagInfo> contentTagInfos = tagGroupDto.tags();
        Map<String, List<Keywords.Keyword>> map = new HashMap<>();
        for (ContentTagInfo contentTagInfo : contentTagInfos) {
            List<Keywords.Keyword> keywords = new ArrayList<>();
            for (TagKeyword keyword : contentTagInfo.keywords()) {
                keywords.add(new Keywords.Keyword(
                        keyword.name(),
                        keyword.weight())
                );
            }
            map.put(contentTagInfo.name(), keywords);
        }
        return map;
    }

    @Override
    public void onTagGroupChanged(TagGroupDto tagGroupDto) {
        KeywordProcessor keywordProcessor = findExist(tagGroupDto.id());
        if (keywordProcessor == null) {
            addKeywordProcessor(tagGroupDto);
            return;
        }
        Map<String, List<Keywords.Keyword>> keywordMap =
                mapToKeywordMap(tagGroupDto);
        Keywords keywords = new Keywords(keywordMap);
        keywordProcessor.setTagGroupDto(tagGroupDto);
        keywordProcessor.updateKeywords(keywords);
    }

    private KeywordProcessor findExist(long tagGroupId) {
        for (KeywordProcessor keywordProcessor : keywordProcessors) {
            if (keywordProcessor.tagGroupDto().id() == tagGroupId) {
                return keywordProcessor;
            }
        }
        return null;
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
    public void onStorageProcess(AttributedStorage storage,
                                 @Nullable StorageAttr storageAttr) {
        onStorageCreated(storage, storageAttr);
    }

    @Override
    public void onStorageCreated(@NonNull AttributedStorage storage,
                                 StorageAttr storageAttr) {
        if (!storage.getStorageType().isFile()) {
            return;
        }

        String name = storage.getName();
        List<TaggedValue> taggedValues = new ArrayList<>();

        List<StorageMetadata> storageMetadatas = new ArrayList<>();

        for (KeywordProcessor keywordProcessor : keywordProcessors) {
            if (!allowName(keywordProcessor.searchScope())) {
                continue;
            }
            KeywordsScorer scorer = keywordProcessor.scorer();
            List<KeywordsScorer.Rank> ranks = scorer.score(name);
            if (ranks.isEmpty()) {
                continue;
            }
            KeywordsScorer.Rank rank = ranks.get(0);
            StorageMetadata storageMetadata =
                    buildMetadata(storage, keywordProcessor, rank);
            storageMetadatas.add(storageMetadata);

            TagGroupDto tagGroupDto = keywordProcessor.tagGroupDto();
            ContentTagInfo contentTagInfo =
                    tagGroupDto.findByName(rank.getGroup());
            taggedValues.add(SimpleTaggedValue.of(
                    storageMetadata.getTagGroupId(),
                    storageMetadata.getTagId(),
                    tagGroupDto.name(),
                    contentTagInfo.name()
            ));
        }
        clearUnusedMetadata(storage, storageMetadatas);

        onProcessed(
                StorageProcessingEventType.CREATE,
                storage,
                storageAttr.size(),
                taggedValues
        );
    }

    private void clearUnusedMetadata(AttributedStorage storage,
                                     List<StorageMetadata> newStorageMetadatas) {
        List<StorageMetadata> unusedMetadatas = new ArrayList<>();
        // TODO: remove unused metadata
    }

    @Override
    public void onStorageDeleted(@NonNull AttributedStorage storage,
                                 @Nullable FileAttributesInfo fileAttributesInfo) {
        if (!storage.getStorageType().isFile()) {
            return;
        }
        if (fileAttributesInfo == null) {
            logger.error("FileAttributesInfo is null, storage: {}.",
                    storage.getStorageId());
            return;
        }

        List<StorageMetadata> storageMetadata = storageMetadataRepository.getByStorageId(
                storage.getStorageId()
        );
        List<TaggedValue> taggedValues = new ArrayList<>();

        List<StorageMetadata> updatedMetas = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (StorageMetadata storageMetadatum : storageMetadata) {
            StorageMetadata updated = storageMetadatum.toBuilder()
                    .setDeleted(true)
                    .setUpdateTime(now)
                    .build();
            TaggedValue taggedValue = SimpleTaggedValue.of(
                    updated.getTagGroupId(),
                    updated.getTagId(),
                    null, null
                    // TODO: get tag group name and tag name
            );
            taggedValues.add(taggedValue);
            updatedMetas.add(updated);
        }
        storageMetadataRepository.update(updatedMetas);

        onProcessed(
                StorageProcessingEventType.DELETE,
                storage,
                fileAttributesInfo.size(),
                taggedValues
        );
    }

    private void onProcessed(StorageProcessingEventType storageProcessingEventType,
                             AttributedStorage storage, long size,
                             List<TaggedValue> taggedValues) {
        StorageProcessingEvent storageProcessingEvent = new StorageProcessingEvent(
                storageProcessingEventType,
                storage,
                size,
                taggedValues
        );
        for (StorageProcessingCallback storageProcessingCallback :
                storageProcessingCallbacks) {
            storageProcessingCallback.onProcessed(storageProcessingEvent);
        }
    }


    @Override
    public void onTagGroupDelete(String tagGroupName) {
        keywordProcessors.removeIf(keywordProcessor ->
                Objects.equals(keywordProcessor.tagGroupDto().name(), tagGroupName)
        );
    }

    private StorageMetadata buildMetadata(Storage storage,
                                          KeywordProcessor keywordProcessor,
                                          KeywordsScorer.Rank rank) {
        TagGroupDto tagGroupDto = keywordProcessor.tagGroupDto();
        ContentTagInfo contentTagInfo =
                tagGroupDto.findByName(rank.getGroup());
        long time = System.currentTimeMillis();
        StorageMetadata exist = storageMetadataRepository.getByStorageIdAndTagGroupId(
                storage.getStorageId(), tagGroupDto.id()
        );
        if (exist != null) {
            StorageMetadata updated = exist.toBuilder()
                    .setTagId(contentTagInfo.id())
                    .setUpdateTime(time)
                    .build();
            storageMetadataRepository.update(updated);
            return updated;
        }

        StorageMetadata storageMetadata = StorageMetadata.builder()
                .setTagId(contentTagInfo.id())
                .setTagGroupId(tagGroupDto.id())
                .setCreateTime(time)
                .setUpdateTime(time)
                .setStorageId(storage.getStorageId())
                .build();
        long id = storageMetadataRepository.insert(storageMetadata);
        return storageMetadata.toBuilder()
                .setId(id)
                .build();
    }

    private boolean allowName(KeywordSearchScope searchScope) {
        if (searchScope == KeywordSearchScope.NAME) {
            return true;
        }
        return searchScope == KeywordSearchScope.ALL;
    }

    private static final class KeywordProcessor {
        private Keywords keywords;
        private KeywordsScorer scorer;
        private KeywordSearchScope searchScope;
        private TagGroupDto tagGroupDto;

        private KeywordProcessor(
                Keywords keywords,
                KeywordsScorer scorer,
                KeywordSearchScope searchScope,
                TagGroupDto tagGroupDto
        ) {
            this.keywords = keywords;
            this.scorer = scorer;
            this.searchScope = searchScope;
            this.tagGroupDto = tagGroupDto;
        }

        public KeywordProcessor setKeywords(Keywords keywords) {
            this.keywords = keywords;
            return this;
        }

        public KeywordProcessor setScorer(KeywordsScorer scorer) {
            this.scorer = scorer;
            return this;
        }

        public KeywordProcessor setSearchScope(KeywordSearchScope searchScope) {
            this.searchScope = searchScope;
            return this;
        }

        public KeywordProcessor setTagGroupDto(TagGroupDto tagGroupDto) {
            this.tagGroupDto = tagGroupDto;
            return this;
        }

        void updateKeywords(Keywords keywords) {
            this.keywords = keywords;
            this.scorer = new KeywordsScorer(keywords);
        }

        public Keywords keywords() {
            return keywords;
        }

        public KeywordsScorer scorer() {
            return scorer;
        }

        public KeywordSearchScope searchScope() {
            return searchScope;
        }

        public TagGroupDto tagGroupDto() {
            return tagGroupDto;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (KeywordProcessor) obj;
            return Objects.equals(this.tagGroupDto, that.tagGroupDto);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keywords, scorer, searchScope, tagGroupDto);
        }

        @Override
        public String toString() {
            return "KeywordProcessor[" +
                    "keywords=" + keywords + ", " +
                    "scorer=" + scorer + ", " +
                    "searchScope=" + searchScope + ", " +
                    "tagGroupDto=" + tagGroupDto + ']';
        }
    }
}
