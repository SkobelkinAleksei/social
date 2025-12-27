package org.example.likepostmodule.repository;

import org.example.likepostmodule.entity.LikePostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikePostRepository extends JpaRepository<LikePostEntity, Long> {
    Optional<LikePostEntity> findByPostIdAndAuthorId(Long postId, Long authorId);

    @Query("""
    SELECT lp
    FROM LikePostEntity lp
    WHERE lp.postId = :postId
        AND lp.likeStatus = 'ACTIVE'
""")
    List<LikePostEntity> findAllActiveByPostId(Long postId);
}