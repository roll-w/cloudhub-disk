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

package tech.rollw.disk.web.domain.userstorage.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.authentication.token.AuthenticationTokenService;
import tech.rollw.disk.web.domain.userstorage.StorageDownloadTokenProvider;
import tech.rollw.disk.web.domain.userstorage.StorageIdentity;
import tech.rollw.disk.web.domain.userstorage.StorageType;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import tech.rollw.disk.common.AuthErrorCode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * @author RollW
 */
@Service
public class DownloadTokenServiceImpl implements StorageDownloadTokenProvider {
    private final Cache<String, DownloadToken> downloadTokenCache;

    public DownloadTokenServiceImpl() {
        downloadTokenCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30))
                .expireAfterAccess(Duration.ofMinutes(30))
                .build();
    }

    @Override
    public String getDownloadToken(StorageIdentity storageIdentity,
                                   Duration duration) {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        long now = System.currentTimeMillis();
        DownloadToken downloadToken = new DownloadToken(
                token,
                storageIdentity.getStorageId(),
                storageIdentity.getStorageType(),
                now + duration.toMillis()
        );
        downloadTokenCache.put(token, downloadToken);
        return downloadToken.token();
    }

    @Override
    public String getDownloadToken(StorageIdentity storageIdentity) {
        return getDownloadToken(storageIdentity, AuthenticationTokenService.MIN_5);
    }

    @Override
    public StorageIdentity verifyDownloadToken(String token) throws AuthenticationException {
        DownloadToken downloadToken = downloadTokenCache.getIfPresent(token);
        if (downloadToken == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_TOKEN_NOT_EXIST);
        }
        if (downloadToken.expireTime() < System.currentTimeMillis()) {
            throw new AuthenticationException(AuthErrorCode.ERROR_TOKEN_EXPIRED);
        }
        return new SimpleStorageIdentity(
                downloadToken.storageId(),
                downloadToken.storageType()
        );
    }

    private record DownloadToken(
            String token,
            long storageId,
            StorageType storageType,
            long expireTime) {
    }
}
