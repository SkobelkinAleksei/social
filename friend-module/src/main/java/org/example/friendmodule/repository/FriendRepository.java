package org.example.friendmodule.repository;

import org.example.friendmodule.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    @Query("""
    SELECT fe
    FROM FriendEntity fe
    WHERE (fe.userId1 = :userId1 AND fe.userId2 = :userId2)
       OR (fe.userId1 = :userId2 AND fe.userId2 = :userId1)
    ORDER BY fe.id
    LIMIT 1
""")
    Optional<FriendEntity> findEntityByUserId1AndUserId2(Long userId1, Long userId2);

    @Query("""
        SELECT fe
        FROM FriendEntity fe
        WHERE fe.userId1= :userId OR fe.userId2= :userId
     """)
    List<FriendEntity> findAllByUserId(Long userId);
}