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
public interface SystemResourceOperator extends ByStatusProvider, Castable {
    /**
     * For some system resources that may not be updated automatically
     * (such as some batch operations), you need to call this method
     * to update.
     */
    SystemResource update() throws BusinessRuntimeException;

    SystemResource delete() throws BusinessRuntimeException;

    SystemResource rename(String newName) throws BusinessRuntimeException,
            UnsupportedOperationException;

    default SystemResourceOperator disableAutoUpdate() {
        throw new UnsupportedOperationException("The system resource operator "
                + getSystemResource().getSystemResourceKind()
                + " does not support switch auto update.");
    }

    default SystemResourceOperator enableAutoUpdate() {
        throw new UnsupportedOperationException("The system resource operator "
                + getSystemResource().getSystemResourceKind()
                + " does not support switch auto update.");
    }

    default boolean isAutoUpdateEnabled() {
        return true;
    }

    SystemResource getSystemResource();

    default <T extends SystemResource> T getSystemResource(Class<T> type) {
        SystemResource systemResource = getSystemResource();
        if (type.isInstance(systemResource)) {
            return type.cast(systemResource);
        }
        throw new IllegalArgumentException("The system resource is not a "
                + type.getName() +
                " instance, it is a "
                + systemResource.getClass().getName()
                + " instance."
        );
    }
}
