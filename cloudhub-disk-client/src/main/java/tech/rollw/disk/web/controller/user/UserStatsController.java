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

package tech.rollw.disk.web.controller.user;

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.systembased.SystemResourceAuthenticate;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.userstats.UserDataViewService;
import tech.rollw.disk.web.domain.userstats.UserStatisticsService;
import tech.rollw.disk.web.domain.userstats.dto.RestrictInfo;
import tech.rollw.disk.web.domain.userstats.dto.UserStatisticsDetail;
import tech.rollw.disk.web.domain.userstats.vo.UserStatisticsVo;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.common.AuthErrorCode;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserStatsController {
    private final UserStatisticsService userStatisticsService;
    private final UserDataViewService userDataViewService;

    public UserStatsController(UserStatisticsService userStatisticsService,
                               UserDataViewService userDataViewService) {
        this.userStatisticsService = userStatisticsService;
        this.userDataViewService = userDataViewService;
    }


    @GetMapping("/{ownerType}/{ownerId}/statistics")
    public HttpResponseEntity<UserStatisticsVo> getUserStatistics(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId
    ) {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        if (userIdentity.getUserId() != storageOwner.getOwnerId()) {
            // TODO: supports other user types in the future
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
        UserStatisticsDetail statisticsDetail =
                userStatisticsService.getUserStatistics(storageOwner);
        return HttpResponseEntity.success(
                UserStatisticsVo.from(statisticsDetail)
        );
    }

    @SystemResourceAuthenticate(
            kindParam = "ownerType", inferredKind = false,
            idParam = "ownerId", action = Action.ACCESS, inferredAction = false)
    @GetMapping("/{ownerType}/{ownerId}/statistics/restricts")
    public HttpResponseEntity<List<RestrictInfo>> getUserRestrictInfos(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        List<RestrictInfo> restrictInfos =
                userDataViewService.findRestrictsOf(storageOwner);
        return HttpResponseEntity.success(restrictInfos);
    }

    @SystemResourceAuthenticate(
            kindParam = "ownerType", inferredKind = false,
            idParam = "ownerId",
            action = Action.ACCESS, inferredAction = false)
    @GetMapping("/{ownerType}/{ownerId}/statistics/restricts/{key}")
    public HttpResponseEntity<RestrictInfo> getUserRestrictInfoByKey(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("key") String key) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        RestrictInfo restrictInfo =
                userDataViewService.findRestrictOf(storageOwner, key);
        return HttpResponseEntity.success(restrictInfo);
    }
}
