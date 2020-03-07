package io.younis.jpa.projections.respository;

import io.vavr.control.Option;
import io.younis.jpa.projections.entity.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    Option<Customer> findByEmailContaining(String email);

}
