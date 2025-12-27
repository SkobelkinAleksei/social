package org.example.likepostmodule.dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LikePostDto implements Serializable {
    Long authorId;
}