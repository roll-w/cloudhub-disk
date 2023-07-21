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

import com.google.common.base.Strings;
import tech.rollw.disk.web.domain.storagesearch.SearchCondition;
import tech.rollw.disk.web.domain.storagesearch.SearchConditionGroup;
import tech.rollw.disk.web.domain.storagesearch.StorageSearchConditionProvider;
import tech.rollw.disk.web.domain.storagesearch.common.SearchConditionException;
import tech.rollw.disk.web.domain.storagesearch.common.SearchExpressionException;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.repository.StorageMetadataRepository;
import tech.rollw.disk.web.domain.userstorage.repository.UserFileStorageRepository;
import tech.rollw.disk.web.domain.userstorage.repository.UserStorageSearchCondition;
import tech.rollw.disk.web.domain.userstorage.repository.UserStorageSearchRepository;
import tech.rollw.disk.web.util.TimeParser;
import tech.rollw.disk.web.util.TimeRange;
import tech.rollw.disk.web.domain.tag.ContentTagProvider;
import tech.rollw.disk.web.domain.tag.NameValue;
import tech.rollw.disk.web.domain.tag.SimpleTaggedValue;
import tech.rollw.disk.web.domain.tag.TaggedValue;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupInfo;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.Collections;
import java.util.List;

import static tech.rollw.disk.web.domain.userstorage.common.ConditionNames.*;

/**
 * @author RollW
 */
@Service
public class UserStorageSearchProvider implements StorageCategoryService,
        StorageSearchConditionProvider {

    private static final String[] SUPPORTED_CONDITIONS = {
            NAME, TIME, LAST_MODIFIED_TIME, SIZE, TYPE
    };

    private final StorageMetadataRepository storageMetadataRepository;
    private final UserFileStorageRepository userFileStorageRepository;
    private final ContentTagProvider contentTagProvider;
    private final UserStorageSearchRepository userStorageSearchRepository;

    public UserStorageSearchProvider(StorageMetadataRepository storageMetadataRepository,
                                     UserFileStorageRepository userFileStorageRepository,
                                     ContentTagProvider contentTagProvider, UserStorageSearchRepository userStorageSearchRepository) {
        this.storageMetadataRepository = storageMetadataRepository;
        this.userFileStorageRepository = userFileStorageRepository;
        this.contentTagProvider = contentTagProvider;
        this.userStorageSearchRepository = userStorageSearchRepository;
    }

    @Override
    public List<? extends AttributedStorage> getStorages(SearchConditionGroup conditionGroup,
                                                         StorageOwner storageOwner)
            throws SearchConditionException {
        SearchCondition nameCondition = conditionGroup.getCondition(NAME);
        SearchCondition timeCondition = getTimeCondition(conditionGroup);
        SearchCondition typeCondition = conditionGroup.getCondition(TYPE);

        StorageType storageType = tryParseStorageType(typeCondition);
        FileType fileType = tryParseFileType(typeCondition);

        TimeRange timeRange = tryParseTimeRange(timeCondition);

        UserStorageSearchCondition userStorageSearchCondition = new UserStorageSearchCondition(
                storageType,
                storageOwner,
                nameCondition == null ? null : nameCondition.keyword(),
                fileType,
                null,
                timeRange.end(),
                timeRange.start()
        );

        return userStorageSearchRepository.findStoragesBy(userStorageSearchCondition);
    }

    private SearchCondition getTimeCondition(SearchConditionGroup conditionGroup) {
        SearchCondition timeCondition = conditionGroup.getCondition(TIME);
        if (timeCondition == null) {
            return conditionGroup.getCondition(LAST_MODIFIED_TIME);
        }
        return timeCondition;
    }

    private StorageType tryParseStorageType(SearchCondition condition) {
        if (condition == null) {
            return null;
        }
        return StorageType.from(condition.keyword());
    }

    private FileType tryParseFileType(SearchCondition condition) {
        if (condition == null) {
            return null;
        }
        return FileType.from(condition.keyword());
    }

    @NonNull
    private TimeRange tryParseTimeRange(SearchCondition condition) {
        if (condition == null) {
            return TimeRange.NULL;
        }
        if (Strings.isNullOrEmpty(condition.keyword())) {
            throw new SearchExpressionException("time condition keyword is null or empty");
        }

        return TimeParser.parseTimeRange(condition.keyword());
    }

    @Override
    public boolean supportsCondition(String name) {
        for (String supportedCondition : SUPPORTED_CONDITIONS) {
            if (supportedCondition.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AttributedStorage> getByType(StorageOwner storageOwner,
                                             FileType fileType) {
        return Collections.unmodifiableList(
                userFileStorageRepository.getByType(
                        storageOwner.getOwnerId(),
                        storageOwner.getOwnerType(),
                        fileType
                )
        );
    }

    private List<TaggedValue> toTaggedValues(List<NameValue> nameValues) {
        List<String> groupNames = nameValues.stream()
                .map(NameValue::name)
                .distinct()
                .toList();
        List<String> tagNames = nameValues.stream()
                .map(NameValue::value)
                .distinct()
                .toList();

        List<TagGroupInfo> tagGroupInfos =
                contentTagProvider.getTagGroupInfosByNames(groupNames);
        List<ContentTagInfo> tagInfos =
                contentTagProvider.getTagsByNames(tagNames);
        return SimpleTaggedValue.pairWithTags(tagGroupInfos, tagInfos);
    }


    @Override
    public List<AttributedStorage> getByTags(StorageOwner storageOwner, List<NameValue> nameValues) {
        if (checkDuplicateTag(nameValues)) {
            return List.of();
        }
        List<TaggedValue> taggedValues = toTaggedValues(nameValues);

        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByTagValues(taggedValues);
        List<Long> storageIds = storageMetadata.stream()
                .map(StorageMetadata::getStorageId)
                .distinct()
                .toList();

        return Collections.unmodifiableList(
                userFileStorageRepository.getByIds(storageIds, storageOwner)
        );
    }

    @Override
    public List<AttributedStorage> getByTypeAndTags(StorageOwner storageOwner,
                                                    FileType fileType,
                                                    List<NameValue> nameValues) {
        if (checkDuplicateTag(nameValues)) {
            return List.of();
        }
        List<TaggedValue> taggedValues = toTaggedValues(nameValues);

        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByTagValues(taggedValues);
        List<Long> storageIds = storageMetadata.stream()
                .map(StorageMetadata::getStorageId)
                .distinct()
                .toList();

        List<UserFileStorage> storages =
                userFileStorageRepository.getByIdsAndType(storageIds, fileType, storageOwner);
        return Collections.unmodifiableList(storages);
    }

    private boolean checkDuplicateTag(List<NameValue> nameValues) {
        return nameValues
                .stream()
                .map(NameValue::name)
                .distinct()
                .count() != nameValues.size();
    }


}
