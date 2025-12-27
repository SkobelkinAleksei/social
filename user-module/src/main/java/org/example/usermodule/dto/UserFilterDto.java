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
    private String username;
    private String lastName;
    private String numberPhone;
    private LocalDate birthdayFrom;
    private LocalDate birthdayTo;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private Role role;
}
