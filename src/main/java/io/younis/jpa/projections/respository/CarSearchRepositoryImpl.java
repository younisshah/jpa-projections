package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.Car;
import io.younis.jpa.projections.entity.CarColor;
import io.younis.jpa.projections.entity.CarSearch;
import io.younis.jpa.projections.model.CarSearchCommand;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CarSearchRepositoryImpl implements CarSearchRepository {

    private EntityManager em;

    public CarSearchRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<CarSearch> findByCarSearchCommand(CarSearchCommand carSearchCommand) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // CarSearch is not a managed entity.
        // Similar to a JPA projection
        CriteriaQuery<CarSearch> carCriteriaQuery = cb.createQuery(CarSearch.class);

        // Tell Hibernate we are using table `car` or a managed entity `Car`
        Root<Car> carRoot = carCriteriaQuery.from(Car.class);
        // Tell Hibernate we are using table `car_color` or a managed entity `CarColor`
        Root<CarColor> carColorRoot = carCriteriaQuery.from(CarColor.class);

        // just query aliases
        // no mandatory - makes the queries more readable
        carRoot.alias("car");
        carColorRoot.alias("carColor");

        // We got 4 search parameters - car name, car year, car color code, description
        // for each search param we create a Predicate
        List<Predicate> predicates = new ArrayList<>(4);
        if (!StringUtils.isEmpty(carSearchCommand.getCarName())) {
            predicates.add(cb.equal(carRoot.get("name"), carSearchCommand.getCarName()));
        }

        if (!StringUtils.isEmpty(carSearchCommand.getColorCode())) {
            predicates.add(cb.equal(carRoot.get("colorCode"), carSearchCommand.getColorCode()));
        }

        if (!StringUtils.isEmpty(carSearchCommand.getYear())) {
            predicates.add(cb.equal(carRoot.get("manufacture_year"), carSearchCommand.getYear()));
        }

        if (!StringUtils.isEmpty(carSearchCommand.getDesc())) {
            predicates.add(cb.equal(carRoot.get("description"), carSearchCommand.getDesc()));
        }

        // Join two tables
        predicates.add(cb.equal(carRoot.get("colorCodeId"), carColorRoot.get("id")));

        // add all predicates - essentially the where clause
        carCriteriaQuery.where(predicates.toArray(new Predicate[0]));

        // select what you wanna get from the query
        carCriteriaQuery.select(cb.construct(CarSearch.class,
                carRoot.get("name"),
                carColorRoot.get("colorCode"),
                carColorRoot.get("color"),
                carRoot.get("desc"),
                carRoot.get("year")
        ));

        return em.createQuery(carCriteriaQuery).getResultList();
    }
}
