package org.example.common.dto.user;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto implements Serializable {
    Long id;
    String firstName;
    String lastName;
    String email;
    String numberPhone;
    LocalDate birthday;
    LocalDateTime timeStamp;
    Role role;
}