package org.example.postmodule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "publish_at")
    private LocalDateTime publishAt;

    @ElementCollection
    @CollectionTable(
            name = "post_views",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "user_id")
    private Set<Long> viewSet;

    @Column(name = "status_post", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerationStatusPost statusPost;
}
