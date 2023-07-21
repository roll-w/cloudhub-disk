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

import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.UnsupportedKindException;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.systembased.validate.ValidatorProvider;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class SystemValidatorProviderService implements ValidatorProvider {
    private final List<Validator> validators;

    public SystemValidatorProviderService(List<Validator> validators) {
        this.validators = validators;
    }

    @Override
    public @NonNull Validator getValidator(@NonNull SystemResourceKind systemResourceKind)
            throws UnsupportedKindException {
        return getValidatorByKind(systemResourceKind);
    }

    private Validator getValidatorByKind(SystemResourceKind systemResourceKind) {
        return validators.stream()
                .filter(validator -> validator.supports(systemResourceKind))
                .findFirst()
                .orElseThrow(() -> new UnsupportedKindException(systemResourceKind));
    }
}
