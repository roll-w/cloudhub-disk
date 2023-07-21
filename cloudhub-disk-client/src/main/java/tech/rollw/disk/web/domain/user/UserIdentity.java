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

import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public interface UserIdentity extends Operator, StorageOwner, SystemResource {
    long getUserId();

    @Override
    default long getOperatorId() {
        return getUserId();
    }

    String getUsername();

    String getEmail();

    Role getRole();

    @Override
    default long getResourceId() {
        return getUserId();
    }

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER;
    }

    @Override
    default long getOwnerId() {
        return getUserId();
    }

    @Override
    default LegalUserType getOwnerType() {
        return LegalUserType.USER;
    }
}
