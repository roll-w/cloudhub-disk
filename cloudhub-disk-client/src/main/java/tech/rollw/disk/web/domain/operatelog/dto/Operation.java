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

package tech.rollw.disk.web.domain.operatelog.dto;

import tech.rollw.disk.web.domain.operatelog.OperateType;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.operatelog.SimpleOperator;
import tech.rollw.disk.web.domain.systembased.SystemResource;

import java.util.List;

/**
 * @author RollW
 */
public record Operation(
        Operator operator,
        SystemResource systemResource,
        OperateType operateType,
        String address,
        long timestamp,
        String originContent,
        String changedContent,
        List<SystemResource> associatedResources
) {

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Operator operator;
        private SystemResource systemResource;
        private OperateType operateType;
        private String address;
        private long timestamp;
        private String originContent;
        private String changedContent;
        private List<SystemResource> associatedResources;

        public Builder() {
        }

        public Builder(Operation operation) {
            this.operator = operation.operator;
            this.systemResource = operation.systemResource;
            this.operateType = operation.operateType;
            this.address = operation.address;
            this.timestamp = operation.timestamp;
            this.originContent = operation.originContent;
            this.changedContent = operation.changedContent;
            this.associatedResources = operation.associatedResources;
        }

        public Builder setOperator(Operator operator) {
            this.operator = operator;
            return this;
        }

        public Builder setOperator(long operatorId) {
            this.operator = new SimpleOperator(operatorId);
            return this;
        }

        public Builder setSystemResource(SystemResource systemResource) {
            this.systemResource = systemResource;
            return this;
        }

        public Builder setOperateType(OperateType operateType) {
            this.operateType = operateType;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setOriginContent(String originContent) {
            this.originContent = originContent;
            return this;
        }

        public Builder setChangedContent(String changedContent) {
            this.changedContent = changedContent;
            return this;
        }

        public Builder setAssociatedResources(List<SystemResource> associatedResources) {
            this.associatedResources = associatedResources;
            return this;
        }

        public Operator getOperator() {
            return operator;
        }

        public SystemResource getSystemResource() {
            return systemResource;
        }

        public OperateType getOperateType() {
            return operateType;
        }

        public String getAddress() {
            return address;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getOriginContent() {
            return originContent;
        }

        public String getChangedContent() {
            return changedContent;
        }

        public List<SystemResource> getAssociatedResources() {
            return associatedResources;
        }

        public Operation build() {
            return new Operation(
                    operator, systemResource, operateType,
                    address, timestamp, originContent,
                    changedContent, associatedResources);
        }
    }
}
