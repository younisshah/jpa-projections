package io.younis.jpa.projections.entity.join;

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
@Table(name = "notification_templates")
public class NotificationTemplate {

    @Id
    private String templateId;

    private String eventTypeId;

    private String enTitle;

    private String idTitle;

    private String enContent;

    private String idContent;

    private String image;


    @PrePersist
    public void afterSetProperties() {
        this.setTemplateId(UUID.randomUUID().toString());
    }
}
