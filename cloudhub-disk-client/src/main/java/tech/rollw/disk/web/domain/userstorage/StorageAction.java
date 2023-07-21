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

package tech.rollw.disk.web.domain.userstorage;

import tech.rollw.disk.web.BaseAbility;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.userstorage.common.StorageException;
import tech.rollw.disk.common.BusinessRuntimeException;

/**
 * 存储相关操作接口，自动在上下文记录操作日志。
 *
 * @author RollW
 */
@BaseAbility
public interface StorageAction extends AttributedStorage, SystemResourceOperator {
    StorageAction delete() throws StorageException;

    @Override
    StorageAction update() throws BusinessRuntimeException;

    void restore() throws StorageException;

    void create() throws StorageException;

    @Override
    StorageAction rename(String newName) throws StorageException;

    void move(long newParentId) throws StorageException;

    StorageAction copy(long newParentId) throws StorageException;

    @Override
    StorageAction getSystemResource();
}
