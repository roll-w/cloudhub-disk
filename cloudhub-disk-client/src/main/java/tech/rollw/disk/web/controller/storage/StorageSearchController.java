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

import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.domain.storagesearch.SearchCondition;
import tech.rollw.disk.web.domain.storagesearch.SearchExpressionParser;
import tech.rollw.disk.web.domain.storagesearch.StorageSearchService;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.StorageOwner;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;
import tech.rollw.disk.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class StorageSearchController {
    private final StorageSearchService storageSearchService;
    private final SearchExpressionParser searchExpressionParser;

    public StorageSearchController(StorageSearchService storageSearchService,
                                   SearchExpressionParser searchExpressionParser) {
        this.storageSearchService = storageSearchService;
        this.searchExpressionParser = searchExpressionParser;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/search")
    public HttpResponseEntity<List<StorageVo>> searchStorages(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestParam("expr") String expression) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        List<SearchCondition> searchConditions =
                searchExpressionParser.parse(expression);
        List<? extends AttributedStorage> attributedStorages =
                storageSearchService.searchFor(searchConditions, storageOwner);
        return HttpResponseEntity.success(
                attributedStorages.stream().map(StorageVo::from).toList()
        );
    }
}
