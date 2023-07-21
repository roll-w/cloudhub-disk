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

package tech.rollw.disk.web.domain.operatelog.dto;

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public record OperationAssociation(
        long resourceId,
        SystemResourceKind systemResourceKind
) implements SystemResource {
    @Override
    public long getResourceId() {
        return resourceId;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return systemResourceKind;
    }
}
