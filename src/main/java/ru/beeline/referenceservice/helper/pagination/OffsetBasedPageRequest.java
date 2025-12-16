/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.helper.pagination;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Optional;

@Data
public final class OffsetBasedPageRequest implements Pageable, Serializable {

    private static final long serialVersionUID = -25822477129613575L;

    private final int limit;
    private final int offset;
    private final Sort sort;

    public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero!");
        }
        this.offset = offset;
        this.limit = limit;
        this.sort = (sort == null) ? Sort.unsorted() : sort;
    }

    public static OffsetBasedPageRequest of(int offset, int limit, Sort.Direction direction, String... properties) {
        return new OffsetBasedPageRequest(offset, limit, Sort.by(direction, properties));
    }

    public static OffsetBasedPageRequest of(int offset, int limit, Sort sort) {
        return new OffsetBasedPageRequest(offset, limit, sort);
    }

    public static OffsetBasedPageRequest of(int offset, int limit) {
        return new OffsetBasedPageRequest(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(offset + limit, limit, sort);
    }

    public OffsetBasedPageRequest previous() {
        int newOffset = Math.max(offset - limit, 0);
        return new OffsetBasedPageRequest(newOffset, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, limit, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        return new OffsetBasedPageRequest(pageNumber * limit, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset >= limit;
    }

    @Override
    public boolean isPaged() {
        return Pageable.super.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return Pageable.super.isUnpaged();
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return Pageable.super.getSortOr(sort);
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Pageable.super.toOptional();
    }
}