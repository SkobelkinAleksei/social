package org.example.postmodule.dto;

import lombok.*;
import org.example.postmodule.entity.ModerationStatusPost;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostFullDto implements Serializable {
    Long id;
    Long authorId;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime publishAt;
    Set<Long> viewSet;
    ModerationStatusPost statusPost;
}