package com.eurovision.sandbox.cities.infrastructure.db.repository;

import com.eurovision.sandbox.cities.infrastructure.db.entity.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitiesRepository extends JpaRepository<CityEntity, Integer> {

    Page<CityEntity> findAllByOrderByNameAsc(Pageable pageable);

    @Query(
            value = "select subquery.id as id, subquery.cityName as name from " +
                    "(" +
                        "select c.name as cityName, " +
                        "c.id as id, " +
                        "char_length(c.name) as length " +
                        "from cities c" +
                    ") subquery " +
                    "where subquery.length = 7",
            nativeQuery = true
    )
    List<CityEntity> findCitiesWithSevenLetters();

}
