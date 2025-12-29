package org.example.friendmodule.repository;

import org.example.friendmodule.entity.FriendRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends
        JpaRepository<FriendRequestEntity, Long>,
        JpaSpecificationExecutor<FriendRequestEntity>
{
    @Query("""
        select fr from FriendRequestEntity fr
        where (fr.requesterId = :user1 and fr.addresseeId = :user2)
           or (fr.requesterId = :user2 and fr.addresseeId = :user1)
    """)
    Optional<FriendRequestEntity> findBetweenUsers(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

    @Query("""
        select fr from FriendRequestEntity fr
        where (fr.requesterId = :requesterId and fr.addresseeId = :addresseeId)
    """)
    FriendRequestEntity findByRequesterIdAndAddresseeId(
            @Param("requesterId") Long requesterId,
            @Param("addresseeId") Long addresseeId
    );
}