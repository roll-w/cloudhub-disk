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

package tech.rollw.disk.web.domain.usergroup.vo;

import tech.rollw.disk.web.domain.usergroup.dto.UserGroupInfo;
import tech.rollw.disk.common.KeyValue;

import java.util.List;

/**
 * @author RollW
 */
public record UserGroupVo(
        long id,
        String name,
        String description,
        List<KeyValue> settings,
        long createTime,
        long updateTime
) {
    public static UserGroupVo from(UserGroupInfo userGroupInfo) {
        return new UserGroupVo(
                userGroupInfo.id(),
                userGroupInfo.name(),
                userGroupInfo.description(),
                KeyValue.from(userGroupInfo.settings()),
                userGroupInfo.createTime(),
                userGroupInfo.updateTime()
        );
    }
}
