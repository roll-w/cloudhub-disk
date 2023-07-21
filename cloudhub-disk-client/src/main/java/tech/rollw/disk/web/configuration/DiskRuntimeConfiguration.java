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

package tech.rollw.disk.web.configuration;

import tech.rollw.disk.common.conf.ClientConfigLoader;
import tech.rollw.disk.web.DiskClientApplication;
import org.cloudhub.client.CFSClient;
import org.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.lingu.light.DatasourceConfig;

import java.io.IOException;

/**
 * @author RollW
 */
@Configuration
public class DiskRuntimeConfiguration {
    private final ClientConfigLoader clientConfigLoader;

    public DiskRuntimeConfiguration() throws IOException {
        this.clientConfigLoader = ClientConfigLoader.tryOpenDefault(DiskClientApplication.class);
    }

    @Bean
    public GrpcProperties grpcProperties() {
        return new GrpcProperties(
                clientConfigLoader.getRpcPort(),
                clientConfigLoader.getRpcMaxInboundSize());
    }

    @Bean
    public DatasourceConfig datasourceConfig() {
        return new DatasourceConfig(
                clientConfigLoader.getDatabaseUrl(),
                "com.mysql.cj.jdbc.Driver",
                clientConfigLoader.getDatabaseUsername(),
                clientConfigLoader.getDatabasePassword());
    }


    @Bean
    public ClientConfigLoader getClientConfigLoader() {
        return clientConfigLoader;
    }

    @Bean
    public CFSClient cfsClient() {
        return new CFSClient(
                clientConfigLoader.getMetaServerAddress(),
                new GrpcProperties(
                        0,
                        clientConfigLoader.getRpcMaxInboundSize()
                )
        );
    }

}
