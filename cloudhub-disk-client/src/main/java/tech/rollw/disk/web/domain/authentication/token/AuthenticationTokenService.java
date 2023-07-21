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

package tech.rollw.disk.web.domain.authentication.token;

import java.time.Duration;

/**
 * 认证令牌服务
 * <p>
 * 只负责生成Token和验证Token的合法性。不负责确认用户状态。
 *
 * @author RollW
 */
public interface AuthenticationTokenService {
    Duration DAY_7 = Duration.ofDays(7);
    Duration DAY_1 = Duration.ofDays(1);
    Duration MIN_30 = Duration.ofMinutes(30);
    Duration MIN_5 = Duration.ofMinutes(5);

    default String generateAuthToken(long userId, String signature) {
        return generateAuthToken(userId, signature, DAY_7);
    }

    String generateAuthToken(long userId, String signature,
                             Duration duration);

    /**
     * 只验证Token的合法性，不负责确认用户状态。
     */
    TokenAuthResult verifyToken(String token,
                                String signature);

    Long getUserId(String token);

}
