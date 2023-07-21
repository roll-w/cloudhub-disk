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

package tech.rollw.disk.web.domain.usergroup;

import tech.rollw.disk.web.domain.usergroup.dto.UserGroupInfo;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.common.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface UserGroupSearchService {
    UserGroupInfo findUserGroup(long userGroupId);

    UserGroupInfo findUserGroupsByUser(StorageOwner storageOwner);

    List<? extends StorageOwner> findUserGroupMembers(long userGroupId);

    List<UserGroupInfo> getUserGroups(Pageable pageable);

}
