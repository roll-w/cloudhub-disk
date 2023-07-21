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

/**
 * 实现部分类POSIX语义。
 *
 * @author RollW
 */
public enum Action {
    CREATE,
    UPDATE,
    ACCESS(false),
    EDIT,
    DELETE,
    MOVE,
    COPY,
    RENAME,
    ;

    private final boolean isWrite;

    Action(boolean isWrite) {
        this.isWrite = isWrite;
    }

    Action() {
        this(true);
    }

    public boolean isWrite() {
        return isWrite;
    }

    public boolean isRead() {
        return !isWrite();
    }
}
