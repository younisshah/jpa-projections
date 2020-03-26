package io.younis.jpa.projections.respository;

import io.younis.jpa.projections.entity.Car;
import io.younis.jpa.projections.entity.CarColor;
import io.younis.jpa.projections.entity.CarRow;
import io.younis.jpa.projections.model.CarSearchCriteria;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CarSearchRepositoryImpl implements CarSearchRepository {

    private EntityManager em;

    public CarSearchRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    public static <T> List<T> map(Class<T> type, List<Object[]> records) {
        List<T> result = new LinkedList<>();
        for (Object[] record : records) {
            result.add(map(type, record));
        }
        return result;
    }

    public static <T> List<T> getResultList(Query query, Class<T> type) {
        @SuppressWarnings("unchecked")
        List<Object[]> records = query.getResultList();
        return map(type, records);
    }

    public static <T> T map(Class<T> type, Object[] tuple) {
        List<Class<?>> tupleTypes = new ArrayList<>();
        for (Object field : tuple) {
            tupleTypes.add(field.getClass());
        }
        try {
            Constructor<T> ctor = type.getConstructor(tupleTypes.toArray(new Class<?>[tuple.length]));
            return ctor.newInstance(tuple);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CarRow> findByCarSearchCommand(CarSearchCriteria carSearchCriteria) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // CarSearch is not a managed entity.
        // Similar to a JPA projection
        CriteriaQuery<CarRow> carCriteriaQuery = cb.createQuery(CarRow.class);

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
        if (!StringUtils.isEmpty(carSearchCriteria.getCarName())) {
            predicates.add(cb.equal(carRoot.get("name"), carSearchCriteria.getCarName()));
        }

        if (!StringUtils.isEmpty(carSearchCriteria.getColorCode())) {
            predicates.add(cb.equal(carColorRoot.get("colorCode"), carSearchCriteria.getColorCode()));
        }

        if (!StringUtils.isEmpty(carSearchCriteria.getYear())) {
            predicates.add(cb.equal(carRoot.get("manufacture_year"), carSearchCriteria.getYear()));
        }

        if (!StringUtils.isEmpty(carSearchCriteria.getDesc())) {
            predicates.add(cb.equal(carRoot.get("description"), carSearchCriteria.getDesc()));
        }

        // Join two tables
        predicates.add(cb.equal(carRoot.get("colorCodeId"), carColorRoot.get("id")));

        //carRoot.join("carColor", JoinType.INNER);

        // add all predicates - essentially the where clause
        carCriteriaQuery.where(predicates.toArray(new Predicate[0]));

        // select what you wanna get from the query
        carCriteriaQuery.select(cb.construct(CarRow.class,
                carRoot.get("name"),
                carColorRoot.get("colorCode"),
                carColorRoot.get("color"),
                carRoot.get("desc"),
                carRoot.get("year")
        ));

        return em.createQuery(carCriteriaQuery).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    /*
     * Because this is using a deprecated API, shouldn't use!
     */
    public List<CarRow> findByColorCode(String colorCode) {
        return em.createNativeQuery("select" +
                " c.NAME as \"carName\"," + // double quoting the alias is mandatory
                " cc.COLOR_CODE as \"colorCode\"," + // double quoting the alias is mandatory
                " cc.COLOR as \"color\"," + // double quoting the alias is mandatory
                " c.DESCRIPTION as \"desc\"," + // double quoting the alias is mandatory
                " c.MANUFACTURE_YEAR as \"year\"" + // double quoting the alias is mandatory
                " from CAR c" +
                " INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID" +
                " WHERE CC.COLOR_CODE=:colorCode").setParameter("colorCode", colorCode)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(CarRow.class)) // will be removed in Hibernate 6
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    /*
     * Very raw and error prone!
     */
    public List<CarRow> findByColorCodeRaw(String colorCode) {

        Query nativeQuery = em.createNativeQuery("select" +
                " c.NAME," +
                " cc.COLOR_CODE," +
                " cc.COLOR," +
                " c.DESCRIPTION," +
                " c.MANUFACTURE_YEAR" +
                " from CAR as c" +
                " INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID" +
                " WHERE CC.COLOR_CODE = :colorCode")
                .setParameter("colorCode", colorCode);

        List<Object[]> resultList = nativeQuery.getResultList();

        return resultList.stream().map(row ->
                CarRow.builder()
                        .carName((String) row[0])
                        .colorCode((String) row[1])
                        .color((String) row[2])
                        .desc((String) row[3])
                        .year((String) row[4])
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CarRow> findByColorCodeConstructorMapping(String colorCode) {

        return em.createNativeQuery("select" +
                " c.NAME as \"carName\"," + // double quoting the alias is mandatory
                " cc.COLOR_CODE as \"colorCode\"," + // double quoting the alias is mandatory
                " cc.COLOR as \"color\"," + // double quoting the alias is mandatory
                " c.DESCRIPTION as \"desc\"," + // double quoting the alias is mandatory
                " c.MANUFACTURE_YEAR as \"year\"" + // double quoting the alias is mandatory
                " from CAR c" +
                " INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID" +
                " WHERE CC.COLOR_CODE=:colorCode", "CarSearchResult")
                .setParameter("colorCode", colorCode)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CarRow> findByColorCodeConstructorMappingXml(String colorCode) {
        return em.createNativeQuery("select" +
                " c.NAME as \"carName\"," + // double quoting the alias is mandatory
                " cc.COLOR_CODE as \"colorCode\"," + // double quoting the alias is mandatory
                " cc.COLOR as \"color\"," + // double quoting the alias is mandatory
                " c.DESCRIPTION as \"desc\"," + // double quoting the alias is mandatory
                " c.MANUFACTURE_YEAR as \"year\"" + // double quoting the alias is mandatory
                " from CAR c" +
                " INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID" +
                " WHERE CC.COLOR_CODE=:colorCode", "CarSearchXml")
                .setParameter("colorCode", colorCode)
                .getResultList();
    }

    public List<CarRow> _findByColorCodeConvenienceMap(CarSearchCriteria carSearchCriteria) {

        StringBuilder queryBuilder = new StringBuilder()
                .append("select")
                .append(" c.NAME,")
                .append(" cc.COLOR_CODE,")
                .append(" cc.COLOR,")
                .append(" c.DESCRIPTION,")
                .append(" c.MANUFACTURE_YEAR")
                .append(" from CAR as c")
                .append(" INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID WHERE 1=1");

        // queryParams represents the where clause key=value pairs
        Map<String, Object> queryParams = new LinkedHashMap<>(); // order matters here. hence LinkedHashMap
        if (!StringUtils.isEmpty(carSearchCriteria.getCarName())) {
            queryParams.put("C.name", carSearchCriteria.getCarName()); // WHERE C.name = "VALUE"
        }
        if (!StringUtils.isEmpty(carSearchCriteria.getColorCode())) {
            queryParams.put("CC.COLOR_CODE", carSearchCriteria.getColorCode()); // WHERE CC.COLOR_CODE = "VALUE"
        }
        if (!StringUtils.isEmpty(carSearchCriteria.getYear())) {
            queryParams.put("C.manufacture_year", carSearchCriteria.getYear()); // WHERE C.MANUFACTURE_YEAR = "VALUE"
        }
        if (!StringUtils.isEmpty(carSearchCriteria.getDesc())) {
            queryParams.put("C.description", carSearchCriteria.getDesc()); // WHERE C.DESCRIPTION = "VALUE"
        }

        // tracks if we need to add "AND" or "WHERE"
        boolean whereClauseStarted = false;
        for (String key : queryParams.keySet()) {
            if (!whereClauseStarted) {
                queryBuilder.append(" WHERE ");
                whereClauseStarted = true;
            } else {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(key);
            queryBuilder.append(" = ?"); // the placeholder
        }

        String finalSql = queryBuilder.toString();
        final Query query = em.createNativeQuery(finalSql);

        // tracks the positional placeholder
        int index = 1;
        // always travers entrySet() and NOT keySet().
        // much more efficient
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            query.setParameter(index, entry.getValue());
            index++;
        }
        return getResultList(query, CarRow.class);
    }

    @Override
    public List<CarRow> findByColorCodeConvenienceMap(CarSearchCriteria searchCriteria) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("select")
                .append(" c.NAME,")
                .append(" cc.COLOR_CODE,")
                .append(" cc.COLOR,")
                .append(" c.DESCRIPTION,")
                .append(" c.MANUFACTURE_YEAR")
                .append(" from CAR as c")
                .append(" INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<>();
        if (!StringUtils.isEmpty(searchCriteria.getCarName())) {
            queryBuilder.append("AND C.name = :name");
            parameters.put("name", searchCriteria.getCarName());
        }
        if (!StringUtils.isEmpty(searchCriteria.getColorCode())) {
            queryBuilder.append("AND CC.COLOR_CODE = :colorCode");
            parameters.put("colorCode", searchCriteria.getCarName());
        }
        if (!StringUtils.isEmpty(searchCriteria.getYear())) {
            queryBuilder.append("AND C.manufacture_year = :year");
            parameters.put("year", searchCriteria.getCarName());
        }
        if (!StringUtils.isEmpty(searchCriteria.getDesc())) {
            queryBuilder.append("AND C.description = :description");
            parameters.put("description", searchCriteria.getCarName());
        }

        final Query query = em.createNativeQuery(queryBuilder.toString());
        parameters.forEach(query::setParameter);
        return getResultList(query, CarRow.class);
    }
}
