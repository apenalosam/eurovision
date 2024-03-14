package com.eurovision.sandbox.cities.rest.controller;

import com.eurovision.sandbox.cities.domain.component.CityService;
import com.eurovision.sandbox.cities.dto.PaginatedResponseDto;
import com.eurovision.sandbox.cities.dto.PermutableCityResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CitiesController {

    private final CityService cityService;

    /**
     * @param page The page number requested in the pagination
     * @param size The number of elements per page
     * @return: ResponseEntity with PaginatedResponseDto as its body
     */
    @GetMapping(value = "/queryByPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedResponseDto> queryCitiesByPage(@RequestParam int page, @RequestParam int size) {
        PaginatedResponseDto response = cityService.findCitiesInAlphabeticalOrderAscendant(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for exercise b of the test
     * @return ResponseEntity with body containing the most permutable city and all its permutations
     */
    @GetMapping(value = "/mostPermutableCity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermutableCityResponseDto> findMostPermutableCity() {
        PermutableCityResponseDto response = cityService.findMostPermutableCity();
        return ResponseEntity.ok(response);
    }

}
