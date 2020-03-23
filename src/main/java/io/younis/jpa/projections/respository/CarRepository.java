package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, CarSearchRepository {
}
