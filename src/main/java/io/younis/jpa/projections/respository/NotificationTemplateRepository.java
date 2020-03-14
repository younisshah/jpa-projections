package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.join.NotificationTemplate;
import io.younis.jpa.projections.entity.join.SmsTemplateView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {

    @Query(value = "SELECT NTEMPLATES.en_title as enTitle," +
            " NTEMPLATES.id_title as idTitle," +
            " NTEMPLATES.en_content as enContent," +
            " NTEMPLATES.id_content as idContent from events as E" +
            " INNER JOIN event_types as ETYPES" +
            " ON E.event_code=ETYPES.event_code" +
            " INNER JOIN notification_templates as NTEMPLATES" +
            " ON ETYPES.event_type_id=NTEMPLATES.event_type_id" +
            " WHERE E.event_code=:eventCode", nativeQuery = true)
    SmsTemplateView getSmsTemplateByEventCode(@Param("eventCode") String eventCode);
}
