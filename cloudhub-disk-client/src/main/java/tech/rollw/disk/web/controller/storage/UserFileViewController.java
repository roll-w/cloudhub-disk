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

package tech.rollw.disk.web.controller.storage;

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.systembased.*;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageCategoryService;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.domain.userstorage.UserStorageSearchService;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserFileViewController {
    private final StorageCategoryService storageCategoryService;
    private final UserStorageSearchService storageSearchService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;
    private final SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory;

    public UserFileViewController(StorageCategoryService storageCategoryService,
                                  UserStorageSearchService storageSearchService,
                                  ContextThreadAware<PageableContext> pageableContextThreadAware,
                                  SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory) {
        this.storageCategoryService = storageCategoryService;
        this.storageSearchService = storageSearchService;
        this.pageableContextThreadAware = pageableContextThreadAware;
        this.systemResourceAuthenticationProviderFactory = systemResourceAuthenticationProviderFactory;
    }


    @GetMapping("/{ownerType}/{ownerId}/disk/storages")
    public HttpResponseEntity<List<StorageVo>> getAllFiles(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerType) {
        StorageOwner storageOwner =
                ParameterHelper.buildStorageOwner(ownerId, ownerType);
        UserIdentity user = ApiContextHolder.getContext().userInfo();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);
        List<AttributedStorage> attributedStorages =
                storageSearchService.listStorages(storageOwner);

        SystemResourceAuthenticationProvider authenticationProvider = systemResourceAuthenticationProviderFactory
                .getSystemResourceAuthenticationProvider(SystemResourceKind.FILE);
        List<SystemAuthentication> authentications =
                authenticationProvider.authenticate(attributedStorages, user, Action.ACCESS);
        List<AttributedStorage> authenticatedStorages =
                authentications.stream().filter(SystemAuthentication::isAllowAccess)
                        .map(SystemAuthentication::getSystemResource)
                        .map(resource -> resource.cast(AttributedStorage.class))
                        .toList();
        List<StorageVo> storageVos = authenticatedStorages.stream()
                .map(StorageVo::from)
                .toList();
        return HttpResponseEntity.success(
                pageableContext.toPage(storageVos)
        );
    }
}
