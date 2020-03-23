package io.younis.jpa.projections.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSearch {
    private String carName;
    private String colorCode;
    private String color;
    private String desc;
    private String year;
}
