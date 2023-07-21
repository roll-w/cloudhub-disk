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

package tech.rollw.disk.web.controller.share;

import tech.rollw.disk.web.common.ParameterFailedException;
import tech.rollw.disk.web.domain.share.ShareService;

import java.time.Duration;

/**
 * @author RollW
 */
public record ShareCreateRequest(
        int time,
        String password,
        int type
) {
    public static final int TIME_1_DAY = -1;
    public static final int TIME_7_DAY = -7;
    public static final int TIME_30_DAY = -30;
    public static final int TIME_INFINITE = 0;

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;

    public Duration toDuration() {
        return switch (time) {
            case TIME_1_DAY -> ShareService.DAYS_1;
            case TIME_7_DAY -> ShareService.DAYS_7;
            case TIME_30_DAY -> ShareService.DAYS_30;
            case TIME_INFINITE -> ShareService.INFINITE;
            // TODO: may supports custom time future
            default -> throw new ParameterFailedException("Not supports time param.");
        };
    }

}
