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

package tech.rollw.disk.web.domain.usergroup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public final class GroupSettingKeys {
    // by mb
    public static final String GROUP_QUOTA = "group_quota";

    public static final String GROUP_FILE_NUM_LIMIT = "group_file-number-limit";


    static final String NO_LIMIT = "-1";

    private static final Map<String, String> DEFAULT_GROUP_SETTINGS = new HashMap<>();
    static {
        DEFAULT_GROUP_SETTINGS.put(GROUP_QUOTA, "10240");
        DEFAULT_GROUP_SETTINGS.put(GROUP_FILE_NUM_LIMIT, NO_LIMIT);
    }

    public static final UserGroup DEFAULT = new UserGroup(
            0L, "default", "default group",
            DEFAULT_GROUP_SETTINGS,
            0, 0, false
    );

    private GroupSettingKeys() {
    }
}
