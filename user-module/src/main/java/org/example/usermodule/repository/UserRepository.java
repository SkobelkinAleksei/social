package org.example.usermodule.repository;

import org.example.usermodule.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends
        JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity>
{
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByNumberPhone(String numberPhone);
}
