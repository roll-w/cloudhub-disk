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

package tech.rollw.disk.web.domain.storagesearch.service;

import tech.rollw.disk.web.domain.storagesearch.SearchCondition;
import tech.rollw.disk.web.domain.storagesearch.SearchConditionGroup;
import tech.rollw.disk.web.domain.storagesearch.StorageSearchConditionProvider;
import tech.rollw.disk.web.domain.storagesearch.StorageSearchService;
import tech.rollw.disk.web.domain.storagesearch.common.SearchConditionException;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author RollW
 */
@Service
public class StorageSearchServiceImpl implements StorageSearchService {
    private final List<StorageSearchConditionProvider> storageSearchConditionProviders;

    public StorageSearchServiceImpl(List<StorageSearchConditionProvider> storageSearchConditionProviders) {
        this.storageSearchConditionProviders = storageSearchConditionProviders;
    }

    @Override
    public List<? extends AttributedStorage> searchFor(
            List<SearchCondition> searchConditions, StorageOwner storageOwner) {
        if (searchConditions.isEmpty()) {
            return List.of();
        }
        Set<StorageSearchConditionProvider> collected =
                getProviders(searchConditions);
        if (collected.isEmpty()) {
            return List.of();
        }
        SearchConditionGroup conditionGroup = new SearchConditionGroup(searchConditions);
        if (conditionGroup.hasDuplicateConditionName()) {
            return List.of();
        }

        List<AttributedStorage> result = new ArrayList<>();
        for (StorageSearchConditionProvider storageSearchConditionProvider : collected) {
            List<? extends AttributedStorage> attributedStorages =
                    storageSearchConditionProvider.getStorages(conditionGroup, storageOwner);
            result.addAll(attributedStorages);
        }
        return distinctByIdAndType(result);
    }

    private List<? extends AttributedStorage> distinctByIdAndType(List<AttributedStorage> result) {
        Set<SimpleStorageIdentity> existStorages = new HashSet<>();
        List<AttributedStorage> distinctResult = new ArrayList<>();
        for (AttributedStorage attributedStorage : result) {
            SimpleStorageIdentity simpleStorageIdentity = new SimpleStorageIdentity(attributedStorage.getStorageId(),
                    attributedStorage.getStorageType());
            if (!existStorages.contains(simpleStorageIdentity)) {
                distinctResult.add(attributedStorage);
                existStorages.add(simpleStorageIdentity);
            }
        }
        return distinctResult;
    }

    @Override
    public List<? extends AttributedStorage> searchFor(
            List<SearchCondition> searchConditions) throws SearchConditionException {
        return null;
    }

    private Set<StorageSearchConditionProvider> getProviders(
            List<SearchCondition> searchConditions) {
        Set<StorageSearchConditionProvider> collected = new HashSet<>();
        for (SearchCondition searchCondition : searchConditions) {
            StorageSearchConditionProvider provider = findFirst(searchCondition.name());
            if (provider == null) {
                return Set.of();
            }
            collected.add(provider);
        }
        return collected;
    }

    private StorageSearchConditionProvider findFirst(String condition) {
        return storageSearchConditionProviders.stream()
                .filter(storageSearchConditionProvider -> storageSearchConditionProvider
                        .supportsCondition(condition))
                .findFirst()
                .orElse(null);
    }
}
