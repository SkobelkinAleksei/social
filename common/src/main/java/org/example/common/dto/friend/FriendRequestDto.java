package org.example.common.dto.friend;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto implements Serializable {
    Long id;
    Long requesterId;
    Long addresseeId;
    FriendRequestStatus status;
}