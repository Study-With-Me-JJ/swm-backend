package com.jj.swm.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class PageResponse<D> {
    private int numberOfElements;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private List<D> data;

    public static <E, D> PageResponse<D> of(Page<E> entity, Function<E, D> makeDto) {
        List<D> dto = convertToDto(entity, makeDto);

        return new PageResponse<>(
                entity.getNumberOfElements(),
                entity.getTotalPages(),
                entity.getTotalElements(),
                entity.hasNext(),
                dto
        );
    }

    public static <D> PageResponse<D> of(List<D> dto, boolean hasNext) {
        return new PageResponse<>(
                dto.size(),
                -1,
                -1,
                hasNext,
                dto
        );
    }

    public static <D> PageResponse<D> of(
            int numberOfElements,
            int totalPages,
            long totalElements,
            boolean hasNext,
            List<D> dto
    ) {
        return new PageResponse<>(
                numberOfElements,
                totalPages,
                totalElements,
                hasNext,
                dto
        );

    }

    private static <E, D> List<D> convertToDto(Page<E> entity, Function<E, D> makeDto) {
        return entity.getContent()
                .stream()
                .map(makeDto)
                .toList();
    }
}
