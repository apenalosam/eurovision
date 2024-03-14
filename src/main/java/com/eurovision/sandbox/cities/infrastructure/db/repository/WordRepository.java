package com.eurovision.sandbox.cities.infrastructure.db.repository;

import com.eurovision.sandbox.cities.infrastructure.db.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<WordEntity, Integer> {

    List<WordEntity> findAll();

    @Query(value = "select subquery.wordName from " +
            "(" +
                "select w.name as wordName, " +
                "char_length(w.name) as length from word w" +
            ") subquery " +
            "where subquery.length = ?1", nativeQuery = true)
    List<String> findNumberedLetteredWords(int length);

}
