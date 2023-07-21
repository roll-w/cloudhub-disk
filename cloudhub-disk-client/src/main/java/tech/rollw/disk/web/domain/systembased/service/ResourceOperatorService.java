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

package tech.rollw.disk.web.domain.systembased.service;

import org.springframework.stereotype.Service;
import tech.rollw.disk.web.domain.systembased.*;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ResourceOperatorService implements SystemResourceOperatorProvider {
    private final List<SystemResourceOperatorFactory> systemResourceOperatorFactories;

    public ResourceOperatorService(List<SystemResourceOperatorFactory> systemResourceOperatorFactories) {
        this.systemResourceOperatorFactories = systemResourceOperatorFactories;
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource) {
        return getSystemResourceOperator(systemResource, true);
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource, boolean checkDelete) {
        SystemResourceOperatorFactory systemResourceOperatorFactory =
                findFirstOf(systemResource.getSystemResourceKind());
        SystemResourceOperator systemResourceOperator =
                systemResourceOperatorFactory.createResourceOperator(systemResource, checkDelete);
        try {
            return (T) systemResourceOperator;
        } catch (ClassCastException e) {
            throw noFactoryConfiguredForKindAndType(
                    systemResource.getSystemResourceKind(),
                    systemResourceOperator.getClass(),
                    e.getMessage()
            );
        }
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind,
            boolean checkDelete) {
        SystemResourceOperatorFactory systemResourceOperatorFactory = findFirstOf(
                targetSystemResourceKind
        );
        SystemResourceOperator systemResourceOperator = systemResourceOperatorFactory.createResourceOperator(
                systemResource,
                targetSystemResourceKind,
                checkDelete
        );
        try {
            return (T) systemResourceOperator;
        } catch (ClassCastException e) {
            throw noFactoryConfiguredForKindAndType(
                    systemResource.getSystemResourceKind(),
                    systemResourceOperator.getClass(),
                    e.getMessage()
            );
        }
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind) {
        return getSystemResourceOperator(
                systemResource,
                targetSystemResourceKind,
                true
        );
    }

    private SystemResourceOperatorFactory findFirstOf(SystemResourceKind kind) {
        return systemResourceOperatorFactories.stream()
                .filter(factory -> factory.supports(kind))
                .findFirst()
                .orElseThrow((() -> noFactoryConfiguredForKind(kind)));
    }

    private IllegalArgumentException noFactoryConfiguredForKindAndType(
            SystemResourceKind kind, Class<? extends SystemResourceOperator> clazz,
            String message) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + " and type:" + clazz + ". " + message);
    }

    private IllegalArgumentException noFactoryConfiguredForKind(
            SystemResourceKind kind) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + ".");
    }

    private IllegalArgumentException noFactoryConfiguredForBothKind(
            SystemResourceKind kind, SystemResourceKind targetSystemResourceKind) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + " and target kind:" + targetSystemResourceKind + ".");
    }
}
