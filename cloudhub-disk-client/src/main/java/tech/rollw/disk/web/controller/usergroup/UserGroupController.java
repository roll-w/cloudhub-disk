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

package tech.rollw.disk.web.controller.usergroup;

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.usergroup.UserGroupSearchService;
import tech.rollw.disk.web.domain.usergroup.dto.UserGroupInfo;
import tech.rollw.disk.web.domain.usergroup.vo.UserGroupVo;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.common.AuthErrorCode;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author RollW
 */
@Api
public class UserGroupController {
    private final UserGroupSearchService userGroupSearchService;

    public UserGroupController(UserGroupSearchService userGroupSearchService) {
        this.userGroupSearchService = userGroupSearchService;
    }

    // Gets the current user's group settings
    @GetMapping("/groups")
    public HttpResponseEntity<UserGroupVo> getCurrentUserGroupSettings() {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(userIdentity);
        return HttpResponseEntity.success(
                UserGroupVo.from(userGroupInfo));
    }

    @GetMapping("/{ownerType}/{ownerId}/groups")
    public HttpResponseEntity<UserGroupVo> getGroupSettings(
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

        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        return HttpResponseEntity.success(
                UserGroupVo.from(userGroupInfo));
    }
}
