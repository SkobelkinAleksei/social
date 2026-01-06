package org.example.postmodule.repository;

import org.example.postmodule.entity.ModerationStatusPost;
import org.example.postmodule.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    List<PostEntity> findAllByAuthorId(Long authorId);

    @Query("""
        SELECT pe
        FROM PostEntity pe
        WHERE pe.statusPost = :statusPost
""")
    Page<PostEntity> findAllByStatusPost(ModerationStatusPost statusPost, Pageable pageable);
}
