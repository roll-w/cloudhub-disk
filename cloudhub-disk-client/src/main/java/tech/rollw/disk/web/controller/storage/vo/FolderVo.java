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

package tech.rollw.disk.web.controller.storage.vo;

import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.userstorage.UserFolder;
import tech.rollw.disk.web.domain.userstorage.dto.FolderInfo;
import tech.rollw.disk.web.domain.userstorage.dto.FolderStructureInfo;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public record FolderVo(
        long storageId,
        String name,
        Long parentId,
        List<FolderInfo> parents,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long createTime,
        long updateTime,
        boolean deleted
) {

    public static FolderVo of(UserFolder folder, List<FolderInfo> parents) {
        return new FolderVo(
                folder.getStorageId(),
                folder.getName(),
                folder.getParentId(),
                parents,
                folder.getOwnerId(),
                folder.getOwnerType(),
                folder.getCreateTime(),
                folder.getUpdateTime(),
                folder.isDeleted()
        );
    }

    public static FolderVo of(FolderStructureInfo structureInfo) {
        return new FolderVo(
                structureInfo.getStorageId(),
                structureInfo.getName(),
                structureInfo.getParentId(),
                structureInfo.getParents(),
                structureInfo.getOwnerId(),
                structureInfo.getOwnerType(),
                structureInfo.getCreateTime(),
                structureInfo.getUpdateTime(),
                structureInfo.isDeleted()
        );
    }

}
