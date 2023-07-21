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

package tech.rollw.disk.web.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author RollW
 */
public record TimeRange(
        Long start,
        Long end
) {
    public static final TimeRange NULL = new TimeRange(null, null);

    public LocalDateTime startDateTime() {
        if (start == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(start),
                TimeZone.getDefault().toZoneId());
    }

    public LocalDateTime endDateTime() {
        if (end == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(end),
                TimeZone.getDefault().toZoneId());
    }

}
