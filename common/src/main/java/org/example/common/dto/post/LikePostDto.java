package org.example.common.dto.post;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LikePostDto implements Serializable {
    Long authorId;
    LocalDateTime createdAt;
}