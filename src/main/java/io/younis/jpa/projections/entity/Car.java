package io.younis.jpa.projections.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Basic
    @Column(name = "MANUFACTURE_YEAR")
    private String year;

    @Basic
    @Column(name = "DESCRIPTION")
    private String desc;
    private Long colorCodeId;
    private String manufacturer;
}
