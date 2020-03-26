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
    private String carName;
    private String colorCode;
    private String year;
    private String desc;
    private Integer pageSize;
    private Integer pageNumber;
    private String sortBy;
    private String sortType;
}
