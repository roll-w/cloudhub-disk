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

/**
 * @author RollW
 */
public interface SystemResourceOperatorFactory extends SystemResourceSupportable {
    @Override
    boolean supports(SystemResourceKind systemResourceKind);

    boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz);

    SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            boolean checkDelete
    );

    default SystemResourceOperator createResourceOperator(SystemResource systemResource) {
        return createResourceOperator(systemResource, true);
    }

    /**
     * Open a new resource operator through its related resource.
     * Such as use Storage to open an operator of StoragePermission.
     * <p>
     * Will check the target system resource kind is supported by
     * this factory.
     */
    default SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind,
            boolean checkDelete
    ) {
        throw new UnsupportedOperationException();
    }

    default SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind
    ) {
        return createResourceOperator(systemResource, targetSystemResourceKind, true);
    }
}
