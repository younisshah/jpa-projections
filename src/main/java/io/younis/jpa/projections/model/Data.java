package io.younis.jpa.projections.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    private String name;
    private String email;
}