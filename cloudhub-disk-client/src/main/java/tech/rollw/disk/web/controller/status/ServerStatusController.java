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

package tech.rollw.disk.web.controller.status;

import org.cloudhub.client.CFSClient;
import tech.rollw.disk.web.common.CloudhubBizRuntimeException;
import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.domain.cfsserver.ServerStatusService;
import tech.rollw.disk.web.domain.cfsserver.ServerStatusSummary;
import org.cloudhub.client.server.ConnectedServers;
import org.cloudhub.client.server.ContainerStatus;
import org.cloudhub.server.DiskUsageInfo;
import org.cloudhub.server.NetworkUsageInfo;
import org.cloudhub.server.ServerHostInfo;
import tech.rollw.disk.common.CommonErrorCode;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.WebCommonErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class ServerStatusController {
    private final CFSClient cfsClient;
    private final ServerStatusService serverStatusService;

    public ServerStatusController(CFSClient cfsClient,
                                  ServerStatusService serverStatusService) {
        this.cfsClient = cfsClient;
        this.serverStatusService = serverStatusService;
    }

    @GetMapping("/server/status/time")
    public HttpResponseEntity<Long> getRunTimeFromStart() {
        return HttpResponseEntity.success(serverStatusService.getRunTimeLength());
    }

    @GetMapping("/server/status/summary")
    public HttpResponseEntity<ServerStatusSummary> getServerStatusSummary() {
        return HttpResponseEntity.success(serverStatusService.getSummary());
    }

    @GetMapping("/server/cfs/connected")
    public HttpResponseEntity<ConnectedServers> getConnectedServers() {
        ConnectedServers connectedServers =
                cfsClient.getConnectedServers();
        return HttpResponseEntity.success(connectedServers);
    }

    @GetMapping("/server/cfs/status/{serverId}/containers")
    public HttpResponseEntity<List<ContainerStatus>> getFileServerContainerStatuses(
            @PathVariable("serverId") String serverId) {
        if (serverId == null || serverId.isEmpty()) {
            throw new CloudhubBizRuntimeException(WebCommonErrorCode.ERROR_PARAM_MISSING,
                    "Missing server id.");
        }
        if (serverId.equalsIgnoreCase("meta")) {
            throw new CloudhubBizRuntimeException(WebCommonErrorCode.ERROR_NOT_SUPPORT,
                    "Not support meta server.");
        }

        return HttpResponseEntity.success(
                cfsClient.getContainerStatuses(serverId));
    }

    public static final String META_SERVER_ID = "meta";

    @GetMapping("/server/cfs/status/{serverId}")
    public HttpResponseEntity<ServerHostInfo> getServerStatus(
            @PathVariable("serverId") String serverId) {
        if (serverId == null || serverId.isEmpty()) {
            return HttpResponseEntity.success(
                    serverStatusService.getCurrentInfo());
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerInfo());
        }
        return HttpResponseEntity.success(
                cfsClient.getFileServerInfo(serverId));
    }

    @GetMapping("/server/cfs/status")
    public HttpResponseEntity<ServerHostInfo> getServerStatus() {
        return getServerStatus(null);
    }

    @GetMapping("/server/cfs/status/{serverId}/net")
    public HttpResponseEntity<List<NetworkUsageInfo>> getServerNetInfos(
            @PathVariable("serverId") String serverId) {
        if (serverId == null || serverId.isEmpty()) {
            return HttpResponseEntity.success(
                    serverStatusService.getNetInfos());
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerNetRecords());
        }
        List<NetworkUsageInfo> nets =
                cfsClient.getFileNetRecords(serverId);
        if (nets == null) {
            throw new CloudhubBizRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        return HttpResponseEntity.success(
                cfsClient.getFileNetRecords(serverId));
    }

    @GetMapping("/server/cfs/status/net")
    public HttpResponseEntity<List<NetworkUsageInfo>> getServerNetInfos() {
        return getServerNetInfos(null);
    }

    @GetMapping("/server/cfs/status/{serverId}/disk")
    public HttpResponseEntity<List<DiskUsageInfo>> getServerDiskInfos(
            @PathVariable("serverId") String serverId) {

        if (serverId == null || serverId.isEmpty()) {
            throw new CloudhubBizRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerDiskRecords());
        }
        List<DiskUsageInfo> disks =
                cfsClient.getFileServerDiskRecords(serverId);
        if (disks == null) {
            throw new CloudhubBizRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        return HttpResponseEntity.success(disks);
    }

    @GetMapping("/server/cfs/status/disk")
    public HttpResponseEntity<List<DiskUsageInfo>> getServerDiskInfos() {
        return getServerDiskInfos(null);
    }
}
