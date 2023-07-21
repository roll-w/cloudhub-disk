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
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;

/**
 * @author RollW
 */
public interface FavoriteOperatorDelegate {
    void updateFavoriteGroup(FavoriteGroup favoriteGroup);

    void updateFavoriteItem(FavoriteItem favoriteItem);

    long createFavoriteItem(FavoriteItem favoriteItem);

    FavoriteItem getFavoriteItemBy(long groupId, StorageIdentity storageIdentity);

    FavoriteItem getFavoriteItem(long itemId);
}
