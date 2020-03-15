package io.younis.jpa.projections.entity.specification;

import io.younis.jpa.projections.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.text.MessageFormat;
import java.util.Arrays;

public final class UserSpecifications {

    public static Specification<User> firstNameContains(String expression) {
        return (root, query, builder) -> builder.like(root.get("firstName"), contains(expression));
    }

    public static Specification<User> lastNameContains(String expression) {
        return (root, query, builder) -> builder.like(root.get("lastName"), contains(expression));
    }

    public static Specification<User> lastNameIn(String... values) {
        return (root, query, builder) -> builder.or(
                Arrays.stream(values)
                        .map(v -> builder.equal(root.get("lastName"), v))
                        .toArray(Predicate[]::new)
        );
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }
}
