package com.dku.council.debug;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@ToString
public class TestDto {

    @Min(0)
    @Max(10)
    private int age;

    @NotEmpty
    private String name;
}
