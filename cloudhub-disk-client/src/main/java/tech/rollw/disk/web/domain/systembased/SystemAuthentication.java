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

/**
 * @author RollW
 */
public interface SystemAuthentication {
    SystemResource getSystemResource();

    Operator getOperator();

    boolean isAuthenticated();

    boolean isAllowAccess();

    /**
     * Throws an {@link AuthenticationException} if the user is not authenticated.
     */
    void throwAuthenticationException() throws AuthenticationException;
}
