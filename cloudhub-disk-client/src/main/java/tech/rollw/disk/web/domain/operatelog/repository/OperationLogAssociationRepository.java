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
import tech.rollw.disk.web.database.dao.OperationLogAssociationDao;
import tech.rollw.disk.web.database.repository.BaseRepository;
import tech.rollw.disk.web.domain.operatelog.OperationLogAssociation;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class OperationLogAssociationRepository extends BaseRepository<OperationLogAssociation> {
    private final OperationLogAssociationDao operationLogAssociationDao;

    protected OperationLogAssociationRepository(DiskDatabase database,
                                                ContextThreadAware<PageableContext> pageableContextThreadAware,
                                                CacheManager cacheManager) {
        super(database.getOperationLogAssociationDao(), pageableContextThreadAware, cacheManager);
        operationLogAssociationDao = database.getOperationLogAssociationDao();
    }

    public List<OperationLogAssociation> getByOperationId(long operationId) {
        List<OperationLogAssociation> operationLogAssociations =
                operationLogAssociationDao.getByOperationId(operationId);
        return cacheResult(operationLogAssociations);
    }

    public List<OperationLogAssociation> getByOperationIds(List<Long> operationIds) {
        if (operationIds == null || operationIds.isEmpty()) {
            return List.of();
        }
        return cacheResult(
                operationLogAssociationDao.getByOperationIds(operationIds)
        );
    }

    public List<OperationLogAssociation> getByResourceId(long resourceId,
                                                         SystemResourceKind resourceKind) {
        List<OperationLogAssociation> operationLogAssociations =
                operationLogAssociationDao.getByResourceId(resourceId, resourceKind);
        return cacheResult(operationLogAssociations);
    }

    @Override
    protected Class<OperationLogAssociation> getEntityClass() {
        return OperationLogAssociation.class;
    }
}
