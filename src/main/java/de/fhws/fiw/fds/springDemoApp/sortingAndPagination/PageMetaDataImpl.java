package de.fhws.fiw.fds.springDemoApp.sortingAndPagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.PagedModel;

public class PageMetaDataImpl extends PagedModel.PageMetadata {

    private final int numberOfElements;

    public PageMetaDataImpl(long size, long number, long totalElements, int numberOfElements) {
        super(size, number, totalElements);
        this.numberOfElements = numberOfElements;
    }

    public PageMetaDataImpl(long size, long number, long totalElements, long totalPages, int numberOfElements) {
        super(size, number, totalElements, totalPages);
        this.numberOfElements = numberOfElements;
    }

    @JsonProperty("numberOfElements")
    public int getNumberOfElements() {
        return numberOfElements;
    }
}
