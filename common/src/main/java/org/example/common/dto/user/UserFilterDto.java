package org.example.common.dto.user;

import lombok.*;

import java.time.LocalDate;

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

