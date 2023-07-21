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

package tech.rollw.disk.web.domain.usergroup.dto;

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public record UserGroupDetails(
        long id,
        String name,
        String description,
        Map<String, String> settings,
        long createTime,
        long updateTime,
        boolean deleted,
        List<StorageOwner> members
) implements SystemResource {
    @Override
    public long getResourceId() {
        return id;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER_GROUP;
    }
}
