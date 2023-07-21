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

package tech.rollw.disk.web.domain.user.vo;

import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.Role;
import tech.rollw.disk.web.domain.user.UserIdentity;

/**
 * @author RollW
 */
public record UserDetailsVo(
        long userId,
        Role role,
        String username,
        String email,
        boolean enabled,
        boolean locked,
        boolean canceled,
        long createdAt,
        long updatedAt,
        String nickname
) {

    public static UserDetailsVo of(UserIdentity userIdentity) {
        if (userIdentity instanceof AttributedUser user) {
            return of(user);
        }
        return null;
    }

    public static UserDetailsVo of(AttributedUser user) {
        return new UserDetailsVo(
                user.getUserId(),
                user.getRole(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.isLocked(),
                user.isCanceled(),
                user.getCreateTime(),
                user.getUpdateTime(),
                user.getNickname()
        );
    }
}
