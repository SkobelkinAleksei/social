package org.example.postmodule.util;

import org.example.postmodule.entity.ModerationStatusPost;
import org.example.postmodule.entity.PostEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PostStatusSpecification {

    public static Specification<PostEntity> filter(List<ModerationStatusPost> moderationStatus) {
        return (root, query, cb) -> {

            if (moderationStatus == null || moderationStatus.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("statusPost").in(moderationStatus);
        };
    }

    public static Specification<PostEntity> byAuthor(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("authorId"), authorId);
    }
}
