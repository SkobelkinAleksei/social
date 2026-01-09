package org.example.common.dto.user;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    Long userId;
    String firstName;
    String lastName;
    String email;
    String numberPhone;
    LocalDate birthday;
    List<?> authorities;
}