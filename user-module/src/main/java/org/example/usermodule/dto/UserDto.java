package org.example.usermodule.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    Long userId;
    String username;
    String lastName;
    String numberPhone;
    LocalDate birthday;
}