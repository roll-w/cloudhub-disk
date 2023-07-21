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

package tech.rollw.disk.web.controller.favorite;

import tech.rollw.disk.web.domain.favorites.dto.FavoriteItemInfo;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record FavoriteItemVo(
        long id,
        long favoriteGroupId,
        long userId,
        long ownerId,
        LegalUserType ownerType,
        String name,
        long storageId,
        StorageType storageType,
        long createTime,
        long updateTime
) {

    public static FavoriteItemVo of(FavoriteItemInfo favoriteItemInfo,
                                    AttributedStorage storage) {
        return new FavoriteItemVo(
                favoriteItemInfo.id(),
                favoriteItemInfo.favoriteGroupId(),
                favoriteItemInfo.userId(),
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getName(),
                favoriteItemInfo.storageId(),
                favoriteItemInfo.storageType(),
                favoriteItemInfo.createTime(),
                favoriteItemInfo.updateTime()
        );
    }
}
