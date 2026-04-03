package com.zorvyn.assignment.specifications;

import com.zorvyn.assignment.entities.FinancialRecord;
import com.zorvyn.assignment.enums.RecordType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialRecordSpecification {

    public static Specification<FinancialRecord> withFilters(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("category")),
                        category.toLowerCase()
                ));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
