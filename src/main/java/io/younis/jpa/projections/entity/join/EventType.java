package io.younis.jpa.projections.entity.join;

import io.younis.jpa.projections.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "event_types")
public class EventType extends BaseEntity {

    @Id
    private String eventTypeId;

    private String eventTypeName;

    private String eventCode;

    private String eventName;

    @PrePersist
    public void afterSetProperties() {
        this.setEventTypeId(UUID.randomUUID().toString());
    }
}
