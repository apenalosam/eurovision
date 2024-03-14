package com.eurovision.sandbox.cities.dto;

import com.eurovision.sandbox.cities.domain.model.City;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PermutableCityResponseDto {

    private City city;
    private List<String> wordsInDictionary;

}
