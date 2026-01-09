package org.example.usermodule.dto;

import lombok.*;
import org.example.usermodule.entity.enums.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDto {
    private String firstName;
    private String lastName;
    private String numberPhone;
    private LocalDate timeStamp;
    private Role role;
    private LocalDate birthdayFrom;
    private LocalDate birthdayTo;
}
