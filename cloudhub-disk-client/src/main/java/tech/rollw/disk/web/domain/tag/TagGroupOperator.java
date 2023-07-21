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
public interface TagGroupOperator extends SystemResource, SystemResourceOperator {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.TAG_GROUP;
    }

    @Override
    TagGroupOperator update() throws BusinessRuntimeException;

    @Override
    TagGroupOperator delete() throws BusinessRuntimeException;

    @Override
    TagGroupOperator rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException;

    TagGroupOperator setDescription(String description);

    TagGroupOperator setKeywordSearchScope(KeywordSearchScope scope);

    TagGroupOperator addTag(long tagId);

    TagGroupOperator removeTag(long tagId);

    TagGroupOperator addTag(SystemResource systemResource);

    TagGroupOperator removeTag(SystemResource systemResource);

    @Override
    TagGroupOperator disableAutoUpdate();

    @Override
    TagGroupOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    TagGroupOperator getSystemResource();

    TagGroup getTagGroup();

}
