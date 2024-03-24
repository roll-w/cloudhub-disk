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

package tech.rollw.disk.web.domain.favorites;

import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;

/**
 * @author RollW
 */
public interface FavoriteOperator extends SystemResourceOperator, SystemResource {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    FavoriteOperator update() throws BusinessRuntimeException;

    @Override
    FavoriteOperator delete() throws BusinessRuntimeException;

    @Override
    FavoriteOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException;

    @Override
    FavoriteOperator getSystemResource();

    FavoriteOperator setVisibility(boolean publicVisible);

    FavoriteOperator addFavorite(StorageIdentity storageIdentity, Operator operator);

    FavoriteOperator removeFavorite(StorageIdentity storageIdentity);

    FavoriteOperator removeFavorite(long itemId);

    FavoriteGroup getFavoriteGroup();



    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.FAVORITE_GROUP;
    }
}
