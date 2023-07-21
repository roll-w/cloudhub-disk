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

package tech.rollw.disk.web.domain.user;

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.common.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface UserOperator extends SystemResourceOperator,
        SystemResource, AttributedUser {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    UserOperator disableAutoUpdate();

    @Override
    UserOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    UserOperator update() throws BusinessRuntimeException;

    @Override
    UserOperator delete() throws BusinessRuntimeException;

    @Override
    UserOperator rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException;

    UserOperator setNickname(String nickname)
            throws BusinessRuntimeException;

    UserOperator setEmail(String email)
            throws BusinessRuntimeException;

    UserOperator setRole(Role role)
            throws BusinessRuntimeException;

    UserOperator setPassword(String password)
            throws BusinessRuntimeException;

    UserOperator setPassword(String oldPassword, String password)
            throws BusinessRuntimeException;

    UserOperator setEnabled(boolean enabled)
            throws BusinessRuntimeException;

    UserOperator setLocked(boolean locked)
            throws BusinessRuntimeException;

    UserOperator setCanceled(boolean canceled)
            throws BusinessRuntimeException;

    @Override
    UserOperator getSystemResource();


}
