package org.example.commentmodule.repository;

import org.example.commentmodule.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("""
        SELECT ce
        FROM CommentEntity ce
        WHERE ce.postId= :postId
        AND ce.commentStatus= :PUBLISHED
    """)
    List<CommentEntity> findAllByPostIdAndStatusPublished(Long postId);
}