package com.eurovision.sandbox.cities.domain.service;

import com.eurovision.sandbox.cities.domain.component.CityService;
import com.eurovision.sandbox.cities.domain.exception.BadRequestException;
import com.eurovision.sandbox.cities.domain.exception.DataProcessingException;
import com.eurovision.sandbox.cities.domain.model.City;
import com.eurovision.sandbox.cities.dto.PaginatedResponseDto;
import com.eurovision.sandbox.cities.dto.PermutableCityResponseDto;
import com.eurovision.sandbox.cities.infrastructure.db.entity.CityEntity;
import com.eurovision.sandbox.cities.infrastructure.db.repository.CitiesRepository;
import com.eurovision.sandbox.cities.infrastructure.db.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CityServiceImpl implements CityService {

    private final CitiesRepository citiesRepository;
    private final WordRepository wordRepository;

    @Override
    public PaginatedResponseDto findCitiesWithParams(int page, int size) {

        validatePageAndSize(page, size);

        final Pageable pageRequest = PageRequest.of(page - 1, size);

        log.info("Fetching all cities from database");
        Page<CityEntity> cityRepositoryResponse = citiesRepository.findAll(pageRequest);

        log.info("Mapping entities to domain models");
        List<City> cities = cityRepositoryResponse.stream().map(this::mapToDto).collect(Collectors.toList());

        log.info(String.format("Mapped %d results. Building response", cities.size()));
        return buildResponse(cities, page, size, cityRepositoryResponse);
    }

    @Override
    public PaginatedResponseDto findCitiesInAlphabeticalOrderAscendant(int page, int size) {

        validatePageAndSize(page, size);

        final Pageable pageRequest = PageRequest.of(page - 1, size);

        log.info("Fetching all cities from database");
        Page<CityEntity> cityRepositoryResponse = citiesRepository.findAllByOrderByNameAsc(pageRequest);

        log.info("Mapping entities to domain models");
        List<City> cities = cityRepositoryResponse.stream().map(this::mapToDto).collect(Collectors.toList());

        log.info(String.format("Mapped %d results. Building response", cities.size()));
        return buildResponse(cities, page, size, cityRepositoryResponse);

    }

    @Override
    public PermutableCityResponseDto findMostPermutableCity() {
        List<CityEntity> citiesWithSevenLetters = citiesRepository.findCitiesWithSevenLetters();
        List<String> possibleWordsFromPermutations = new ArrayList<>();
        City mostPermutableCity = getCityWithMostPermutations(citiesWithSevenLetters, possibleWordsFromPermutations);

        return buildMostPermutableCityResponse(mostPermutableCity, possibleWordsFromPermutations);

    }

    /**
     * Validates that page number and page size are not smaller than possible
     * @param page The page number requested in the pagination
     * @param size The number of elements per page
     */
    private void validatePageAndSize(int page, int size) {

        log.info(String.format("Validating page number: %d and size: %d", page, size));

        if (page < 1) {
            log.error("Page number validation failed: Page number was less than 1");
            throw BadRequestException.of("Page must be at least one", "Pagination should start at 1");
        }

        if (size < 1) {
            log.error("Page size validation failed: Size was less than 1");
            throw BadRequestException.of("Size must be greater than zero", "Query must show at least one element");
        }

    }

    /**
     * Maps a database CityEntity to a domain model City object
     * @param entity CityEntity from database
     * @return City object from domain model
     */
    private City mapToDto(CityEntity entity) {
        return City.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    /**
     * Builds the response object for the response payload
     * @param cities List of City domain model objects
     * @param page Current page of the pagination
     * @param size Number of entries per page
     * @param pageableResponse response from database to know how many entries and pages there are in total
     * @return PaginatedResponseDto object that will be the body of the response entity
     */
    private PaginatedResponseDto buildResponse(List<City> cities, int page, int size,
                                               Page<CityEntity> pageableResponse) {
        int totalPages = pageableResponse.getTotalPages();
        log.info("Validating page number: Checking if requested page is grater than last page");
        if (page > totalPages) {
            log.error(String. format("Validation failed: Requested page was %d, but last page is %d", page,
                    totalPages));
            throw BadRequestException.of("Not enough pages",
                    String.format("Query results can show up to %d pages", totalPages));
        }
        return PaginatedResponseDto.builder()
                .content(cities)
                .totalElements(pageableResponse.getTotalElements())
                .totalPages(totalPages)
                .last(totalPages <= page)
                .size(size)
                .number(page)
                .build();
    }

    /**
     * This method is in charge of finding all possible permutations of each city in the list and comparing them to
     * words in the dictionary to find all matches
     * @param sevenLetterCities List of all cities with seven letters
     * @param possibleWords List to which all word matches with permutations will be added
     * @return City domain model object with most matches to words with permutations from its name
     */
    private City getCityWithMostPermutations(List<CityEntity> sevenLetterCities, List<String> possibleWords) {
        log.info(String.format("checking %d city names", sevenLetterCities.size()));
        //Using atomic reference to be able to manipulate it in lambda expression
        AtomicReference<CityEntity> response = new AtomicReference<>();
        List<String> fiveLetteredWords = wordRepository.findNumberedLetteredWords(5);
        List<String> sixLetteredWords = wordRepository.findNumberedLetteredWords(6);
        List<String> sevenLetteredWords = wordRepository.findNumberedLetteredWords(7);
        sevenLetterCities.forEach( c -> {
            List<String> tmpList = new ArrayList<>();
            //Add all found five-lettered words from the permutations to a temporary list
            log.info(String.format("finding permutations of %d elements for city %s", 5, c.getName()));
            findPermutations(c.getName().trim(), 5, "", tmpList, fiveLetteredWords);
            //Add all found six-lettered words from the permutations to the temporary list
            log.info(String.format("finding permutations of %d elements for city %s", 6, c.getName()));
            findPermutations(c.getName().trim(), 6, "", tmpList, sixLetteredWords);
            //Add all found seven-lettered words from the permutations to the temporary list
            log.info(String.format("finding permutations of %d elements for city %s", 7, c.getName()));
            findPermutations(c.getName().trim(), 7, "", tmpList, sevenLetteredWords);
            //Check if temporary list has more elements than previously saved list. If so, update with new list
            if (tmpList.size() > possibleWords.size()) {
                log.info(String.format("updating response to result with %d words", tmpList.size()));
                possibleWords.clear();
                possibleWords.addAll(tmpList);
                response.set(c);
            }

        });

        if (response.get() == null) {
            throw DataProcessingException.of("An error occurred while calculating permutations and response was null");
        }

        return mapToDto(response.get());
    }

    /**
     * Builds the response object for the response payload
     * @param mostPermutableCity city with most permutable name
     * @param possibleWords List of all words with matches to the city's permutations
     * @return PermutableCityResponseDto object that will be the body of the response entity
     */
    private PermutableCityResponseDto buildMostPermutableCityResponse(City mostPermutableCity,
                                                                      List<String> possibleWords) {
        return PermutableCityResponseDto.builder()
                .city(mostPermutableCity)
                .wordsInDictionary(possibleWords)
                .build();
    }

    private void findPermutations(String cityName, int numberOfElements, String prefix, List<String> result,
                                  List<String> dictionary) {

        if (prefix.length() == numberOfElements) {
            if (dictionary.stream().anyMatch(prefix::equalsIgnoreCase)
                    && result.stream().noneMatch(prefix::equalsIgnoreCase)) {
                log.info(String.format("Found %s in the dictionary. Adding to the list", prefix.toLowerCase()));
                result.add(prefix.toLowerCase());
            }
        } else {
            for (int i = 0; i < cityName.length(); i++) {
                findPermutations(cityName.substring(0,i)+cityName.substring(i+1),
                        numberOfElements, prefix + cityName.charAt(i), result, dictionary);
            }
        }

    }
}
