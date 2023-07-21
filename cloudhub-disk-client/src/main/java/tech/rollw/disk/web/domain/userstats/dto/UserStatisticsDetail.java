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

package tech.rollw.disk.web.domain.userstats.dto;

import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstats.UserStatistics;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;

import java.util.Map;

/**
 * @author RollW
 */
public record UserStatisticsDetail(
        long id,
        long userId,
        LegalUserType userType,
        Map<String, Long> statistics
) {

    public static UserStatisticsDetail from(UserStatistics userStatistics) {
        return new UserStatisticsDetail(
                userStatistics.getId(),
                userStatistics.getUserId(),
                userStatistics.getUserType(),
                userStatistics.getStatistics()
        );
    }

    public static UserStatisticsDetail defaultOf(StorageOwner storageOwner) {
        return new UserStatisticsDetail(
                0L,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType(),
                Map.of()
        );
    }
}
