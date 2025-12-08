package org.example.usermodule.utils;

import jakarta.persistence.criteria.Predicate;
import org.example.usermodule.dto.UserFilterDto;
import org.example.usermodule.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<UserEntity> filter(UserFilterDto userFilterDto) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (userFilterDto.getUsername() != null) {
                predicates.add(cb.like(cb.lower(root.get("username")),
                        "%" + userFilterDto.getUsername().toLowerCase() + "%"));
            }

            if (userFilterDto.getLastName() != null) {
                predicates.add(cb.like(cb.lower(root.get("lastName")),
                        "%" + userFilterDto.getLastName().toLowerCase() + "%"));
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

            if (userFilterDto.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("timeStamp"), userFilterDto.getCreatedFrom()));
            }

            if (userFilterDto.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("timeStamp"), userFilterDto.getCreatedTo()));
            }

            if (userFilterDto.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), userFilterDto.getRole()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
