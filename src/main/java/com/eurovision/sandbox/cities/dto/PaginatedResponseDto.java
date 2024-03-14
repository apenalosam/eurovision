package com.eurovision.sandbox.cities.dto;

import com.eurovision.sandbox.cities.domain.model.City;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedResponseDto {

    private List<City> content;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private int size;
    //page number
    private int number;

}
