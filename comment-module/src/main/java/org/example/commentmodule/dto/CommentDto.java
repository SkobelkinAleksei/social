package org.example.commentmodule.dto;

import lombok.*;
import org.example.commentmodule.entity.CommentEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto implements Serializable {
    Long id;
    Long authorId;
    Long postId;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}