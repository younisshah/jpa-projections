package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.join.EventType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTypeRepository extends PagingAndSortingRepository<EventType, String> {

}
