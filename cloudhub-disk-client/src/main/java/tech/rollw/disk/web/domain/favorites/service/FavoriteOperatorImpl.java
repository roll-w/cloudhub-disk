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

package tech.rollw.disk.web.domain.favorites.service;

import tech.rollw.disk.web.domain.favorites.FavoriteGroup;
import tech.rollw.disk.web.domain.favorites.FavoriteItem;
import tech.rollw.disk.web.domain.favorites.FavoriteOperator;
import tech.rollw.disk.web.domain.favorites.common.FavoriteErrorCode;
import tech.rollw.disk.web.domain.favorites.common.FavoriteException;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.common.DataErrorCode;

/**
 * @author RollW
 */
public class FavoriteOperatorImpl implements FavoriteOperator {
    private final FavoriteOperatorDelegate delegate;

    private FavoriteGroup favoriteGroup;
    private final FavoriteGroup.Builder favoriteGroupBuilder;
    private boolean checkDeleted;

    public FavoriteOperatorImpl(FavoriteOperatorDelegate delegate,
                                FavoriteGroup favoriteGroup,
                                boolean checkDeleted) {
        this.delegate = delegate;
        this.favoriteGroup = favoriteGroup;
        this.favoriteGroupBuilder = favoriteGroup.toBuilder();
        this.checkDeleted = checkDeleted;
    }


    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        this.checkDeleted = checkDeleted;
    }

    @Override
    public boolean isCheckDeleted() {
        return checkDeleted;
    }

    @Override
    public FavoriteOperator update() throws BusinessRuntimeException {
        return this;
    }

    @Override
    public FavoriteOperator delete() throws BusinessRuntimeException {
        checkDeleted();
        favoriteGroupBuilder.setDeleted(true);
        return updateInternal();
    }

    @Override
    public FavoriteOperator rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException {
        checkDeleted();
        favoriteGroupBuilder.setName(newName);

        return updateInternal();
    }

    @Override
    public FavoriteOperator getSystemResource() {
        return this;
    }

    @Override
    public FavoriteOperator setVisibility(boolean publicVisible) {
        checkDeleted();

        favoriteGroupBuilder.setPublic(publicVisible);

        return updateInternal();
    }

    @Override
    public FavoriteOperator addFavorite(StorageIdentity storageIdentity, Operator operator) {
        checkDeleted();

        FavoriteItem favoriteItem =
                delegate.getFavoriteItemBy(favoriteGroup.getId(), storageIdentity);
        if (favoriteItem != null && !favoriteItem.isDeleted()) {
            throw new BusinessRuntimeException(DataErrorCode.ERROR_DATA_EXISTED);
        }
        if (favoriteItem != null) {
            delegate.updateFavoriteItem(favoriteItem.toBuilder()
                    .setDeleted(false)
                    .setUpdateTime(System.currentTimeMillis())
                    .build()
            );
            return this;
        }

        long time = System.currentTimeMillis();
        FavoriteItem.Builder favoriteItemBuilder = FavoriteItem.builder()
                .setFavoriteGroupId(favoriteGroup.getId())
                .setUserId(operator.getOperatorId())
                .setStorageId(storageIdentity.getStorageId())
                .setStorageType(storageIdentity.getStorageType())
                .setDeleted(false)
                .setCreateTime(time)
                .setUpdateTime(time);
        long id = delegate.createFavoriteItem(favoriteItemBuilder.build());
        return this;
    }

    @Override
    public FavoriteOperator removeFavorite(StorageIdentity storageIdentity) {
        checkDeleted();
        FavoriteItem favoriteItem =
                delegate.getFavoriteItemBy(favoriteGroup.getId(), storageIdentity);
        if (favoriteItem != null) {
            delegate.updateFavoriteItem(favoriteItem.toBuilder()
                    .setDeleted(true)
                    .setUpdateTime(System.currentTimeMillis())
                    .build()
            );
        }
        return this;
    }

    @Override
    public FavoriteOperator removeFavorite(long itemId) {
        checkDeleted();

        FavoriteItem favoriteItem = delegate.getFavoriteItem(itemId);
        if (favoriteItem != null) {
            delegate.updateFavoriteItem(favoriteItem.toBuilder()
                    .setDeleted(true)
                    .setUpdateTime(System.currentTimeMillis())
                    .build()
            );
        }
        return this;
    }

    @Override
    public FavoriteGroup getFavoriteGroup() {
        return favoriteGroup;
    }

    @Override
    public long getResourceId() {
        return favoriteGroup.getId();
    }

    private FavoriteOperator updateInternal() {
        if (favoriteGroup.getId() <= 0) {
            return this;
        }

        if (favoriteGroupBuilder != null) {
            favoriteGroup = favoriteGroupBuilder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateFavoriteGroup(favoriteGroup);
        }
        return this;
    }

    private void checkDeleted() {
        if (!checkDeleted) {
            return;
        }
        if (favoriteGroup.isDeleted()) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
    }

}
