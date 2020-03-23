package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.CarSearch;
import io.younis.jpa.projections.model.CarSearchCommand;

import java.util.List;

public interface CarSearchRepository {
    List<CarSearch> findByCarSearchCommand(CarSearchCommand carSearchCommand);
}
