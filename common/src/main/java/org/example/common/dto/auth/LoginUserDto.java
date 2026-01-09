package org.example.common.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {

    @Email
    @Size(max = 100, message = "Email не может быть длиннее 100 символов.")
    String email;

    @NotBlank(message = "Пароль не может быть пустым.")
    String password;
}
