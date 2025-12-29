package org.example.friendmodule.dto;

import lombok.*;
import org.example.friendmodule.entity.FriendRequestStatus;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto implements Serializable {
    Long addresseeId;
    FriendRequestStatus status;
}