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

import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.systembased.validate.ValidatorProvider;
import tech.rollw.disk.web.domain.usergroup.UserGroup;
import tech.rollw.disk.web.domain.usergroup.UserGroupMember;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupErrorCode;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupException;
import tech.rollw.disk.web.domain.usergroup.repository.UserGroupMemberRepository;
import tech.rollw.disk.web.domain.usergroup.repository.UserGroupRepository;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import org.springframework.stereotype.Service;
import tech.rollw.disk.web.domain.systembased.*;

/**
 * @author RollW
 */
@Service
public class UserGroupOperatorFactoryService
        implements SystemResourceOperatorFactory, UserGroupOperatorDelegate {
    private final Validator validator;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;

    public UserGroupOperatorFactoryService(ValidatorProvider validatorProvider,
                                           UserGroupRepository userGroupRepository,
                                           UserGroupMemberRepository userGroupMemberRepository) {
        validator = validatorProvider.getValidator(SystemResourceKind.USER_GROUP);
        this.userGroupRepository = userGroupRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER_GROUP;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return UserGroupOperatorDelegate.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        if (!supports(systemResource.getSystemResourceKind())) {
            throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        }
        if (systemResource instanceof UserGroup userGroup) {
            return new UserGroupOperatorImpl(userGroup, this, checkDelete);
        }
        UserGroup userGroup =
                userGroupRepository.getById(systemResource.getResourceId());
        if (userGroup == null) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_NOT_FOUND);
        }
        return new UserGroupOperatorImpl(userGroup, this, checkDelete);
    }

    @Override
    public void updateUserGroup(UserGroup userGroup) {
        userGroupRepository.update(userGroup);
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public UserGroupMember getUserGroupMember(StorageOwner storageOwner) {
        return userGroupMemberRepository.getByUser(storageOwner);
    }

    @Override
    public Long createUserGroupMember(UserGroupMember userGroupMember) {
        return userGroupMemberRepository.insert(userGroupMember);
    }

    @Override
    public void updateUserGroupMember(UserGroupMember userGroupMember) {
        userGroupMemberRepository.update(userGroupMember);
    }
}
