package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.Customer;
import io.younis.jpa.projections.entity.NameEmail;
import io.younis.jpa.projections.entity.NameOnly;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    @Query(value = "SELECT first_name as firstName, last_name as lastName FROM customer WHERE email=:email", nativeQuery = true)
    NameOnly findCustomerNameByEmail(@Param("email") String email);

    @Query(value = "SELECT new io.younis.jpa.projections.entity.NameEmail(c.firstName, c.email) from Customer c where c.id=:customerId")
    NameEmail findNameEmailById(@Param("customerId") Long id);
}
