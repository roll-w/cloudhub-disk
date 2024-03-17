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

package tech.rollw.disk.web.domain.userstats;

import tech.rollw.disk.web.domain.usergroup.GroupSettingKeys;

import java.util.Map;

/**
 * @author RollW
 */
public final class UserStatisticsKeys {
    public static final String USER_STORAGE_COUNT = "user_storage_count";
    public static final String USER_STORAGE_USED = "user_storage_used";

    public static final long NO_LIMIT = -1;

    static final Map<String, RestrictKey> RESTRICT_KEYS = Map.of(
            USER_STORAGE_COUNT,
            new RestrictKey(USER_STORAGE_COUNT, GroupSettingKeys.GROUP_FILE_NUM_LIMIT),
            USER_STORAGE_USED,
            new RestrictKey(USER_STORAGE_USED, GroupSettingKeys.GROUP_QUOTA,
                    (strictValue) -> {
                        if (strictValue <= 0) return strictValue;
                        return strictValue * 1024 * 1024;
                    }
            )// mb to bytes
    );

    public static RestrictKey restrictKeyOf(String key) {
        return RESTRICT_KEYS.get(key);
    }

    private UserStatisticsKeys() {
    }
}
