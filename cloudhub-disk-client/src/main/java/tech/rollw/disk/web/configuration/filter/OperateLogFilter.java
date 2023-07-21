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

package tech.rollw.disk.web.configuration.filter;

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.domain.operatelog.OperateLogger;
import tech.rollw.disk.web.domain.operatelog.context.OperationContext;
import tech.rollw.disk.web.domain.operatelog.context.OperationContextHolder;
import tech.rollw.disk.web.domain.operatelog.dto.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author RollW
 */
@Component
public class OperateLogFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(OperateLogFilter.class);
    private final OperateLogger operateLogger;

    public OperateLogFilter(OperateLogger operateLogger) {
        this.operateLogger = operateLogger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ApiContextHolder.ApiContext apiContext = ApiContextHolder.getContext();
        OperationContext context = new OperationContext();
        OperationContextHolder.setContext(context);
        context.setOperator(apiContext.userInfo())
                .setAddress(apiContext.ip())
                .setTimestamp(apiContext.timestamp());
        try {
            filterChain.doFilter(request, response);
        } finally {
            logContext(context, request);
            OperationContextHolder.remove();
        }
    }

    private void logContext(OperationContext context, HttpServletRequest request) {
        Operation operation = context.build();
        if (operation.operator() == null || operation.systemResource() == null) {
            return;
        }
        if (operation.operateType() == null) {
            logger.error("Not configured operation type, skip logging. Source: {}.",
                    request.getRequestURI());
            return;
        }
        operateLogger.recordOperate(operation);
    }
}
