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

package tech.rollw.disk.web.domain.cfsserver;

import org.cloudhub.client.CFSClient;
import org.cloudhub.client.CFSStatus;
import org.cloudhub.client.server.ConnectedServers;
import org.cloudhub.server.NetworkUsageInfo;
import org.cloudhub.server.ServerHostInfo;
import org.cloudhub.server.ServerStatusMonitor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
@Service
public class ServerStatusService {
    private final ServerStatusMonitor serverStatusMonitor;
    private final CFSClient cfsClient;
    private final AtomicLong runSecs = new AtomicLong(0);

    public ServerStatusService(CFSClient cfsClient) {
        this.cfsClient = cfsClient;
        serverStatusMonitor = new ServerStatusMonitor(".");
        serverStatusMonitor.setLimit(100);
        serverStatusMonitor.setRecordFrequency(1000);
        serverStatusMonitor.startMonitor();
    }

    public ServerHostInfo getCurrentInfo() {
        return serverStatusMonitor.getLatest();
    }

    public List<NetworkUsageInfo> getNetInfos() {
        return serverStatusMonitor.getRecent(50).stream()
                .map(ServerHostInfo::getNetworkUsageInfo)
                .toList();
    }

    @Scheduled(fixedRate = 10000)
    private void countUp() {
        runSecs.addAndGet(10);
    }

    public long getRunTimeLength() {
        return runSecs.get();
    }

    public ServerStatusSummary getSummary() {
        ConnectedServers connectedServers;
        ServerHostInfo serverHostInfo =
                serverStatusMonitor.getLatest();
        try {
            connectedServers = cfsClient.getConnectedServers();
        } catch (Exception e) {
            return getSummaryOf(
                    runSecs.get(),
                    CFSStatus.UNAVAILABLE,
                    0,
                    0,
                    serverHostInfo
            );
        }
        return getSummaryOf(
                runSecs.get(),
                CFSStatus.SUCCESS,
                connectedServers.activeServers().size(),
                connectedServers.deadServers().size(),
                serverHostInfo
        );
    }

    private static ServerStatusSummary getSummaryOf(long runtime,
                                                    CFSStatus cfsStatus,
                                                    int activeFileServers,
                                                    int deadFileServers,
                                                    ServerHostInfo serverHostInfo) {
        long diskUsed = serverHostInfo.getDiskUsageInfo().getTotal() -
                serverHostInfo.getDiskUsageInfo().getFree();
        return new ServerStatusSummary(
                runtime,
                cfsStatus,
                activeFileServers,
                deadFileServers,
                new UsageInfo(
                        serverHostInfo.getDiskUsageInfo().getTotal(),
                        diskUsed
                ),
                new UsageInfo(
                        serverHostInfo.getMemoryUsageInfo().getTotal(),
                        serverHostInfo.getMemoryUsageInfo().getUsed()
                )
        );
    }
}
