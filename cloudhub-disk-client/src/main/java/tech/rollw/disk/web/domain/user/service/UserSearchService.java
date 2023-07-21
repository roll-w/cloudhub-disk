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

import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.user.common.UserException;
import tech.rollw.disk.common.data.page.Pageable;
import space.lingu.NonNull;

import java.util.List;

/**
 * Provides to user APIs, which are used to get user information.
 * Admin APIs should use {@link UserManageService}.
 *
 * @author RollW
 */
public interface UserSearchService extends UserProvider {
    /**
     * Get user by id. And enables check if user is deleted or canceled.
     *
     * @throws UserException if user is deleted or canceled.
     */
    @Override
    AttributedUser findUser(long userId) throws UserException;


    @Override
    AttributedUser findUser(String username) throws UserException;

    List<AttributedUser> findUsers(@NonNull String keyword);

    /**
     * Get user by id. And enables check if user is deleted or canceled.
     *
     * @throws UserException if user is deleted or canceled.
     */
    AttributedUser findUser(UserIdentity userIdentity) throws UserException;

    List<? extends AttributedUser> findUsers(Pageable pageable);

    List<? extends AttributedUser> findUsers();

    List<? extends AttributedUser> findUsers(List<Long> ids);

    static AttributedUser binarySearch(long id, List<? extends AttributedUser> attributedUsers) {
        List<? extends AttributedUser> sorted = attributedUsers.stream()
                .sorted((o1, o2) -> (int) (o1.getUserId() - o2.getUserId()))
                .toList();
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            AttributedUser attributedUser = sorted.get(mid);
            if (attributedUser.getUserId() < id) {
                low = mid + 1;
            } else if (attributedUser.getUserId() > id) {
                high = mid - 1;
            } else {
                return attributedUser;
            }
        }
        return null;
    }
}
