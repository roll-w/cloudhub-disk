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

package tech.rollw.disk.web.domain.operatelog;

import tech.rollw.disk.web.domain.operatelog.dto.Operation;
import tech.rollw.disk.web.domain.systembased.SystemResource;

import java.util.List;

/**
 * @author RollW
 */
public interface OperateLogger {
    void recordOperate(Action action, SystemResource systemResource,
                       OperateType operateType,
                       String originContent,
                       String changedContent,
                       List<SystemResource> associateResources);

    void recordOperate(Action action, SystemResource systemResource,
                       OperateType operateType,
                       String originContent, List<SystemResource> associateResources);

    void recordOperate(Action action, SystemResource systemResource,
                       OperateType operateType,
                       List<SystemResource> associateResources);

    void recordOperate(Operation operation);
}
