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

package tech.rollw.disk.web.domain.tag;

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.common.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface TagOperator extends SystemResource, SystemResourceOperator {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.TAG;
    }

    @Override
    TagOperator update() throws BusinessRuntimeException;

    @Override
    TagOperator delete() throws BusinessRuntimeException;

    @Override
    TagOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException;

    TagOperator setDescription(String description) throws BusinessRuntimeException;

    /**
     * If the keyword is already in the tag, the weight of the keyword will be updated.
     */
    TagOperator addKeyword(TagKeyword tagKeyword) throws BusinessRuntimeException;

    TagOperator removeKeyword(TagKeyword tagKeyword) throws BusinessRuntimeException;

    @Override
    TagOperator disableAutoUpdate();

    @Override
    TagOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    TagOperator getSystemResource();

    ContentTag getContentTag();
}
