package com.eurovision.sandbox.cities.domain.service;

import com.eurovision.sandbox.cities.domain.exception.BadRequestException;
import com.eurovision.sandbox.cities.domain.model.City;
import com.eurovision.sandbox.cities.dto.PaginatedResponseDto;
import com.eurovision.sandbox.cities.dto.PermutableCityResponseDto;
import com.eurovision.sandbox.cities.infrastructure.db.entity.CityEntity;
import com.eurovision.sandbox.cities.infrastructure.db.repository.CitiesRepository;
import com.eurovision.sandbox.cities.infrastructure.db.repository.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @Mock
    private CitiesRepository citiesRepository;

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    CityServiceImpl classToTest;

    @Test
    void findCitiesWithParams() {

        //given
        PaginatedResponseDto expectedResult = PaginatedResponseDto.builder()
                .last(true)
                .totalPages(1)
                .size(3)
                .totalElements(3)
                .number(1)
                .content(List.of(
                        buildModel(1, "first city"),
                        buildModel(2, "second city"),
                        buildModel(3, "third city")
                        ))
                .build();
        when(citiesRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(mockedDbCitiesResult));
        //when
        PaginatedResponseDto actualResult = classToTest.findCitiesWithParams(1, 3);

        //then
        assertSame(expectedResult.getNumber(), actualResult.getNumber());
        assertSame(expectedResult.isLast(), actualResult.isLast());
        assertSame(expectedResult.getTotalPages(), actualResult.getTotalPages());
        assertSame(expectedResult.getTotalElements(), actualResult.getTotalElements());
        assertSame(expectedResult.getSize(), actualResult.getSize());
        assertEquals(expectedResult.getContent(), actualResult.getContent());

    }

    @Test
    void failedValidationForPage() {
        //given
        BadRequestException expectedResponse =
                BadRequestException.of("Page must be at least one", "Pagination should start at 1");
        //then
        BadRequestException actualResponse = assertThrows(BadRequestException.class,
                () -> classToTest.findCitiesWithParams(-1, 4));
        assertEquals(expectedResponse.getHttpStatus(), actualResponse.getHttpStatus());
        assertEquals(expectedResponse.getTitleMessageCode(), actualResponse.getTitleMessageCode());
        assertEquals(expectedResponse.getTypeMessageCode(), actualResponse.getTypeMessageCode());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

    }

    @Test
    void failedValidationForPageExcessNumber() {
        //given
        BadRequestException expectedResponse =
                BadRequestException.of("Not enough pages", "Query results can show up to 1 pages");
        when(citiesRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(mockedDbCitiesResult));
        //then
        BadRequestException actualResponse = assertThrows(BadRequestException.class,
                () -> classToTest.findCitiesWithParams(3, 3));
        assertEquals(expectedResponse.getHttpStatus(), actualResponse.getHttpStatus());
        assertEquals(expectedResponse.getTitleMessageCode(), actualResponse.getTitleMessageCode());
        assertEquals(expectedResponse.getTypeMessageCode(), actualResponse.getTypeMessageCode());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

    }

    @Test
    void failedValidationForNumberOfElements() {
        //given
        BadRequestException expectedResponse =
                BadRequestException.of("Size must be greater than zero", "Query must show at least one element");
        //then
        BadRequestException actualResponse = assertThrows(BadRequestException.class,
                () -> classToTest.findCitiesWithParams(1, 0));
        assertEquals(expectedResponse.getHttpStatus(), actualResponse.getHttpStatus());
        assertEquals(expectedResponse.getTitleMessageCode(), actualResponse.getTitleMessageCode());
        assertEquals(expectedResponse.getTypeMessageCode(), actualResponse.getTypeMessageCode());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

    }

    @Test
    void findCitiesInAlphabeticalOrderAscendant() {

        //given
        PaginatedResponseDto expectedResult = PaginatedResponseDto.builder()
                .last(true)
                .totalPages(1)
                .size(3)
                .totalElements(3)
                .number(1)
                .content(List.of(
                        buildModel(1, "first city"),
                        buildModel(2, "second city"),
                        buildModel(3, "third city")
                ))
                .build();
        when(citiesRepository.findAllByOrderByNameAsc(any(Pageable.class))).thenReturn(new PageImpl<>(mockedDbCitiesResult));
        //when
        PaginatedResponseDto actualResult = classToTest.findCitiesInAlphabeticalOrderAscendant(1, 3);

        //then
        assertSame(expectedResult.getNumber(), actualResult.getNumber());
        assertSame(expectedResult.isLast(), actualResult.isLast());
        assertSame(expectedResult.getTotalPages(), actualResult.getTotalPages());
        assertSame(expectedResult.getTotalElements(), actualResult.getTotalElements());
        assertSame(expectedResult.getSize(), actualResult.getSize());
        assertEquals(expectedResult.getContent(), actualResult.getContent());

    }

    @Test
    void findMostPermutableCity() {

        //given
        PermutableCityResponseDto expectedResult = PermutableCityResponseDto.builder()
                .wordsInDictionary(List.of("brede", "breed", "reebd", "dereb", "breded", "breede", "bdeeer", "beerded"))
                .city(buildModel(3, "deerbed"))
                .build();
        when(citiesRepository.findCitiesWithSevenLetters()).thenReturn(List.of(
                buildEntity(1, "elitted"),
                buildEntity(2, "denzost"),
                buildEntity(3, "deerbed")
        ));
        when(wordRepository.findNumberedLetteredWords(5)).thenReturn(mockedDbFiveLetterWords);
        when(wordRepository.findNumberedLetteredWords(6)).thenReturn(mockedDbSixLetterWords);
        when(wordRepository.findNumberedLetteredWords(7)).thenReturn(mockedDbSevenLetterWords);
        //when
        PermutableCityResponseDto actualResult = classToTest.findMostPermutableCity();
        //then
        assertEquals(expectedResult.getCity(), actualResult.getCity());
        assertTrue(expectedResult.getWordsInDictionary().size() == actualResult.getWordsInDictionary().size()
        && expectedResult.getWordsInDictionary().containsAll(actualResult.getWordsInDictionary())
        && actualResult.getWordsInDictionary().containsAll(expectedResult.getWordsInDictionary()));

    }

    private final List<String> mockedDbFiveLetterWords = List.of(
            "brede", "breed", "reebd", "dereb", "dozen", "zendo", "dozed", "lited"
    );

    private final List<String> mockedDbSixLetterWords = List.of(
            "breded", "breede", "bdeeer", "dozens", "sozend", "tilted"
    );

    private final List<String> mockedDbSevenLetterWords = List.of(
            "beerded", "zosendt", "delitte"
    );

    private final List<CityEntity> mockedDbCitiesResult = List.of(
            buildEntity(1, "first city"),
            buildEntity(2, "second city"),
            buildEntity(3, "third city")
    );

    private CityEntity buildEntity(int id, String name) {
        return CityEntity.builder()
                .id(id)
                .name(name)
                .build();
    }

    private City buildModel(int id, String name) {
        return City.builder()
                .id(id)
                .name(name)
                .build();
    }
}