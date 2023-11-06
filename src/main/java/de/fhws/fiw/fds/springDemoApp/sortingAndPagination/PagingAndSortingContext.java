package de.fhws.fiw.fds.springDemoApp.sortingAndPagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class PagingAndSortingContext {

    final private Sort sort;

    final private int page;

    final private int size;

    final private Class<?> clazz;

    final private List<String> classFields;

    private String direction;

    private String property;

    public PagingAndSortingContext(final int page, final int size, final String sort, final Class<?> clazz) {
        this.page = Math.max(page, 0);
        this.size = Math.min(Math.max(size, 1), 100);
        if (!Sortable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("clazz must implement " + Sortable.class.getSimpleName());
        }
        this.clazz = clazz;
        this.classFields = setClassFields();
        this.sort = setSort(sort);
    }

    private List<String> setClassFields() {
        return Arrays.stream(this.clazz.getDeclaredFields())
                .map(Field::getName)
                .toList();
    }

    public Sort getSort() {
        return sort;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getDirection() {
        return direction;
    }

    public String getProperty() {
        return property;
    }

    public String getSortAsQueryParam() {
        return this.direction.equalsIgnoreCase("DESC") ? "-" + property : property;
    }

    public String getSortAsQueryParamReversed() {
        return this.direction.equalsIgnoreCase("ASC") ? "-" + property : property;
    }

    public String getSortForDBAccess() {
        return property + " " + direction;
    }
    private Sort setSort(final String sort) {
        if (sort.startsWith("-")) {
            String propertyFromQuery = sort.substring(1);
            var direction = Sort.Direction.DESC;
            String property = getProperty(propertyFromQuery);

            this.property = property;
            this.direction = direction.toString();
            return Sort.by(direction, property);
        }

        String property = getProperty(sort);
        var direction = Sort.Direction.ASC;
        this.direction = direction.toString();
        this.property = property;
        return Sort.by(direction, property);
    }

    private String getProperty(String property) {
        return classFields.stream()
                .filter(f -> f.equalsIgnoreCase(property))
                .findFirst()
                .orElse("id");
    }

    public int calculateOffset(long totalEntities) {
        return Math.max(Math.min(page * size, (int) totalEntities - size), 0);
    }

    public Pageable getPageable() {
        return PageRequest.of(page, size, sort);
    }
}
