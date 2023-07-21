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
import tech.rollw.disk.web.domain.favorites.FavoriteProvider;
import tech.rollw.disk.web.domain.favorites.FavoriteService;
import tech.rollw.disk.web.domain.favorites.common.FavoriteErrorCode;
import tech.rollw.disk.web.domain.favorites.common.FavoriteException;
import tech.rollw.disk.web.domain.favorites.dto.FavoriteGroupInfo;
import tech.rollw.disk.web.domain.favorites.dto.FavoriteItemInfo;
import tech.rollw.disk.web.domain.favorites.repository.FavoriteGroupRepository;
import tech.rollw.disk.web.domain.favorites.repository.FavoriteItemRepository;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceProvider;
import tech.rollw.disk.web.domain.systembased.UnsupportedKindException;
import tech.rollw.disk.web.domain.systembased.validate.FieldType;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.systembased.validate.ValidatorProvider;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.common.BusinessRuntimeException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FavoriteServiceImpl implements
        FavoriteService, FavoriteProvider, SystemResourceProvider {
    private final FavoriteGroupRepository favoriteGroupRepository;
    private final FavoriteItemRepository favoriteItemRepository;
    private final Validator validator;

    public FavoriteServiceImpl(FavoriteGroupRepository favoriteGroupRepository,
                               FavoriteItemRepository favoriteItemRepository,
                               ValidatorProvider validatorProvider) {
        this.favoriteGroupRepository = favoriteGroupRepository;
        this.favoriteItemRepository = favoriteItemRepository;
        this.validator = validatorProvider.getValidator(SystemResourceKind.FAVORITE_GROUP);
    }

    @Override
    public List<FavoriteGroupInfo> getFavoriteGroups() {
        return favoriteGroupRepository.get()
                .stream()
                .map(FavoriteGroupInfo::of)
                .toList();
    }

    @Override
    public List<FavoriteGroupInfo> getFavoriteGroups(StorageOwner storageOwner) {
        return favoriteGroupRepository.getGroupsOf(storageOwner)
                .stream()
                .map(FavoriteGroupInfo::of)
                .toList();
    }

    @Override
    public List<FavoriteItemInfo> getFavoriteItems(
            long favoriteGroupId) {
        return favoriteItemRepository.getByGroup(favoriteGroupId)
                .stream()
                .map(FavoriteItemInfo::of)
                .toList();
    }

    @Override
    public FavoriteGroupInfo getFavoriteGroup(long favoriteGroupId) {
        if (favoriteGroupId == 0) {
            return FavoriteGroupInfo.DEFAULT;
        }
        if (favoriteGroupId == -1) {
            // TODO: integrate with recycle bin
            return FavoriteGroupInfo.RECYCLE_BIN;
        }

        FavoriteGroup favoriteGroup =
                favoriteGroupRepository.getById(favoriteGroupId);
        if (favoriteGroup == null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return FavoriteGroupInfo.of(favoriteGroup);
    }

    @Override
    public FavoriteItemInfo getFavoriteItem(long itemId) {
        FavoriteItem favoriteItem = favoriteItemRepository.getById(itemId);
        if (favoriteItem == null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return FavoriteItemInfo.of(favoriteItem);
    }

    @Override
    public void createFavoriteGroup(String name,
                                    boolean isPublic,
                                    Operator of) {
        validator.validateThrows(name, FieldType.NAME);
        FavoriteGroup existed =
                favoriteGroupRepository.getByName(name, of);
        if (existed != null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_EXISTED);
        }
        long now = System.currentTimeMillis();
        FavoriteGroup favoriteGroup = FavoriteGroup.builder()
                .setName(name)
                .setUserId(of.getOperatorId())
                .setPublic(isPublic)
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        favoriteGroupRepository.insert(favoriteGroup);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FAVORITE_GROUP ||
                systemResourceKind == SystemResourceKind.FAVORITE_ITEM;
    }

    @Override
    public SystemResource provide(long resourceId,
                                  SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException {
        return switch (systemResourceKind) {
            case FAVORITE_GROUP -> getFavoriteGroup(resourceId);
            case FAVORITE_ITEM -> getFavoriteItem(resourceId);
            default -> throw new UnsupportedKindException(systemResourceKind);
        };
    }
}
