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

import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * @author RollW
 */
public interface PageableInterceptor {
    <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz);

    <T> Page<T> interceptPageable(List<T> list,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz);

    <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz,
                                  boolean active);

    <T> Page<T> interceptPageable(List<T> list,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz,
                                  boolean active);

    <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                  Pageable parameter,
                                  LongSupplier countSupplier);

    <T> Page<T> interceptPageable(List<T> list,
                                  Pageable parameter,
                                  LongSupplier countSupplier);

    default <D extends PageableDataTransferObject<T>, T extends DataItem> Page<D> interceptPageable(
            Supplier<List<D>> supplier,
            Pageable parameter,
            PageableDataTransferObject<T> transferObject) {
        return interceptPageable(supplier, parameter, transferObject.convertFrom());
    }
}
