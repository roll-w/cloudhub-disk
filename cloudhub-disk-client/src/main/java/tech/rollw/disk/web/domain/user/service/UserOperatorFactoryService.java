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

package tech.rollw.disk.web.domain.user.service;

import tech.rollw.disk.web.domain.user.User;
import tech.rollw.disk.web.domain.user.UserOperator;
import tech.rollw.disk.web.domain.user.common.UserException;
import tech.rollw.disk.web.domain.user.filter.UserInfoFilter;
import tech.rollw.disk.web.domain.user.repository.UserRepository;
import tech.rollw.disk.common.UserErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.rollw.disk.web.domain.systembased.*;

/**
 * @author RollW
 */
@Service
public class UserOperatorFactoryService implements SystemResourceOperatorFactory, UserOperatorDelegate {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoFilter userInfoFilter;

    public UserOperatorFactoryService(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      UserInfoFilter userInfoFilter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoFilter = userInfoFilter;
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return UserOperator.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        User user = tryGetUser(systemResource);

        return new UserOperatorImpl(user, this, checkDelete);
    }

    private User tryGetUser(SystemResource systemResource) {
        if (systemResource.getSystemResourceKind() != SystemResourceKind.USER) {
            throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        }
        if (systemResource instanceof User user) {
            return user;
        }
        User user = userRepository.getById(systemResource.getResourceId());
        if (user == null) {
            throw new UserException(UserErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist, id: " + systemResource.getResourceId()
            );
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        userRepository.update(user);
    }

    @Override
    public boolean checkUsernameExist(String username) {
        return false;
    }

    @Override
    public boolean checkEmailExist(String email) {
        return false;
    }

    @Override
    public boolean validatePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public UserInfoFilter getUserInfoFilter() {
        return userInfoFilter;
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
