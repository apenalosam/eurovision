package com.eurovision.sandbox.cities.domain.component;

import com.eurovision.sandbox.cities.dto.PaginatedResponseDto;
import com.eurovision.sandbox.cities.dto.PermutableCityResponseDto;

public interface CityService {

    /**
     * @param page The page number requested in the pagination
     * @param size The number of elements per page
     * @return PaginatedResponseDto
     */
    PaginatedResponseDto findCitiesWithParams(int page, int size);

    /**
     * @param page The page number requested in the pagination
     * @param size The number of elements per page
     * @return PaginatedResponseDto with list of cities in ascending alphabetical order
     */
    PaginatedResponseDto findCitiesInAlphabeticalOrderAscendant(int page, int size);

    /**
     * Method to find the most permutable city;
     * @return PermutableCityResponseDto
     */
    PermutableCityResponseDto findMostPermutableCity();

}
