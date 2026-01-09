package org.example.usermodule.dto;

import lombok.*;
import org.example.usermodule.entity.enums.Role;

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
    String username;
    String lastName;
    String email;
    String numberPhone;
    LocalDate birthday;
    LocalDateTime timeStamp;
    Role role;
}