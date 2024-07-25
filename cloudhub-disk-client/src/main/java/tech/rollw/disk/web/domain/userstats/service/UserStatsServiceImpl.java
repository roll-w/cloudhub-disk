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

import tech.rollw.disk.web.domain.usergroup.GroupSettingKeys;
import tech.rollw.disk.web.domain.usergroup.UserGroupSearchService;
import tech.rollw.disk.web.domain.usergroup.dto.UserGroupInfo;
import tech.rollw.disk.web.domain.userstats.*;
import tech.rollw.disk.web.domain.userstats.dto.RestrictInfo;
import tech.rollw.disk.web.domain.userstats.dto.UserStatisticsDetail;
import tech.rollw.disk.web.domain.userstats.repository.UserStatisticsRepository;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserStatsServiceImpl implements UserStatisticsService, UserDataViewService {
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserGroupSearchService userGroupSearchService;

    public UserStatsServiceImpl(UserStatisticsRepository userStatisticsRepository,
                                UserGroupSearchService userGroupSearchService) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.userGroupSearchService = userGroupSearchService;
    }

    @Override
    public UserStatisticsDetail getUserStatistics(
            @NonNull StorageOwner storageOwner) {
        UserStatistics userStatistics = userStatisticsRepository
                .getByOwnerIdAndOwnerType(storageOwner.getOwnerId(),
                        storageOwner.getOwnerType());
        if (userStatistics == null) {
            return UserStatisticsDetail.defaultOf(storageOwner);
        }
        return UserStatisticsDetail.from(userStatistics);
    }

    @Override
    public void rescanUserStatisticsOf(@NonNull StorageOwner storageOwner) {
        // TODO: rescan user statistics
    }

    @Override
    public void rescanUserStatistics() {

    }

    @Override
    public RestrictInfo findRestrictOf(StorageOwner storageOwner,
                                       String key) {
        UserStatisticsDetail userStatisticsDetail =
                getUserStatistics(storageOwner);
        Number valueGet = userStatisticsDetail.statistics()
                .getOrDefault(key, 0L);
        long userValue = valueGet.longValue();
        RestrictKey restrictKey = UserStatisticsKeys.restrictKeyOf(key);
        if (restrictKey == null) {
            return null;
        }
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        String restrictValue =
                // TODO: fix load default settings
                userGroupInfo.settings().get(restrictKey.getRestrictKey());
        long value = restrictKey.toValue(restrictValue);
        return new RestrictInfo(key, userValue, value);
    }

    @Override
    public List<RestrictInfo> findRestrictsOf(StorageOwner storageOwner) {
        UserStatisticsDetail userStatisticsDetail =
                getUserStatistics(storageOwner);
        List<RestrictKey> restrictKeys =
                userStatisticsDetail.statistics().keySet().stream()
                        .map(UserStatisticsKeys::restrictKeyOf)
                        .toList();
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        return restrictKeys.stream().map(restrictKey -> {
            String defaultValue = GroupSettingKeys.DEFAULT.getSettings()
                    .get(restrictKey.getRestrictKey());
            String restrictValue = userGroupInfo.settings()
                    .getOrDefault(restrictKey.getRestrictKey(), defaultValue);
            long value = restrictKey.toValue(restrictValue);
            Number number = userStatisticsDetail.statistics()
                    .getOrDefault(restrictKey.getKey(), 1L);
            long userValue = number.longValue();
            return new RestrictInfo(restrictKey.getKey(), userValue, value);
        }).toList();
    }
}
