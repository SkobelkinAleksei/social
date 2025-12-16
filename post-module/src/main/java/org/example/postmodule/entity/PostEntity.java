package org.example.postmodule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.postmodule.dto.ModerationStatusPost;
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

    @ElementCollection
    @CollectionTable(
            name = "post_views",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "author_id")
    private Set<Long> viewSet = new HashSet<>();

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<LikeEntity> likeSet = new HashSet<>();
//
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<CommentEntity> commentTable = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ModerationStatusPost statusPost;
}
