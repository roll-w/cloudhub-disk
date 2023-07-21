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

package tech.rollw.disk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import tech.rollw.disk.common.conf.ClientConfigLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@SpringBootApplication
@EnableScheduling
public class DiskClientApplication {
    private static ConfigurableApplicationContext sContext;

    public static void main(String[] args) throws IOException {
        SpringApplication application =
                new SpringApplication(DiskClientApplication.class);
        ClientConfigLoader loader =
                ClientConfigLoader.tryOpenDefault(DiskClientApplication.class);
        int port = loader.getWebPort(7015);

        String logLevel = loader.getLogLevel();

        Map<String, Object> overrideProperties = new HashMap<>();
        overrideProperties.put("server.port", port);
        overrideProperties.put("spring.mvc.throw-exception-if-no-handler-found", true);

        overrideProperties.put("logging.level.org.huel.cloudhub", logLevel);
        overrideProperties.put("logging.level.org.cloudhub", logLevel);

        logToFile(args, overrideProperties, loader);

        application.setDefaultProperties(overrideProperties);
        sContext = application.run(args);
    }

    private static final String LOG_FILE = "cloudhub-disk-client.out";
    private static final String ARCHIVE_LOG_FILE = "cloudhub-disk-client-log.%d{yyyy-MM-dd}.%i.log";

    private static void logToFile(String[] args,
                                  Map<String, Object> overrideProperties,
                                  ClientConfigLoader clientConfigLoader) {
        if (!startAsDaemon(args)) {
            System.out.println("Not start as daemon, log to console.");
            return;
        }
        String logPath = clientConfigLoader.getLogPath();
        if (ClientConfigLoader.LOG_PATH_DEFAULT.equals(logPath)) {
            return;
        }

        overrideProperties.put("logging.file.name", logPath + "/" + LOG_FILE);
        overrideProperties.put("logging.logback.rollingpolicy.file-name-pattern",
                logPath + "/" + ARCHIVE_LOG_FILE);
    }

    private static boolean startAsDaemon(String[] args) {
        for (String arg : args) {
            if (arg.equals("--daemon")) {
                return true;
            }
        }
        return false;
    }
}
