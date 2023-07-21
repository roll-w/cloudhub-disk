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

package tech.rollw.disk.web.domain.operatelog.context;

import tech.rollw.disk.web.domain.operatelog.OperateType;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.operatelog.dto.Operation;
import tech.rollw.disk.web.domain.systembased.SystemResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class OperationContext {
    private final Operation.Builder operationBuilder;

    public OperationContext() {
        operationBuilder = Operation.builder();
    }

    public OperationContext(Operation.Builder operationBuilder) {
        this.operationBuilder = operationBuilder;
    }

    public OperationContext setOperator(Operator operator) {
        operationBuilder.setOperator(operator);
        return this;
    }

    public OperationContext addSystemResource(SystemResource systemResource) {
        SystemResource exists = operationBuilder.getSystemResource();
        if (exists == null) {
            operationBuilder.setSystemResource(systemResource);
            return this;
        }
        List<SystemResource> associatedResources = operationBuilder.getAssociatedResources();
        if (associatedResources == null) {
            associatedResources = new ArrayList<>();

        }
        associatedResources.add(systemResource);
        operationBuilder.setAssociatedResources(associatedResources);
        return this;
    }

    public OperationContext addSystemResources(SystemResource... systemResources) {
        for (SystemResource systemResource : systemResources) {
            addSystemResource(systemResource);
        }
        return this;
    }

    public OperationContext addSystemResources(List<? extends SystemResource> systemResources) {
        for (SystemResource systemResource : systemResources) {
            addSystemResource(systemResource);
        }
        return this;
    }

    public OperationContext addSystemResourceOverrides(SystemResource systemResource) {
        operationBuilder.setSystemResource(systemResource);
        return this;
    }

    public OperationContext setOperateType(OperateType operateType) {
        operationBuilder.setOperateType(operateType);
        return this;
    }

    public OperationContext setAddress(String address) {
        operationBuilder.setAddress(address);
        return this;
    }

    public OperationContext setTimestamp(long timestamp) {
        operationBuilder.setTimestamp(timestamp);
        return this;
    }

    public OperationContext setOriginContent(String originContent) {
        operationBuilder.setOriginContent(originContent);
        return this;
    }

    public OperationContext setChangedContent(String changedContent) {
        operationBuilder.setChangedContent(changedContent);
        return this;
    }

    protected OperationContext setAssociatedResources(SystemResource... associatedResources) {
        operationBuilder.setAssociatedResources(List.of(associatedResources));
        return this;
    }

    protected OperationContext setAssociatedResources(List<SystemResource> associatedResources) {
        operationBuilder.setAssociatedResources(associatedResources);
        return this;
    }

    public Operator getOperator() {
        return operationBuilder.getOperator();
    }

    public Operation build() {
        return operationBuilder.build();
    }

}
