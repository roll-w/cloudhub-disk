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

import com.google.common.base.Strings;
import tech.rollw.disk.web.common.ParameterFailedException;
import tech.rollw.disk.web.domain.systembased.ContextThread;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.systembased.service.PageableContextFactory;
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
public class ContextInitializeFilter extends OncePerRequestFilter {
    private final PageableContextFactory pageableContextFactory;

    public ContextInitializeFilter(PageableContextFactory pageableContextFactory) {
        this.pageableContextFactory = pageableContextFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        PageableContext context = fromRequest(request);
        ContextThread<PageableContext> contextThread =
                pageableContextFactory.assambleContextThread(context);
        try {
            filterChain.doFilter(request, response);
        } finally {
            pageableContextFactory.clearContextThread();
        }
    }

    private PageableContext fromRequest(HttpServletRequest request) {
        String page = request.getParameter("page");
        String size = request.getParameter("size");

        try {
            int pageInt = Strings.isNullOrEmpty(page) ? 1 : Integer.parseInt(page);
            int sizeInt = Strings.isNullOrEmpty(size) ? 10 : Integer.parseInt(size);
            return new PageableContext(pageInt, sizeInt);
        } catch (NumberFormatException e) {
            throw new ParameterFailedException(e.getMessage());
        }
    }
}
