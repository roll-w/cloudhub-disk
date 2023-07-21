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

package tech.rollw.disk.web.database;

import tech.rollw.disk.web.database.dao.*;
import tech.rollw.disk.web.domain.favorites.FavoriteGroup;
import tech.rollw.disk.web.domain.favorites.FavoriteItem;
import tech.rollw.disk.web.domain.operatelog.OperationLog;
import tech.rollw.disk.web.domain.operatelog.OperationLogAssociation;
import tech.rollw.disk.web.domain.share.UserShare;
import tech.rollw.disk.web.domain.statistics.DatedStatistics;
import tech.rollw.disk.web.domain.statistics.Statistics;
import tech.rollw.disk.web.domain.storage.DiskFileStorage;
import tech.rollw.disk.web.domain.storagepermission.StoragePermission;
import tech.rollw.disk.web.domain.storagepermission.StorageUserPermission;
import tech.rollw.disk.web.domain.tag.ContentTag;
import tech.rollw.disk.web.domain.tag.TagGroup;
import tech.rollw.disk.web.domain.user.User;
import tech.rollw.disk.web.domain.usergroup.UserGroup;
import tech.rollw.disk.web.domain.usergroup.UserGroupMember;
import tech.rollw.disk.web.domain.userstats.UserStatistics;
import tech.rollw.disk.web.domain.userstorage.StorageMetadata;
import tech.rollw.disk.web.domain.userstorage.UserFileStorage;
import tech.rollw.disk.web.domain.userstorage.UserFolder;
import tech.rollw.disk.web.domain.versioned.VersionedFileStorage;
import space.lingu.light.DataConverters;
import space.lingu.light.Database;
import space.lingu.light.LightConfiguration;
import space.lingu.light.LightDatabase;

/**
 * @author RollW
 */
@Database(name = "cloudhub_disk_database", version = 1, tables = {
        User.class,
        UserGroup.class, UserGroupMember.class, UserStatistics.class,
        DiskFileStorage.class, UserFileStorage.class, UserFolder.class,
        UserShare.class,
        Statistics.class, DatedStatistics.class,
        FavoriteGroup.class, FavoriteItem.class,
        StorageMetadata.class, VersionedFileStorage.class,
        StoragePermission.class, StorageUserPermission.class,
        OperationLog.class, OperationLogAssociation.class,
        ContentTag.class, TagGroup.class,
})
@DataConverters({DiskConverter.class})
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "255")
public abstract class DiskDatabase extends LightDatabase {
    public abstract UserDao getUserDao();

    public abstract UserGroupDao getUserGroupDao();

    public abstract UserGroupMemberDao getUserGroupMemberDao();

    public abstract UserStatisticsDao getUserStatisticsDao();

    public abstract DiskFileStorageDao getDiskFileStorageDao();

    public abstract UserFileStorageDao getUserFileStorageDao();

    public abstract UserFolderDao getUserDirectoryDao();

    public abstract StatisticsDao getStatisticsDao();

    public abstract DatedStatisticsDao getDatedStatisticsDao();

    public abstract UserShareDao getUserShareDao();

    public abstract FavoriteGroupDao getFavoriteGroupDao();

    public abstract FavoriteItemDao getFavoriteItemDao();

    public abstract StorageMetadataDao getStorageMetadataDao();

    public abstract StoragePermissionDao getStoragePermissionDao();

    public abstract StorageUserPermissionDao getStorageUserPermissionDao();

    public abstract VersionedFileStorageDao getVersionedFileStorageDao();

    public abstract OperationLogDao getOperationLogDao();

    public abstract OperationLogAssociationDao getOperationLogAssociationDao();

    public abstract ContentTagDao getContentTagDao();

    public abstract TagGroupDao getTagGroupDao();
}
