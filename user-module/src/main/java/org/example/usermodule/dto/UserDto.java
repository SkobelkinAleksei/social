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
    String username;
    String lastName;
    String email;
    String numberPhone;
    LocalDate birthday;
}