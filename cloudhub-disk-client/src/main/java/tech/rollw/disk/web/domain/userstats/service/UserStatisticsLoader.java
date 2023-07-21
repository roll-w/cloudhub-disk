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

package tech.rollw.disk.web.domain.userstats.service;

import tech.rollw.disk.web.domain.userstats.UserStatisticsService;
import tech.rollw.disk.web.domain.userstats.repository.UserStatisticsRepository;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserStatisticsLoader {
    private final UserStatisticsService userStatisticsService;
    private final UserStatisticsRepository userStatisticsRepository;

    public UserStatisticsLoader(UserStatisticsService userStatisticsService,
                                UserStatisticsRepository userStatisticsRepository) {
        this.userStatisticsService = userStatisticsService;
        this.userStatisticsRepository = userStatisticsRepository;
    }

    private void triggerOnInitialized() {
        // needs to confirm all statistics has been calculated on
        // every user, if not, it needs to be a rescan and calculate
        // all statistics.

    }
}
