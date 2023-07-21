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

package tech.rollw.disk.web.database.dao;

import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.usergroup.UserGroupMember;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserGroupMemberDao extends AutoPrimaryBaseDao<UserGroupMember> {
    @Override
    @Query("SELECT * FROM user_group_member WHERE deleted = 0")
    List<UserGroupMember> getActives();

    @Override
    @Query("SELECT * FROM user_group_member WHERE deleted = 1")
    List<UserGroupMember> getInactives();

    @Override
    @Query("SELECT * FROM user_group_member WHERE id = {id}")
    UserGroupMember getById(long id);

    @Override
    @Query("SELECT * FROM user_group_member WHERE id IN ({ids})")
    List<UserGroupMember> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_group_member WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_group_member WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_group_member ORDER BY id DESC")
    List<UserGroupMember> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_group_member")
    int count();

    @Override
    @Query("SELECT * FROM user_group_member ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserGroupMember> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_group_member";
    }

    @Query("SELECT * FROM user_group_member WHERE user_id = {userId} AND user_type = {userType}")
    UserGroupMember getByUser(long userId, LegalUserType userType);

    @Query("SELECT * FROM user_group_member WHERE group_id = {groupId}")
    List<UserGroupMember> getByGroup(long groupId);
}
