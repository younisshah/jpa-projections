package io.younis.jpa.projections.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String street1;

    private String street2;

    private String city;

    private String state;

    private String country;

    private Integer zipCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    public enum AddressType {
        HOME,
        WORK
    }
}
