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
import tech.rollw.disk.web.domain.favorites.repository.FavoriteGroupRepository;
import tech.rollw.disk.web.domain.favorites.repository.FavoriteItemRepository;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorFactory;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class FavoriteOperatorFactoryService implements
        SystemResourceOperatorFactory, FavoriteOperatorDelegate {
    private final FavoriteGroupRepository favoriteGroupRepository;
    private final FavoriteItemRepository favoriteItemRepository;

    public FavoriteOperatorFactoryService(FavoriteGroupRepository favoriteGroupRepository,
                                          FavoriteItemRepository favoriteItemRepository) {
        this.favoriteGroupRepository = favoriteGroupRepository;
        this.favoriteItemRepository = favoriteItemRepository;
    }

    @Override
    public void updateFavoriteGroup(FavoriteGroup favoriteGroup) {
        favoriteGroupRepository.update(favoriteGroup);
    }

    @Override
    public void updateFavoriteItem(FavoriteItem favoriteItem) {
        favoriteItemRepository.update(favoriteItem);
    }

    @Override
    public long createFavoriteItem(FavoriteItem favoriteItem) {
        return favoriteItemRepository.insert(favoriteItem);
    }

    @Override
    public FavoriteItem getFavoriteItemBy(long groupId,
                                          StorageIdentity storageIdentity) {
        return favoriteItemRepository.getByGroupAndIdentity(groupId, storageIdentity);
    }

    @Override
    public FavoriteItem getFavoriteItem(long itemId) {
        return favoriteItemRepository.getById(itemId);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FAVORITE_GROUP;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return FavoriteOperator.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        if (systemResource instanceof FavoriteGroup favoriteGroup) {
            return new FavoriteOperatorImpl(
                    this, favoriteGroup, checkDelete);
        }

        if (systemResource.getResourceId() <= 0) {
            FavoriteGroup favoriteGroup =
                    getDefaultGroup(systemResource.getResourceId());
            return new FavoriteOperatorImpl(
                    this, favoriteGroup, checkDelete);
        }

        FavoriteGroup favoriteGroup = favoriteGroupRepository
                .getById(systemResource.getResourceId());
        if (favoriteGroup == null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return new FavoriteOperatorImpl(
                this, favoriteGroup, checkDelete);
    }

    private FavoriteGroup getDefaultGroup(long id) {
        return switch ((int) id) {
            case 0 -> FavoriteGroup.SYSTEM_FAVORITE_GROUP;
            case -1 -> FavoriteGroup.RECYCLE_BIN;
            default -> throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        };
    }
}
