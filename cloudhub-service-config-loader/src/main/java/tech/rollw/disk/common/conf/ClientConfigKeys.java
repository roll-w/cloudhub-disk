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

package tech.rollw.disk.common.conf;

/**
 * Client configuration keys.
 *
 * @author RollW
 */
public class ClientConfigKeys {
    /**
     * Cloudhub client rpc port.
     * receive remote call through the port.
     */
    public static final String RPC_PORT = "cloudhub.rpc.port";

    /**
     * Cloudhub client rpc max inbound size in mb.
     */
    public static final String RPC_MAX_INBOUND_SIZE = "cloudhub.rpc.max_inbound_size";

    /**
     * Cloudhub web port.
     * Access client in web through the port.
     */
    public static final String WEB_PORT = "cloudhub.web.port";

    /**
     * Meta-server address.
     * Format: host:port.
     */
    public static final String META_ADDRESS = "cloudhub.meta.address";

    /**
     * Cloudhub client temporary file directory.
     */
    public static final String FILE_TEMP_PATH = "cloudhub.file.temp_dir";

    /**
     * Cloudhub client log level. Support: trace, debug, info, warn, error.
     */
    public static final String LOG_LEVEL = "cloudhub.client.log.level";

    /**
     * Log path. If path is "console", log will be printed to console.
     * Or will be saved to the path as file. File name format is
     * "cloudhub-disk-client.out" & "cloudhub-disk-client-{date}.{order}.log".
     * <p>
     * When not start as daemon, this config will be ignored and
     * log will be printed to console.
     */
    public static final String LOG_PATH = "cloudhub.client.log.path";

    public static final String DATABASE_URL = "cloudhub.client.db.url";

    public static final String DATABASE_USERNAME = "cloudhub.client.db.username";

    public static final String DATABASE_PASSWORD = "cloudhub.client.db.password";


    private ClientConfigKeys() {
    }
}
