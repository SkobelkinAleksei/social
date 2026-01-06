package org.example.usermodule.utils;

import jakarta.persistence.criteria.Predicate;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.dto.UserFilterDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<UserEntity> filter(UserFilterDto userFilterDto) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (userFilterDto.getFirstName() != null && !userFilterDto.getFirstName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("firstName")),
                        "%" + userFilterDto.getFirstName().toLowerCase().trim() + "%"));
            }

            if (userFilterDto.getLastName() != null && !userFilterDto.getLastName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("lastName")),
                        "%" + userFilterDto.getLastName().toLowerCase().trim() + "%"));
            }

            if (userFilterDto.getNumberPhone() != null) {
                predicates.add(cb.equal(root.get("numberPhone"), userFilterDto.getNumberPhone()));
            }

            if (userFilterDto.getBirthdayFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("birthday"), userFilterDto.getBirthdayFrom()));
            }
            if (userFilterDto.getBirthdayTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("birthday"), userFilterDto.getBirthdayTo()));
            }

            if (userFilterDto.getTimeStamp() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("timeStamp"), userFilterDto.getTimeStamp()));
            }

            if (userFilterDto.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), userFilterDto.getRole()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
