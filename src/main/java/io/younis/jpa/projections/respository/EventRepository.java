package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.join.Event;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, String> {

}
