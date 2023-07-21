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

import tech.rollw.disk.common.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface SystemResourceProvider extends SystemResourceSupportable {
    @Override
    boolean supports(SystemResourceKind systemResourceKind);

    /**
     * Provide a system resource by id and kind.
     *
     * @param resourceId         the id of the system resource
     * @param systemResourceKind the kind of the system resource
     * @return the system resource
     * @throws UnsupportedKindException if the system resource kind is not supported.
     */
    SystemResource provide(long resourceId,
                           SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException;
}
