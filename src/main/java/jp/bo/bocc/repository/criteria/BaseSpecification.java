package jp.bo.bocc.repository.criteria;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;

/**
 * @author NguyenThuong on 3/16/2017.
 */
public class BaseSpecification<T> implements Specification<T> {

    private SearchCriteria criteria;

    public BaseSpecification(SearchCriteria searchCriteria) {
        this.criteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase("<>")) {
            return builder.between(
                    root.<LocalDateTime> get(criteria.getKey()), criteria.getStartTime(), criteria.getEndTime());
        }

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThan(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }

        if (criteria.getOperation().equalsIgnoreCase("in")) {
            Expression<?> exp = root.get(criteria.getKey());
            Predicate predicate = exp.in(criteria.getObjectList());
            return predicate;
        }

        if (criteria.getOperation().equalsIgnoreCase("like_in")) {
            Expression<?> exp = root.get(criteria.getKey());
            Predicate[] predicates = new Predicate[criteria.getObjectList().size()];
            Predicate predicate;
            for (Object ob : criteria.getObjectList()) {
                predicate = builder.like(root.<String>get(criteria.getKey()), "%" + ob + "%");
                predicates[criteria.getObjectList().indexOf(ob)] = predicate;
            }
            return builder.or(predicates);
        }

        if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThan(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }

        if (criteria.getOperation().equalsIgnoreCase(">=")) {
            return builder.greaterThanOrEqualTo(
                    root.<LocalDateTime> get(criteria.getKey()), (LocalDateTime) criteria.getValue());
        }

        else if (criteria.getOperation().equalsIgnoreCase("=<")) {
            return builder.lessThanOrEqualTo(
                    root.<LocalDateTime> get(criteria.getKey()), (LocalDateTime) criteria.getValue());
        }

        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }

        if (criteria.getOperation().equalsIgnoreCase("%=")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }

        if (criteria.getOperation().equalsIgnoreCase("!=")) {
            return builder.notEqual(
                    root.<Boolean> get(criteria.getKey()), criteria.getValue()
            );
        }

        if (criteria.getOperation().equalsIgnoreCase("==")) {
            return builder.equal(
                    root.<Boolean> get(criteria.getKey()), criteria.getValue()
            );
        }

        return null;
    }
}
