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

import tech.rollw.disk.web.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public interface OperateType extends SystemResourceKind.Kind {
    long getTypeId();

    Action getAction();

    @Override
    SystemResourceKind getSystemResourceKind();

    String getName();

    /**
     * 操作描述模板。
     * <p>
     * 可用参数：
     * <ul>
     *     <li>{0}：操作前的内容（可能为空）</li>
     *     <li>{1}：操作后的内容</li>
     * </ul>
     */
    String getDescriptionTemplate();

    String getDescription(Object... args);
}
