package org.example.common.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostDto implements Serializable {
    Long postId;
    Long authorId;
    String content;
    Set<Long> viewSet;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime publishAt;

    ModerationStatusPost statusPost;

//    @NotNull(message = "Лайки не могут быть Null")
//    Set<Long> likeSet;


//    @NotNull(message = "Комментарии не могут быть Null")
//    Set<CommentDto> commentTable;
}