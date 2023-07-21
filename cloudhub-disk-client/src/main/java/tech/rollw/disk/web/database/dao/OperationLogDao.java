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

import tech.rollw.disk.web.domain.operatelog.OperationLog;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.common.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface OperationLogDao extends AutoPrimaryBaseDao<OperationLog> {
    @Query("SELECT * FROM operation_log WHERE operate_resource_id = {resourceId} AND resource_kind = {resourceKind} ORDER BY id DESC")
    List<OperationLog> getOperationLogsByResourceId(long resourceId, SystemResourceKind resourceKind);

    @Query("SELECT * FROM operation_log WHERE operate_resource_id = {resourceId} AND resource_kind = {resourceKind} ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<OperationLog> getOperationLogsByResourceId(long resourceId, SystemResourceKind resourceKind, Offset offset);

    @Query("SELECT * FROM operation_log WHERE operator = {operator} ORDER BY id DESC")
    List<OperationLog> getByOperator(long operator);

    @Query("SELECT * FROM operation_log WHERE operator = {operator} ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<OperationLog> getByOperator(long operator, Offset offset);

    @Override
    @Query("SELECT * FROM operation_log WHERE deleted = 0")
    List<OperationLog> getActives();

    @Override
    @Query("SELECT * FROM operation_log WHERE deleted = 1")
    List<OperationLog> getInactives();

    @Override
    @Query("SELECT * FROM operation_log WHERE id = {id}")
    OperationLog getById(long id);

    @Override
    @Query("SELECT * FROM operation_log WHERE id IN ({ids}) ORDER BY id DESC")
    List<OperationLog> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM operation_log WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM operation_log WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM operation_log ORDER BY id DESC")
    List<OperationLog> get();

    @Override
    @Query("SELECT COUNT(*) FROM operation_log")
    int count();

    @Query("SELECT COUNT(*) FROM operation_log WHERE operator = {operator} ORDER BY id DESC")
    int count(long operator);

    @Query("SELECT COUNT(*) FROM operation_log WHERE operate_resource_id = {resourceId} AND resource_kind = {resourceKind} ORDER BY id DESC")
    int count(long resourceId, SystemResourceKind resourceKind);

    @Override
    @Query("SELECT * FROM operation_log ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<OperationLog> get(Offset offset);

    @Override
    default String getTableName() {
        return "operation_log";
    }
}
