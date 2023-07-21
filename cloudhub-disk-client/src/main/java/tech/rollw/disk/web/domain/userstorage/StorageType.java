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

package tech.rollw.disk.web.domain.userstorage;

import tech.rollw.disk.web.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public enum StorageType implements SystemResourceKind.Kind {
    FILE(SystemResourceKind.FILE),
    FOLDER(SystemResourceKind.FOLDER),
    LINK(SystemResourceKind.LINK);

    private final SystemResourceKind systemResourceKind;

    StorageType(SystemResourceKind systemResourceKind) {
        this.systemResourceKind = systemResourceKind;
    }

    public boolean isFile() {
        return this == FILE;
    }

    public static StorageType from(String nameIgnoreCase) {
        for (StorageType value : values()) {
            if (value.name().equalsIgnoreCase(nameIgnoreCase)) {
                return value;
            }
        }
        return null;
    }

    public static StorageType from(SystemResourceKind systemResourceKind) {
        for (StorageType value : values()) {
            if (value.getSystemResourceKind() == systemResourceKind) {
                return value;
            }
        }
        return null;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return systemResourceKind;
    }
}
