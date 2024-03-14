package com.eurovision.sandbox.cities.infrastructure.db.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "cities")
public class CityEntity {

    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

}
