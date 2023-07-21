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

package tech.rollw.disk.web.system.pages;

import tech.rollw.disk.web.database.DataItem;
import tech.rollw.disk.common.data.page.Page;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * @author RollW
 */
@Component
public class PagesCachePageableInterceptor implements PageableInterceptor {
    private final PagesCache pagesCache;

    public PagesCachePageableInterceptor(PagesCache pagesCache) {
        this.pagesCache = pagesCache;
    }

    @Override
    public <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                         Pageable parameter,
                                         Class<? extends DataItem> typeClazz) {
        return interceptPageable(supplier, parameter, typeClazz, false);
    }

    @Override
    public <T> Page<T> interceptPageable(List<T> list, Pageable parameter,
                                         Class<? extends DataItem> typeClazz) {
        return interceptPageable(list, parameter, typeClazz, false);
    }

    @Override
    public <T> Page<T> interceptPageable(Supplier<List<T>> supplier, Pageable parameter,
                                         Class<? extends DataItem> typeClazz,
                                         boolean active) {
        long count = getCount(active, typeClazz);
        return Page.of(parameter, count, supplier.get());
    }

    @Override
    public <T> Page<T> interceptPageable(List<T> list, Pageable parameter,
                                         Class<? extends DataItem> typeClazz,
                                         boolean active) {
        long count = getCount(active, typeClazz);
        return Page.of(parameter, count, list);
    }

    @Override
    public <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                         Pageable parameter,
                                         LongSupplier countSupplier) {
        return Page.of(
                parameter,
                countSupplier.getAsLong(),
                supplier.get()
        );
    }

    @Override
    public <T> Page<T> interceptPageable(List<T> list,
                                         Pageable parameter,
                                         LongSupplier countSupplier) {
        return Page.of(
                parameter,
                countSupplier.getAsLong(),
                list
        );
    }


    private long getCount(boolean active, Class<? extends DataItem> typeClazz) {
        return active
                ? pagesCache.getActiveCount(typeClazz)
                : pagesCache.getCount(typeClazz);
    }
}
