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

package tech.rollw.disk.web.domain.systembased;

import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.common.AuthErrorCode;

/**
 * @author RollW
 */
public class SimpleSystemAuthentication implements SystemAuthentication {
    private final SystemResource systemResource;
    private final Operator operator;
    private final boolean allow;

    public SimpleSystemAuthentication(SystemResource systemResource,
                                      Operator operator,
                                      boolean allow) {
        this.systemResource = systemResource;
        this.operator = operator;
        this.allow = allow;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean isAllowAccess() {
        return allow;
    }

    @Override
    public SystemResource getSystemResource() {
        return systemResource;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean isAllow() {
        return allow;
    }

    @Override
    public void throwAuthenticationException() throws AuthenticationException {
        if (!isAuthenticated()) {
            throw new AuthenticationException(AuthErrorCode.ERROR_UNKNOWN_AUTH,
                    "Cannot authenticate the current user with given resource: " + systemResource.getSystemResourceKind());
        }

        if (!isAllowAccess()) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE,
                    "You have no permission to access this resource.");
        }
    }
}
