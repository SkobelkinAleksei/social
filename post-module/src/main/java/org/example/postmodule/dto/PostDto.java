package org.example.postmodule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PostDto implements Serializable {

    Long postId;

    @NotNull(message = "Пользователь должен быть указан.")
    Long authorId;

    @NotBlank(message = "Содержание контента не может быть пустым или состоять только из пробелов.")
    @Size(min = 5, max = 3000, message = "Содержание контента не может быть менее 5 и более 3000 символов.")
    String content;

    @NotNull(message = "Просмотры не могут быть Null")
    Set<Long> viewSet;

    // Удалить createdAt
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime publishAt;

    ModerationStatusPost statusPost;

//    @NotNull(message = "Лайки не могут быть Null")
//    Set<Long> likeSet;


//    @NotNull(message = "Комментарии не могут быть Null")
//    Set<CommentDto> commentTable;
}