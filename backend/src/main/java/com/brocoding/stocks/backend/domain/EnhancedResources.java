package com.brocoding.stocks.backend.domain;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

/**
 * Created by Dave van Hooijdonk on 15-3-2018.
 */
public class EnhancedResources<T> extends Resources<T> {

    private final Integer totalPages;
    private final Integer numberOfElements;
    private final Integer totalElements;

    public EnhancedResources(final Iterable<T> iterable, final Iterable<Link> links, final Integer totalPages, final Integer numberOfElements, final Integer totalElements) {
        super(iterable, links);
        this.totalPages = totalPages;
        this.numberOfElements = numberOfElements;
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public Integer getTotalElements() {
        return totalElements;
    }
}
