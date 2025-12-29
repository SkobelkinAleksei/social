package org.example.friendmodule.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FriendFullDto implements Serializable {
    Long id;
    Long requesterId;
    Long addresseeId;
    LocalDateTime requested_at;
    LocalDateTime respondedAt;
    LocalDateTime updatedAt;
}