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

package tech.rollw.disk.web.domain.userstats.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstats.UserDataViewService;
import tech.rollw.disk.web.domain.userstats.UserStatistics;
import tech.rollw.disk.web.domain.userstats.UserStatisticsKeys;
import tech.rollw.disk.web.domain.userstats.dto.RestrictInfo;
import tech.rollw.disk.web.domain.userstats.repository.UserStatisticsRepository;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.Storage;
import tech.rollw.disk.web.domain.userstorage.StorageEventListener;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.common.StorageErrorCode;
import tech.rollw.disk.web.domain.userstorage.dto.FileAttributesInfo;
import tech.rollw.disk.web.domain.userstorage.dto.StorageAttr;
import tech.rollw.disk.common.CommonErrorCode;
import tech.rollw.disk.common.ErrorCode;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class UserStatsStorageProcessor implements StorageEventListener {
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserDataViewService userDataViewService;

    public UserStatsStorageProcessor(UserStatisticsRepository userStatisticsRepository,
                                     UserDataViewService userDataViewService) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.userDataViewService = userDataViewService;
    }

    // TODO: only preserve pre-checks of storage creation,
    //  move the statistics methods to the new job APIs.

    @Override
    public ErrorCode onBeforeStorageCreated(@NonNull StorageOwner storageOwner,
                                            @NonNull Operator operator,
                                            @Nullable FileAttributesInfo fileAttributesInfo) {
        List<RestrictInfo> restrictInfos =
                userDataViewService.findRestrictsOf(storageOwner);
        for (RestrictInfo restrictInfo : restrictInfos) {
            if (restrictInfo.restrict() == UserStatisticsKeys.NO_LIMIT) {
                continue;
            }
            ErrorCode errorCode = checkRestrictOf(restrictInfo, fileAttributesInfo);
            if (errorCode.failed()) {
                return errorCode;
            }
        }

        return CommonErrorCode.SUCCESS;
    }

    private ErrorCode checkRestrictOf(RestrictInfo restrictInfo,
                                      FileAttributesInfo fileAttributesInfo) {
        if (restrictInfo.restrict() == UserStatisticsKeys.NO_LIMIT) {
            return CommonErrorCode.SUCCESS;
        }
        switch (restrictInfo.key()) {
            case UserStatisticsKeys.USER_STORAGE_USED -> {
                if (restrictInfo.restrict() < fileAttributesInfo.size() + restrictInfo.value()) {
                    return StorageErrorCode.ERROR_STORAGE_SIZE_LIMIT;
                }
            }
            case UserStatisticsKeys.USER_STORAGE_COUNT -> {
                if (restrictInfo.restrict() < restrictInfo.value() + 1) {
                    return StorageErrorCode.ERROR_STORAGE_COUNT_LIMIT;
                }
            }
        }

        return CommonErrorCode.SUCCESS;
    }

    @Override
    public void onStorageCreated(@NonNull AttributedStorage storage,
                                 StorageAttr storageAttr) {
        LegalUserType userType = storage.getOwnerType();
        long userId = storage.getOwnerId();

        UserStatistics userStatistics =
                userStatisticsRepository.getByUserId(userId, userType);
        updateUserStatistics(userStatistics, storage, storageAttr);

    }

    private void updateUserStatistics(UserStatistics userStatistics,
                                      Storage storage,
                                      StorageAttr storageAttr) {
        if (userStatistics == null) {
            createUserStatistics(storage, storageAttr);
            return;
        }
        if (userStatistics.getStatistics().isEmpty()) {
            Map<String, Long> stats =
                    updateStorageStatistics(new HashMap<>(), storageAttr);
            UserStatistics updated = userStatistics.toBuilder()
                    .setStatistics(stats)
                    .build();
            userStatisticsRepository.update(updated);
            return;
        }
        updateStorageStatistics(userStatistics.getStatistics(), storageAttr);
        userStatisticsRepository.update(userStatistics);
    }

    private void createUserStatistics(StorageOwner storageOwner,
                                      StorageAttr storageAttr) {
        Map<String, Long> stats = new HashMap<>();
        updateStorageStatistics(stats, storageAttr);
        UserStatistics userStatistics = UserStatistics.builder()
                .setUserId(storageOwner.getOwnerId())
                .setUserType(storageOwner.getOwnerType())
                .setStatistics(stats)
                .build();
        userStatisticsRepository.insert(userStatistics);
    }

    private Map<String, Long> updateStorageStatistics(Map<String, Long> stats,
                                                      StorageAttr storageAttr) {
        long totalSize = getByKey(stats, UserStatisticsKeys.USER_STORAGE_USED);
        long totalStorageCount = getByKey(stats, UserStatisticsKeys.USER_STORAGE_COUNT);

        stats.put(UserStatisticsKeys.USER_STORAGE_USED,
                totalSize + storageAttr.size());
        stats.put(UserStatisticsKeys.USER_STORAGE_COUNT,
                totalStorageCount + 1);

        return stats;
    }

    private long getByKey(Map<String, Long> stats,
                          String key) {
        Number value = stats.get(key);
        if (value == null) {
            return 0;
        }
        return value.longValue();
    }
}
