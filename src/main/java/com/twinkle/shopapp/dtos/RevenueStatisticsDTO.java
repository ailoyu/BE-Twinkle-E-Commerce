package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueStatisticsDTO {
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;
    private double value1;
    private double value2;
    private double value3;
    private double value4;
    private int value5;

}
