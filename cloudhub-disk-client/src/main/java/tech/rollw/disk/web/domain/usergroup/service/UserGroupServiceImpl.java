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

package tech.rollw.disk.web.domain.usergroup.service;

import tech.rollw.disk.web.domain.operatelog.context.OperationContextHolder;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceProvider;
import tech.rollw.disk.web.domain.systembased.UnsupportedKindException;
import tech.rollw.disk.web.domain.systembased.validate.FieldType;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.systembased.validate.ValidatorProvider;
import tech.rollw.disk.web.domain.usergroup.UserGroup;
import tech.rollw.disk.web.domain.usergroup.UserGroupMember;
import tech.rollw.disk.web.domain.usergroup.UserGroupSearchService;
import tech.rollw.disk.web.domain.usergroup.UserGroupService;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupErrorCode;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupException;
import tech.rollw.disk.web.domain.usergroup.dto.UserGroupInfo;
import tech.rollw.disk.web.domain.usergroup.repository.UserGroupMemberRepository;
import tech.rollw.disk.web.domain.usergroup.repository.UserGroupRepository;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageOwner;
import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class UserGroupServiceImpl implements UserGroupService,
        UserGroupSearchService, SystemResourceProvider {
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final Validator groupValidator;

    public UserGroupServiceImpl(UserGroupRepository userGroupRepository,
                                UserGroupMemberRepository userGroupMemberRepository,
                                ValidatorProvider validatorProvider) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.groupValidator = validatorProvider.getValidator(SystemResourceKind.USER_GROUP);
    }

    @Override
    public void createUserGroup(String name, String description) {
        groupValidator.validateThrows(name, FieldType.NAME);
        groupValidator.validateThrows(description, FieldType.DESCRIPTION);

        UserGroup userGroup = userGroupRepository.getByName(name);
        if (userGroup != null && !userGroup.isDeleted()) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_NAME_EXIST);
        }
        long now = System.currentTimeMillis();
        if (userGroup != null) {
            UserGroup updated = userGroup.toBuilder()
                    .setDescription(description)
                    .setUpdateTime(now)
                    .setSettings(Map.of())
                    .build();
            userGroupRepository.update(updated);
            OperationContextHolder.getContext()
                    .setChangedContent(updated.getName())
                    .addSystemResource(updated);
            return;
        }
        UserGroup newUserGroup = UserGroup.builder()
                .setName(name)
                .setDescription(description)
                .setDeleted(false)
                .setSettings(Map.of())
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        long userGroupId = userGroupRepository.insert(newUserGroup);
        UserGroup inserted = newUserGroup.toBuilder()
                .setId(userGroupId)
                .build();
        OperationContextHolder.getContext()
                .setChangedContent(newUserGroup.getName())
                .addSystemResource(inserted);
    }

    @Override
    public UserGroupInfo findUserGroup(long userGroupId) {
        if (userGroupId == 0) {
            return UserGroupInfo.DEFAULT;
        }

        UserGroup userGroup = userGroupRepository.getById(userGroupId);
        if (userGroup == null) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_NOT_FOUND);
        }
        return UserGroupInfo.from(userGroup);
    }

    @Override
    public UserGroupInfo findUserGroupsByUser(StorageOwner storageOwner) {
        UserGroupMember userGroupMember =
                userGroupMemberRepository.getByUser(storageOwner);
        if (userGroupMember == null) {
            return findUserGroup(0);
        }
        long groupId = userGroupMember.getGroupId();
        return findUserGroup(groupId);
    }

    @Override
    public List<? extends StorageOwner> findUserGroupMembers(long userGroupId) {
        if (userGroupId == 0) {
            return List.of();
        }
        List<UserGroupMember> userGroupMembers =
                userGroupMemberRepository.getByGroup(userGroupId);
        if (userGroupMembers.isEmpty()) {
            return List.of();
        }

        return userGroupMembers.stream()
                .map(userGroupMember -> new SimpleStorageOwner(
                        userGroupMember.getUserId(),
                        userGroupMember.getUserType()
                ))
                .toList();
    }

    @Override
    public List<UserGroupInfo> getUserGroups(Pageable pageable) {
        List<UserGroup> userGroups =
                userGroupRepository.get(pageable.toOffset());

        return userGroups.stream()
                .map(UserGroupInfo::from)
                .toList();
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER_GROUP;
    }

    @Override
    public UserGroupInfo provide(long resourceId, SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException {
        if (systemResourceKind != SystemResourceKind.USER_GROUP) {
            throw new UnsupportedKindException(systemResourceKind);
        }
        return findUserGroup(resourceId);
    }
}
