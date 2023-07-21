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

package tech.rollw.disk.web.domain.systembased;

/**
 * @author RollW
 */
public enum SystemResourceKind {
    FILE,
    FOLDER,
    LINK,
    STORAGE_PERMISSION,
    VERSIONED_FILE,
    VERSIONED_FOLDER,
    STORAGE_SHARE,
    TAG,
    TAG_GROUP,
    USER,
    USER_GROUP("group"),
    ORGANIZATION,
    STORAGE_USER_PERMISSION,
    FAVORITE_GROUP,
    FAVORITE_ITEM,
    ;

    private final String alias;

    SystemResourceKind() {
        alias = null;
    }

    SystemResourceKind(String alias) {
        this.alias = alias;
    }


    public interface Kind {
        SystemResourceKind getSystemResourceKind();
    }

    public static SystemResourceKind from(String nameIgnoreCase) {
        if (nameIgnoreCase == null || nameIgnoreCase.isBlank()) {
            return null;
        }
        for (SystemResourceKind value : values()) {
            if (value.name().equalsIgnoreCase(nameIgnoreCase)) {
                return value;
            }
            if (value.alias == null) {
                continue;
            }
            if (value.alias.equalsIgnoreCase(nameIgnoreCase)) {
                return value;
            }
        }
        return null;
    }
}
