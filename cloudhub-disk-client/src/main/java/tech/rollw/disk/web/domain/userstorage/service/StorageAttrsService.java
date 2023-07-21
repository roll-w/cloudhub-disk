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

package tech.rollw.disk.web.domain.userstorage.service;

import tech.rollw.disk.web.domain.tag.ContentTagProvider;
import tech.rollw.disk.web.domain.tag.SimpleTaggedValue;
import tech.rollw.disk.web.domain.tag.TaggedValue;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupInfo;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.dto.StorageTagValue;
import tech.rollw.disk.web.domain.userstorage.repository.StorageMetadataRepository;
import tech.rollw.disk.common.data.page.Offset;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class StorageAttrsService implements StorageAttributesService {
    private final UserStorageSearchService userStorageSearchService;
    private final ContentTagProvider contentTagProvider;
    private final StorageMetadataRepository storageMetadataRepository;

    public static final String FILE_TYPE = "fileType";

    public StorageAttrsService(UserStorageSearchService userStorageSearchService,
                               ContentTagProvider contentTagProvider,
                               StorageMetadataRepository storageMetadataRepository) {
        this.userStorageSearchService = userStorageSearchService;
        this.contentTagProvider = contentTagProvider;
        this.storageMetadataRepository = storageMetadataRepository;
    }

    @Override
    public List<StorageTagValue> getStorageTags(StorageIdentity storageIdentity,
                                                StorageOwner storageOwner) {
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        if (!storage.getStorageType().isFile()) {
            return List.of(getStorageTagValue(storage));
        }
        StorageTagValue fileTypeTagValue = getStorageTagValue(storage);
        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByStorageId(storageIdentity.getStorageId());
        if (storageMetadata.isEmpty()) {
            return List.of(fileTypeTagValue);
        }
        List<StorageTagValue> tagValues = new ArrayList<>();
        tagValues.add(fileTypeTagValue);

        List<TaggedValue> taggedValues = getTagValues(storageMetadata);
        tagValues.addAll(
                pairWithIdTagValues(taggedValues, storageMetadata)
        );
        return tagValues;
    }

    @Override
    public StorageTagValueIterator getStorageTagValueIterator() {
        return new ValueIteratorImpl(storageMetadataRepository.count());
    }

    private List<StorageTagValue> pairWithIdTagValues(List<TaggedValue> taggedValues,
                                                      List<StorageMetadata> metadatas) {
        List<StorageTagValue> tagValues = new ArrayList<>();
        for (StorageMetadata metadata : metadatas) {
            TaggedValue taggedValue = findInIdTagValues(taggedValues, metadata);
            tagValues.add(StorageTagValue.of(
                    metadata,
                    taggedValue.name(),
                    taggedValue.value()
            ));
        }
        return tagValues;
    }

    private TaggedValue findInIdTagValues(List<TaggedValue> taggedValues, StorageMetadata metadata) {
        return taggedValues.stream()
                .filter(taggedValue -> taggedValue.groupId() == metadata.getTagGroupId())
                .findFirst()
                .orElse(null);
    }

    private List<TaggedValue> getTagValues(List<StorageMetadata> storageMetadata) {
        List<Long> tagGroupIds = storageMetadata.stream()
                .map(StorageMetadata::getTagGroupId)
                .toList();

        List<Long> tagIds = storageMetadata.stream()
                .map(StorageMetadata::getTagId)
                .toList();
        List<TagGroupInfo> tagGroupInfos =
                contentTagProvider.getTagGroupInfos(tagGroupIds);
        List<ContentTagInfo> tags =
                contentTagProvider.getTags(tagIds);
        return SimpleTaggedValue.pairWithTags(tagGroupInfos, tags);
    }

    private StorageTagValue getStorageTagValue(AttributedStorage storage) {
        if (!storage.getStorageType().isFile()) {
            return StorageTagValue.of(
                    FILE_TYPE,
                    storage.getStorageType().name()
            );
        }
        return StorageTagValue.of(
                FILE_TYPE,
                storage.getFileType().name()
        );
    }

    private class ValueIteratorImpl implements StorageTagValueIterator {
        private final long size;
        private Offset currentOffset = null;
        private boolean loaded = false;

        public ValueIteratorImpl(long size) {
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            if (size > 0 && currentOffset == null) {
                this.currentOffset = new Offset(1000, 0);
                return true;
            }
            if (!loaded) {
                return true;
            }

            long curSize = currentOffset.offset()
                    + currentOffset.limit();
            return curSize < size;
        }

        private Offset nextOffset() {
            return new Offset(currentOffset.limit(),
                    currentOffset.offset() + currentOffset.limit());
        }

        @Override
        public List<StorageTagValue> next() {
            if (!hasNext()) {
                return List.of();
            }
            loaded = true;
            List<StorageMetadata> storageMetadata =
                    storageMetadataRepository.get(currentOffset);
            List<TaggedValue> taggedValues =
                    getTagValues(storageMetadata);
            List<StorageTagValue> tagValues =
                    pairWithIdTagValues(taggedValues, storageMetadata);
            currentOffset = nextOffset();
            return tagValues;
        }
    }
}
