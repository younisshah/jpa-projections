package io.younis.jpa.projections.entity.join;

import io.younis.jpa.projections.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "events")
public class Event extends BaseEntity {

    @Id
    private String eventCode;

    private String serviceCode;

    private String eventName;
}
