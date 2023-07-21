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

package tech.rollw.disk.web.controller.share.vo;

import tech.rollw.disk.web.domain.share.dto.ShareStructureInfo;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.userstorage.dto.FolderInfo;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;

import java.util.List;

/**
 * @author RollW
 */
public record ShareStructureVo(
        long id,
        long creatorId,
        String username,
        String nickname,
        String shareCode,
        boolean isPublic,
        long expireTime,
        long createTime,
        List<FolderInfo> parents,
        FolderInfo current,
        List<StorageVo> storages
) {

    public static ShareStructureVo from(ShareStructureInfo shareStructureInfo,
                                        AttributedUser attributedUser) {
        return new ShareStructureVo(
                shareStructureInfo.id(),
                shareStructureInfo.creatorId(),
                attributedUser.getUsername(),
                attributedUser.getNickname(),
                shareStructureInfo.shareCode(),
                shareStructureInfo.isPublic(),
                shareStructureInfo.expireTime(),
                shareStructureInfo.createTime(),
                shareStructureInfo.parents(),
                shareStructureInfo.current(),
                shareStructureInfo.storages()
                        .stream()
                        .map(StorageVo::from)
                        .toList()
        );
    }
}
