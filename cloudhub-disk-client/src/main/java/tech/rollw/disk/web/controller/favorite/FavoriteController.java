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

import tech.rollw.disk.common.AuthErrorCode;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.domain.favorites.*;
import tech.rollw.disk.web.domain.favorites.common.FavoriteErrorCode;
import tech.rollw.disk.web.domain.favorites.common.FavoriteException;
import tech.rollw.disk.web.domain.favorites.dto.FavoriteGroupInfo;
import tech.rollw.disk.web.domain.favorites.dto.FavoriteItemInfo;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.systembased.*;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.user.service.UserProvider;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final FavoriteProvider favoriteProvider;
    private final UserStorageSearchService userStorageSearchService;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final SystemResourceAuthenticationProviderFactory authenticationProviderFactory;
    private final UserProvider userProvider;

    public FavoriteController(FavoriteService favoriteService,
                              FavoriteProvider favoriteProvider,
                              UserStorageSearchService userStorageSearchService,
                              SystemResourceOperatorProvider systemResourceOperatorProvider,
                              SystemResourceAuthenticationProviderFactory authenticationProviderFactory,
                              UserProvider userProvider) {
        this.favoriteService = favoriteService;
        this.favoriteProvider = favoriteProvider;
        this.userStorageSearchService = userStorageSearchService;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.authenticationProviderFactory = authenticationProviderFactory;
        this.userProvider = userProvider;
    }

    @GetMapping("/user/favorites")
    public HttpResponseEntity<List<FavoriteGroupInfo>> getFavoriteGroups(
    ) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        List<FavoriteGroupInfo> favoriteGroupInfos =
                favoriteProvider.getFavoriteGroups(userIdentity);
        return HttpResponseEntity.success(favoriteGroupInfos);
    }

    @GetMapping("/users/{userId}/favorites")
    public HttpResponseEntity<List<FavoriteGroupInfo>> getFavoriteGroupsOf(
            @PathVariable Long userId
    ) {
        UserIdentity currentUserIdentity =
                ApiContextHolder.getContext().userInfo();
        UserIdentity userIdentity = userProvider.findUser(userId);
        List<FavoriteGroupInfo> favoriteGroupInfos =
                favoriteProvider.getFavoriteGroups(userIdentity);
        boolean onlyPublic = isOnlyPublic(currentUserIdentity, userIdentity);

        return HttpResponseEntity.success(
                favoriteGroupInfos.stream()
                        .filter(favoriteGroupInfo -> {
                            if (onlyPublic) {
                                return favoriteGroupInfo.isPublic();
                            }
                            return true;
                        })
                        .toList()
        );
    }

    private boolean isOnlyPublic(UserIdentity currentUserIdentity, UserIdentity userIdentity) {
        if (currentUserIdentity == null) {
            return true;
        }

        return currentUserIdentity.getOwnerId() != userIdentity.getOwnerId();
    }

    @GetMapping("/user/favorites/{groupId}/info")
    public HttpResponseEntity<FavoriteGroupInfo> getFavoriteGroupInfo(
            @PathVariable Long groupId
    ) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        FavoriteGroupInfo favoriteGroupInfo =
                favoriteProvider.getFavoriteGroup(groupId);
        if (favoriteGroupInfo.isPublic()) {
            return HttpResponseEntity.success(favoriteGroupInfo);
        }
        if (userIdentity == null) {
            throw new FavoriteException(AuthErrorCode.ERROR_PERMISSION_DENIED);
        }
        if (userIdentity.getOwnerId() != favoriteGroupInfo.id()) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return HttpResponseEntity.success(favoriteGroupInfo);
    }

    @GetMapping("/users/{userId}/favorites/{groupId}/info")
    public HttpResponseEntity<FavoriteGroupInfo> getFavoriteGroupInfo(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        if (userIdentity == null) {
            throw new FavoriteException(AuthErrorCode.ERROR_PERMISSION_DENIED);
        }
        FavoriteGroupInfo favoriteGroupInfo =
                favoriteProvider.getFavoriteGroup(groupId);
        if (favoriteGroupInfo.userId() != userId) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        if (!favoriteGroupInfo.isPublic() &&
                favoriteGroupInfo.userId() != userIdentity.getOwnerId()) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return HttpResponseEntity.success(favoriteGroupInfo);
    }

    @GetMapping("/users/{userId}/favorites/{groupId}")
    public HttpResponseEntity<List<FavoriteItemVo>> getFavorites(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        if (userIdentity == null || userIdentity.getOwnerId() != userId) {
            throw new FavoriteException(AuthErrorCode.ERROR_PERMISSION_DENIED);
        }

        FavoriteGroupInfo favoriteGroupInfo =
                favoriteProvider.getFavoriteGroup(groupId);
        checkOwner(favoriteGroupInfo, userIdentity);

        List<FavoriteItemInfo> favoriteItemInfos = favoriteProvider
                .getFavoriteItems(groupId, userIdentity.getUserId())
                .stream()
                .filter(favoriteItemInfo -> !favoriteItemInfo.deleted())
                .toList();

        List<FavoriteItemVo> favoriteItemVos =
                convertFavoriteItemInfos(favoriteItemInfos);
        return HttpResponseEntity.success(favoriteItemVos);
    }

    private void checkOwner(FavoriteGroupInfo favoriteGroupInfo,
                            UserIdentity userIdentity) {
        if (favoriteGroupInfo.userId() == userIdentity.getOwnerId()) {
            return;
        }
        if (favoriteGroupInfo.id() <= 0) {
            return;
        }
        if (favoriteGroupInfo.isPublic()) {
            return;
        }
        throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
    }

    private List<FavoriteItemVo> convertFavoriteItemInfos(
            List<FavoriteItemInfo> favoriteItemInfos) {
        List<? extends StorageIdentity> storageIdentities = favoriteItemInfos.
                stream().map(favoriteItemInfo -> new SimpleStorageIdentity(
                        favoriteItemInfo.storageId(),
                        favoriteItemInfo.storageType()
                )).toList();
        List<? extends AttributedStorage> storages =
                userStorageSearchService.findStorages(storageIdentities);
        return favoriteItemInfos.stream().map(favoriteItemInfo -> {
            AttributedStorage attributedStorage = storages.stream()
                    .filter(storage -> storage.getStorageId() == favoriteItemInfo.storageId())
                    .findFirst()
                    .orElseThrow(() -> new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND));
            return FavoriteItemVo.of(favoriteItemInfo, attributedStorage);
        }).toList();

    }


    @GetMapping("/users/{userId}/favorites/{groupId}/{itemId}")
    public HttpResponseEntity<FavoriteItemVo> getFavoriteInfo(
            @PathVariable Long groupId,
            @PathVariable Long itemId,
            @PathVariable Long userId) {
        return HttpResponseEntity.success();
    }

    @PostMapping("/user/favorites")
    public HttpResponseEntity<Void> createFavoriteGroup(
            @RequestBody FavoriteGroupCreateRequest request) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        favoriteService.createFavoriteGroup(
                request.name(),
                request.isPublic(),
                userIdentity
        );
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/user/favorites/{groupId}/{itemId}")
    public HttpResponseEntity<Void> deleteFavoriteItem(
            @PathVariable("groupId") Long groupId,
            @PathVariable("itemId") Long itemId) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        FavoriteItemInfo favoriteItemInfo = favoriteProvider.getFavoriteItem(itemId);

        FavoriteOperator favoriteOperator = systemResourceOperatorProvider
                .getSystemResourceOperator(
                        new SimpleSystemResource(
                                groupId,
                                SystemResourceKind.FAVORITE_GROUP
                        ), true
                );
        checkOwner(favoriteOperator.getFavoriteGroup(), userIdentity);

        favoriteOperator.removeFavorite(favoriteItemInfo.id());
        return HttpResponseEntity.success();
    }

    @PostMapping("/user/favorites/{groupId}")
    public HttpResponseEntity<Void> createFavoriteItem(
            @PathVariable("groupId") Long groupId,
            @RequestBody FavoriteItemCreateRequest request) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        FavoriteOperator favoriteOperator = systemResourceOperatorProvider
                .getSystemResourceOperator(
                        new SimpleSystemResource(
                                groupId,
                                SystemResourceKind.FAVORITE_GROUP
                        ), true
                );
        checkOwner(favoriteOperator.getFavoriteGroup(), userIdentity);
        StorageIdentity storageIdentity = request.toStorageIdentity();
        authorizeStorage(storageIdentity, userIdentity);
        favoriteOperator.addFavorite(storageIdentity, userIdentity);
        return HttpResponseEntity.success();
    }

    private void checkOwner(FavoriteGroup favoriteGroupInfo, UserIdentity userIdentity) {
        if (favoriteGroupInfo.getUserId() == userIdentity.getOwnerId()) {
            return;
        }
        if (favoriteGroupInfo.getId() <= 0) {
            return;
        }
        if (favoriteGroupInfo.isPublic()) {
            return;
        }
        throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
    }


    private void authorizeStorage(StorageIdentity storageIdentity,
                                  Operator operator) {
        SystemAuthentication systemAuthentication = authenticationProviderFactory.getSystemResourceAuthenticationProvider(
                storageIdentity.getSystemResourceKind()
        ).authenticate(storageIdentity, operator, Action.ACCESS);
        systemAuthentication.throwAuthenticationException();
    }
}
