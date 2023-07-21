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

package tech.rollw.disk.web.domain.systembased.paged;

import tech.rollw.disk.web.domain.systembased.SystemContext;
import tech.rollw.disk.common.data.page.Page;
import tech.rollw.disk.common.data.page.Pageable;
import space.lingu.light.Order;

import java.util.List;

/**
 * @author RollW
 */
public class PageableContext implements SystemContext, Pageable {
    private int page;
    private int size;
    private boolean includeDeleted = false;

    private long total = 0;

    private Order order = Order.DESC;
    private String orderBy = "id";

    public PageableContext(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageableContext() {
        this(0, 0);
    }

    public long getTotal() {
        return total;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void addTotal(int total) {
        this.total += total;
    }

    public void addTotal(long total) {
        this.total += total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public <T> Page<T> toPage(List<T> data) {
        return Page.of(this, total, data);
    }

    public static PageableContext of(int page, int size) {
        return new PageableContext(page, size);
    }

    public static PageableContext of(Pageable pageable) {
        return new PageableContext(pageable.getPage(), pageable.getSize());
    }

    @Override
    public Object getObject(String key) {
        return switch (key) {
            case "page" -> page;
            case "size" -> size;
            case "total" -> total;
            default -> null;
        };
    }
}
