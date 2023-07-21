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

package tech.rollw.disk.web.domain.favorites.dto;

import tech.rollw.disk.web.domain.favorites.FavoriteItem;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record FavoriteItemInfo(
        long id,
        long favoriteGroupId,
        long userId,
        long storageId,
        StorageType storageType,
        long createTime,
        long updateTime,
        boolean deleted
) implements SystemResource, StorageIdentity {
    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public long getResourceId() {
        return id;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.FAVORITE_ITEM;
    }

    public static FavoriteItemInfo of(FavoriteItem favoriteItem) {
        return new FavoriteItemInfo(
                favoriteItem.getId(),
                favoriteItem.getFavoriteGroupId(),
                favoriteItem.getUserId(),
                favoriteItem.getStorageId(),
                favoriteItem.getStorageType(),
                favoriteItem.getCreateTime(),
                favoriteItem.getUpdateTime(),
                favoriteItem.isDeleted()
        );
    }
}
