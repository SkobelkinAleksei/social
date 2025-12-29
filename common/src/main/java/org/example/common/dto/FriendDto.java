package org.example.common.dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto implements Serializable {
    Long addresseeId;
}