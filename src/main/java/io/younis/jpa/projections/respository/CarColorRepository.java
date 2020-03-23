package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.CarColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarColorRepository extends JpaRepository<CarColor, Long> {
}
