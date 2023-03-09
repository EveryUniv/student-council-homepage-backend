package com.dku.council.domain.post.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DummyPage<T> implements Page<T> {

    private final List<T> content = new ArrayList<>();
    private final int pageSize;

    public DummyPage(List<T> content) {
        this.pageSize = content.size();
        this.content.addAll(content);
    }

    public DummyPage(List<T> content, int pageSize) {
        this.pageSize = pageSize;
        this.content.addAll(content);
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) content.size() / pageSize);
    }

    @Override
    public long getTotalElements() {
        return content.size();
    }

    @Override
    public int getNumber() {
        return 0;
    }

    @Override
    public int getSize() {
        return pageSize;
    }

    @Override
    public int getNumberOfElements() {
        return 0;
    }

    @NotNull
    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    @NotNull
    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public boolean isFirst() {
        return getNumber() == 0;
    }

    @Override
    public boolean isLast() {
        return getNumber() == getTotalPages() - 1;
    }

    @Override
    public boolean hasNext() {
        return getNumber() < getTotalPages() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    @NotNull
    @Override
    public Pageable nextPageable() {
        return Pageable.unpaged();
    }

    @NotNull
    @Override
    public Pageable previousPageable() {
        return Pageable.unpaged();
    }

    @NotNull
    @Override
    public <U> Page<U> map(@NotNull Function<? super T, ? extends U> converter) {
        List<U> mappedList = content.stream()
                .map(converter)
                .collect(Collectors.toList());
        return new DummyPage<>(mappedList, pageSize);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
