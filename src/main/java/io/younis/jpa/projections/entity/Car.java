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
@SqlResultSetMapping(
        name = "CarSearchResult",
        classes = @ConstructorResult(
                targetClass = CarSearch.class,
                columns = {
                        @ColumnResult(name = "carName"),
                        @ColumnResult(name = "colorCode"),
                        @ColumnResult(name = "color"),
                        @ColumnResult(name = "desc"),
                        @ColumnResult(name = "year")
                }
        )
)
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

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colorCodeId", insertable = false, updatable = false, referencedColumnName = "id")
    private CarColor carColor;*/
}
