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

import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Delete;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;
import space.lingu.light.Update;

import java.util.List;

/**
 * @author RollW
 */
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(T t);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(List<T> ts);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void update(T t);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void update(List<T> ts);

    @Delete
    void delete(T t);

    @Delete
    void delete(List<T> ts);

    default List<T> get() {
        return List.of();
    }

    default int count() {
        return 0;
    }

    default List<T> get(Offset offset) {
        return List.of();
    }

    default String getTableName() {
        return null;
    }
}
