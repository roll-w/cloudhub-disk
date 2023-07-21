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

import tech.rollw.disk.web.domain.storagesearch.SearchCondition;
import tech.rollw.disk.web.domain.storagesearch.SearchConditionGroup;
import tech.rollw.disk.web.domain.storagesearch.StorageSearchConditionProvider;
import tech.rollw.disk.web.domain.storagesearch.common.SearchConditionException;
import tech.rollw.disk.web.domain.tag.InternalTagGroupRepository;
import tech.rollw.disk.web.domain.tag.TagEventListener;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;
import tech.rollw.disk.web.domain.tag.NameValue;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.common.ConditionNames;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author RollW
 */
@Service
public class UserStorageTagSearchProvider implements StorageSearchConditionProvider, TagEventListener {
    private final Set<String> supportedTagNames = new HashSet<>();
    private final InternalTagGroupRepository tagGroupRepository;
    private final StorageCategoryService storageCategoryService;

    public UserStorageTagSearchProvider(InternalTagGroupRepository tagGroupRepository,
                                        StorageCategoryService storageCategoryService) {
        this.tagGroupRepository = tagGroupRepository;
        this.storageCategoryService = storageCategoryService;
        init();
    }

    private void init() {
        tagGroupRepository.findAll().forEach(
                tagGroup -> supportedTagNames.add(tagGroup.getName())
        );
    }

    @Override
    public List<? extends AttributedStorage> getStorages(
            SearchConditionGroup conditionGroup, StorageOwner storageOwner) throws SearchConditionException {
        SearchCondition typeCondition = conditionGroup.getCondition(ConditionNames.TYPE);

        StorageType storageType = StorageType.from(
               typeCondition == null ? null : typeCondition.keyword()
        );
        if (!isValidType(storageType)) {
            return List.of();
        }
        FileType fileType = FileType.from(
                typeCondition == null ? null : typeCondition.keyword()
        );
        List<NameValue> nameValues = extractTagValues(conditionGroup);
        if (fileType == null) {
            return storageCategoryService.getByTags(storageOwner, nameValues);
        }
        return storageCategoryService.getByTypeAndTags(storageOwner, fileType, nameValues);
    }

    private boolean isValidType(StorageType storageType) {
        if (storageType == null) {
            return true;
        }
        return storageType == StorageType.FILE;
    }

    private List<NameValue> extractTagValues(SearchConditionGroup conditionGroup) {
        List<NameValue> result = new ArrayList<>();
        for (String supportedTagName : supportedTagNames) {
            SearchCondition searchCondition =
                    conditionGroup.getCondition(supportedTagName);
            if (searchCondition == null) {
                continue;
            }
            result.add(new NameValue(supportedTagName, searchCondition.keyword()));
        }

        return result;
    }

    @Override
    public boolean supportsCondition(String name) {
        return supportedTagNames.contains(name);
    }

    @Override
    public void onTagGroupChanged(TagGroupDto tagGroupDto) {
        supportedTagNames.add(tagGroupDto.name());
    }

    @Override
    public void onTagGroupDelete(String tagGroupName) {
        supportedTagNames.remove(tagGroupName);
    }
}
