package io.younis.jpa.projections.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSearchCommand {
    private String carName; // Table: car
    private String colorCode; // Table: car
    private String year; // Table: car
    private String desc; // Table: car
}
