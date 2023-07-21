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

package tech.rollw.disk.web.database.dao;

import tech.rollw.disk.web.database.DataItem;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;

import java.util.List;

/**
 * @author RollW
 */
public interface AutoPrimaryBaseDao<T extends DataItem> extends BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertReturns(T t);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long[] insertReturns(List<T> ts);

    default List<T> getActives() {
        return List.of();
    }

    default List<T> getInactives() {
        return List.of();
    }

    default T getById(long id) {
        return null;
    }

    default List<T> getByIds(List<Long> ids) {
        return List.of();
    }

    default int countActive() {
        return 0;
    }

    default int countInactive() {
        return 0;
    }

    @Override
    default List<T> get() {
        return BaseDao.super.get();
    }

    @Override
    default int count() {
        return BaseDao.super.count();
    }

    @Override
    default List<T> get(Offset offset) {
        return BaseDao.super.get(offset);
    }

    @Override
    default String getTableName() {
        return BaseDao.super.getTableName();
    }
}
