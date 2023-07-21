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

import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.controller.usergroup.vo.UserGroupCreateRequest;
import tech.rollw.disk.web.controller.usergroup.vo.UserGroupMemberCreateRequest;
import tech.rollw.disk.web.controller.usergroup.vo.UserGroupMemberVo;
import tech.rollw.disk.web.controller.usergroup.vo.UserGroupUpdateRequest;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.SimpleSystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorProvider;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.user.service.UserSearchService;
import tech.rollw.disk.web.domain.usergroup.UserGroup;
import tech.rollw.disk.web.domain.usergroup.UserGroupOperator;
import tech.rollw.disk.web.domain.usergroup.UserGroupSearchService;
import tech.rollw.disk.web.domain.usergroup.UserGroupService;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupException;
import tech.rollw.disk.web.domain.usergroup.dto.UserGroupInfo;
import tech.rollw.disk.web.domain.usergroup.vo.UserGroupVo;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.system.pages.PageableInterceptor;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.KeyValue;
import tech.rollw.disk.common.WebCommonErrorCode;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserGroupManageController {
    private final UserGroupService userGroupService;
    private final UserGroupSearchService userGroupSearchService;
    private final PageableInterceptor pageableInterceptor;
    private final ContextThreadAware<PageableContext> pageableContextAware;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final UserSearchService userSearchService;

    public UserGroupManageController(UserGroupService userGroupService,
                                     UserGroupSearchService userGroupSearchService,
                                     PageableInterceptor pageableInterceptor,
                                     ContextThreadAware<PageableContext> pageableContextAware,
                                     SystemResourceOperatorProvider systemResourceOperatorProvider,
                                     UserSearchService userSearchService) {
        this.userGroupService = userGroupService;
        this.userGroupSearchService = userGroupSearchService;
        this.pageableInterceptor = pageableInterceptor;
        this.pageableContextAware = pageableContextAware;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.userSearchService = userSearchService;
    }

    @PostMapping("/groups")
    public HttpResponseEntity<Void> createUserGroup(
            @RequestBody UserGroupCreateRequest userGroupCreateRequest) {
        userGroupService.createUserGroup(
                userGroupCreateRequest.name(),
                userGroupCreateRequest.description()
        );
        return HttpResponseEntity.success();
    }

    @GetMapping("/groups")
    public HttpResponseEntity<List<UserGroupVo>> getUserGroups(Pageable pageable) {
        List<UserGroupInfo> userGroupInfos =
                userGroupSearchService.getUserGroups(pageable);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        userGroupInfos.stream()
                                .map(UserGroupVo::from)
                                .toList(),
                        pageable,
                        UserGroup.class
                )
        );
    }

    @GetMapping("/groups/{id}")
    public HttpResponseEntity<UserGroupVo> getUserGroupDetails(
            @PathVariable("id") Long id) {
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroup(id);

        return HttpResponseEntity.success(UserGroupVo.from(userGroupInfo));
    }

    @GetMapping("/groups/{id}/members")
    public HttpResponseEntity<List<UserGroupMemberVo>> getUserGroupMembers(
            @PathVariable("id") Long id) {
        List<? extends StorageOwner> members =
                userGroupSearchService.findUserGroupMembers(id);
        List<Long> userIds = members.stream()
                .filter(storageOwner -> storageOwner.getOwnerType() == LegalUserType.USER)
                .map(StorageOwner::getOwnerId)
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        return HttpResponseEntity.success(
                attributedUsers.stream()
                        .map(UserGroupMemberVo::from)
                        .toList()
        );


    }

    @PutMapping("/groups/{id}/members")
    public HttpResponseEntity<Void> addUserGroupMember(
            @PathVariable("id") Long id,
            @RequestBody UserGroupMemberCreateRequest request) {
        if (request.type() != LegalUserType.USER) {
            throw new UserGroupException(WebCommonErrorCode.ERROR_PARAM_FAILED);
        }

        AttributedUser user = userSearchService.tryFindUser(request.name());
        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator.addMember(user);

        return HttpResponseEntity.success();
    }

    @PutMapping("/groups/{id}")
    public HttpResponseEntity<Void> updateUserGroup(
            @PathVariable("id") Long id,
            @RequestBody UserGroupUpdateRequest userGroupUpdateRequest) {

        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator.disableAutoUpdate()
                .setName(userGroupUpdateRequest.name())
                .setDescription(userGroupUpdateRequest.description())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/groups/{id}/settings")
    public HttpResponseEntity<Void> setSettingOfGroup(
            @PathVariable("id") Long id,
            @RequestBody KeyValue keyValue) {
        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator
                .enableAutoUpdate()
                .setSetting(keyValue.key(), keyValue.value());
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/groups/{id}")
    public HttpResponseEntity<Void> deleteUserGroup(
            @PathVariable("id") Long id) {
        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator.delete();
        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/groups")
    public HttpResponseEntity<UserGroupVo> getGroupSettings(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId
    ) {
        StorageOwner storageOwner =
                ParameterHelper.buildStorageOwner(ownerId, ownerType);
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        return HttpResponseEntity.success(
                UserGroupVo.from(userGroupInfo)
        );
    }
}
