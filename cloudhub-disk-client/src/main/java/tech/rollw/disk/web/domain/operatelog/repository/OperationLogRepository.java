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

package tech.rollw.disk.web.domain.operatelog.repository;

import tech.rollw.disk.web.database.DiskDatabase;
import tech.rollw.disk.web.database.dao.OperationLogDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.operatelog.OperationLog;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class OperationLogRepository extends BaseRepository<OperationLog> {
    private final OperationLogDao operationLogDao;

    public OperationLogRepository(DiskDatabase database,
                                  ContextThreadAware<PageableContext> pageableContextThreadAware,
                                  CacheManager cacheManager) {
        super(database.getOperationLogDao(), pageableContextThreadAware, cacheManager);
        operationLogDao = database.getOperationLogDao();
    }

    public List<OperationLog> getOperationLogsByResourceId(long resourceId,
                                                           SystemResourceKind resourceKind) {
        List<OperationLog> operationLogs =
                operationLogDao.getOperationLogsByResourceId(resourceId, resourceKind);
        return cacheResult(operationLogs);
    }

    public List<OperationLog> getByOperator(long operator) {
        List<OperationLog> operationLogs =
                operationLogDao.getByOperator(operator);
        return cacheResult(operationLogs);
    }

    public List<OperationLog> getByOperator(long operator, Pageable pageable) {
        return cacheResult(
                operationLogDao.getByOperator(operator, pageable.toOffset())
        );
    }

    public List<OperationLog> getOperationLogsByResourceId(long resourceId,
                                                           SystemResourceKind resourceKind,
                                                           Pageable pageable) {
        return cacheResult(
                operationLogDao.getOperationLogsByResourceId(resourceId, resourceKind, pageable.toOffset())
        );
    }

    @Override
    protected Class<OperationLog> getEntityClass() {
        return OperationLog.class;
    }

    public long countByOperator(long operatorId) {
        return operationLogDao.count(operatorId);
    }

    public long countByResourceId(long resourceId, SystemResourceKind resourceKind) {
        return operationLogDao.count(resourceId, resourceKind);
    }
}
