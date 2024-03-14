package com.eurovision.sandbox.cities.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class City {

    private long id;
    private String name;

}
